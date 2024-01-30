import structure.Formula;
import utils.*;

public class Main {
    public static void main(String[] args) {
         Formula f = Utils.readFormulaFromFile("tests/lectureTests/unsat3.cnf");
//        Formula f = Utils.readFormulaFromFile("tests/pigeon-7.cnf");
//        Formula f = Utils.readFormulaFromFile("tests/test3.cnf");
//         Formula f = Utils.readFormulaFromFile("tests/hardSat/uf20-01.cnf");
//        Formula f = Utils.readFormulaFromFile("tests/hardUnsat/uuf50-07.cnf");

        try {
//            if (args.length != 2) {
//                throw new Exception("Wrong usage: script <Strategy> <path_to_cnf>");
//            }
            Strategy strategy;
            try {
                strategy = Strategy.valueOf(args[0]);
            } catch (IllegalArgumentException iae) {
                throw new Exception(args[0] + " is not implemented yet!\nAvailable strategies: FUIP, TWL");
            }
            String filePath = args[1];
            String[] filePieces = filePath.split("\\\\");
            String fileName = filePieces[filePieces.length - 1];
//            Formula f = Utils.readFormulaFromFile(args[1]);
            System.out.print("\n");
            System.out.println("Strategy selected: " + strategy);
            System.out.println(f);
            System.out.println("----------");
            long startTime = System.currentTimeMillis();
            CDCL solver = new CDCL(f);
            boolean isSat;
            String errMessage = "";
            try {
                isSat = solver.findModel(strategy);
            } catch (Exception e){
                errMessage = e.getMessage();
                isSat = false;
            }

            if(isSat) {
                System.out.println("Result: SATISFIABLE\nmodel: " + solver.getModel());
            } else {
                System.out.println(errMessage);
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Ended in " + (endTime - startTime) + "ms");
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
