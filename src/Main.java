import structure.Formula;
import utils.*;

public class Main {
    public static void main(String[] args) {
//         Formula f = Utils.readFormulaFromFile("tests/lectureTests/unsat2.cnf");
//        Formula f = Utils.readFormulaFromFile("tests/pigeon-5.cnf");
         Formula f = Utils.readFormulaFromFile("tests/hard/uf20-09.cnf");
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

//            Formula f = Utils.readFormulaFromFile(args[1]);
            System.out.print("\n");
            System.out.println("Strategy selected: " + strategy);
            System.out.println(f);
            System.out.println("----------");
            long startTime = System.currentTimeMillis();
            CDCL solver = new CDCL(f);
            solver.findModel(strategy);
            long endTime = System.currentTimeMillis();
            System.out.println("Ended in " + (endTime - startTime) + "ms");
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
