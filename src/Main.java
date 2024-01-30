import structure.*;
import utils.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> files = new ArrayList<>();

        // check arguments
        if (args.length == 1) {
            if (args[0].equals("-help")) {
                System.out.println("Two usage for this script: \n" +
                        "1. SAT_solver.jar <Strategy> <path_to_cnf>\n" +
                        "2. SAT_solver.jar <Strategy> -F <path_to_folder_with_cnf>\n" +
                        "Available strategies: FUIP, TWL");
                return;
            }
        } else if (args.length > 3 || args.length <= 0) {
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

        System.out.println(files.size() + " files detected");
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
                    ArrayList<Literal> model = solver.getModel();
                    output.append("Result: SATISFIABLE\nmodel: ").append(model).append("\nmodel size: ").append(model.size()).append("\n\n");
                } else {
                    output.append(errMessage).append("\n\n");
                }

                output.append("decisions: ").append(solver.n_decide_step).append("\n");
                output.append("conflicts: ").append(solver.n_conflict).append("\n\n");

                long endTime = System.currentTimeMillis();
                long interval = endTime - startTime;
                output.append("Ended in ").append(interval).append("ms");
                Utils.saveOutputContent(file, String.valueOf(output));
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}
