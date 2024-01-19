import structure.Formula;
import utils.*;

public class Main {
    public static void main(String[] args) {
        // Formula f = Utils.readFormulaFromFile("tests/lectureTests/unsat2.cnf");
        Formula f = Utils.readFormulaFromFile("tests/test1.cnf");
        // Formula f = Utils.readFormulaFromFile("tests/hard/uf20-09.cnf");
        System.out.println(f);
        CDCL solver = new CDCL(f);
        solver.findModel(Strategy.FUIP);
    }
}
