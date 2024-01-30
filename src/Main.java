import structure.*;
import utils.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> files = new ArrayList<>();
        boolean printProofMap = false;
        // statistic variables
        int satProblems = 0;
        int unsatProblems = 0;
        long maxTime = 0;
        long minTime = -1;
        long sumTime = 0;
        int numberOfProblems;

        // check arguments
        if (args.length == 1) {
            if (args[0].equals("-help")) {
                System.out.println("Two usage for this script: \n" +
                        "1. SAT_solver.jar <Strategy> <path_to_cnf>\n" +
                        "2. SAT_solver.jar <Strategy> -F <path_to_folder_with_cnf>\n" +
                        "Other flags:\n" +
                        "-V to print the proofMap\n\n" +
                        "Available strategies: FUIP, TWL");
                return;
            }
        } else if (args.length > 4 || args.length <= 0) {
            System.out.println("Wrong usage, try: SAT_solver.jar -help");
            return;
        }
        // check strategy
        Strategy strategy;
        try {
            strategy = Strategy.valueOf(args[0]);
        } catch (IllegalArgumentException iae) {
            System.out.println(args[0] + " is not implemented yet!\ntry: SAT_solver.jar -help");
            return;
        }
        // check folder flag and add filepath to files
        if (args[1].equals("-F")) {
            try {
                files = Utils.readFilesFromFolder(args[2]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }
        } else {
            files.add(args[1]);
        }
        // check if verbose flag is on
        for(String arg : args) {
            if(arg.equals("-V")) {
                printProofMap = true;
                break;
            }
        }

        numberOfProblems = files.size();
        System.out.println(numberOfProblems + " files detected...\n");
        long startTimeGlobal = System.currentTimeMillis();
        for (String file : files) {
            try {
                System.out.println("solving " + (files.indexOf(file) + 1) + "/" + files.size() + " (" + file + ")");
                StringBuilder output = new StringBuilder();
                Formula f = Utils.readFormulaFromFile(file);
                output.append("output for file: ").append(file).append("\n");
                output.append("Strategy selected: ").append(strategy).append("\n\n");
                output.append(f).append("\n");
                output.append("----------\n");
                long startTime = System.currentTimeMillis();
                CDCL solver = new CDCL(f);
                boolean isSat;
                String errMessage = "";
                try {
                    isSat = solver.findModel(strategy);
                } catch (Exception e) {
                    errMessage = e.getMessage();
                    isSat = false;
                }

                if (isSat) {
                    satProblems++;
                    ArrayList<Literal> model = solver.getModel();
                    output.append("Result: SATISFIABLE\nmodel: ").append(model).append("\nmodel size: ").append(model.size()).append("\n\n");
                } else {
                    unsatProblems++;
                    output.append(errMessage);
                    output.append("proof size: ").append(solver.proofMapper.keySet().size()).append("\n\n");
                }

                output.append("n. decisions: ").append(solver.n_decide_step).append("\n");
                output.append("n. conflicts: ").append(solver.n_conflict).append("\n\n");

                long endTime = System.currentTimeMillis();
                long interval = endTime - startTime;
                
                if(interval > maxTime) {
                    maxTime = interval;
                }
                if(minTime == -1 || minTime > interval) {
                    minTime = interval;
                }
                sumTime += interval;
                
                output.append("Ended in ").append(interval).append("ms").append("\n\n");

                if(!isSat && printProofMap) {
                    output.append("proof map: ").append(solver.proofMapper);
                }

                Utils.saveOutputContent(file, String.valueOf(output));
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        long endTimeGlobal = System.currentTimeMillis();
        System.out.println("\nAll solved in " + ((endTimeGlobal - startTimeGlobal)/1000) + "s.\n");
        System.out.println("--- Statistics on all files ---\n" +
                "SAT problems: " + satProblems + "\n" +
                "UNSAT problems: " + unsatProblems + "\n\n" +
                "Min time: " + minTime + " ms\n" +
                "Max time: " + maxTime + " ms\n" +
                "Average time: " + (sumTime/numberOfProblems) + " ms");
    }
}
