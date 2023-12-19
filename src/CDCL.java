import structure.*;

import java.util.ArrayList;

public class CDCL {
    private Formula formula;
    private ArrayList<Literal> model;

    public CDCL(Formula f) {
        this.formula = f;
    }

    private void decide() {

    }

    private void unitPropagate(Literal l) {
        if(!model.contains(l)) {
            for(Clause c : formula.getClauses()) {
                // if clauses has not l and there is one undefined literal, then we
            }
        }
    }

    private ArrayList<Literal> checkForUnaryClauses() throws Exception {
        ArrayList<Literal> unitLiterals = new ArrayList<>();
        for(Clause c : formula.getClauses()) {
            if (c.getLiterals().size() == 1) {
                Literal l = c.getLiterals().get(0);
                Literal negateL = new Literal(l.getLit(), !(l.getPositive()));
                if(unitLiterals.contains(negateL)) {
                    throw new Exception("Not satisfiable: 2 unit clauses conflict.");
                }
                unitLiterals.add(l);
            }
        }
        return unitLiterals;
    }

    public ArrayList<Literal> findModel() {
        ArrayList<Literal> unitClauses = null;
        try {
            unitClauses = this.checkForUnaryClauses();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return unitClauses;
    }
}
