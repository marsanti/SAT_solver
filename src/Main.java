import structure.Clause;
import structure.Formula;
import structure.Literal;
import utils.Utils;

public class Main {
    public static void main(String[] args) {
//        Formula formula = new Formula();
//        Clause clause1 = new Clause();
//        clause1.addLiteral(new Literal(1, true));
//        clause1.addLiteral(new Literal(3, false));
//        Clause clause2 = new Clause();
//        clause2.addLiteral(new Literal(2, true));
//        clause2.addLiteral(new Literal(3, true));
//        clause2.addLiteral(new Literal(1, false));
//
//        formula.addClause(clause1);
//        formula.addClause(clause2);
//
//        System.out.println("F = " + formula);
        Formula f = Utils.readFormulaFromFile("tests/test2.cnf");
        CDCL solver = new CDCL(f);
        solver.findModel();
    }
}
