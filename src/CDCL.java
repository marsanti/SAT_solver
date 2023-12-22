import structure.*;

import java.util.ArrayList;

public class CDCL {
    private Formula formula;
    private ArrayList<Literal> model;
    private ArrayList<Literal> unitClauses;
    private ArrayList<Literal> decidedLiterals;
    private int currentLevel = 0;

    public CDCL(Formula f) {
        this.formula = f;
        this.model = new ArrayList<>();
        this.unitClauses = new ArrayList<>();
    }

    private void decide() {
        if(!this.unitClauses.isEmpty()) {
            this.decidedLiterals.add(this.unitClauses.get(0));
            this.unitClauses.remove(0);
        } else {

        }
    }

    private void unitPropagate(Literal l) {
        if(!model.contains(l)) {
            for(Clause c : formula.getClauses()) {

            }
        }
    }

    private ArrayList<Literal> getUnaryClauses() {
        ArrayList<Literal> unitLiterals = new ArrayList<>();
        for(Clause c : this.formula.getClauses()) {
            if (c.getLiterals().size() == 1) {
                unitLiterals.add(c.getLiterals().get(0));
            }
        }
        return unitLiterals;
    }

    /**
     * if there is any conflict clause return it
     * @return Conflict clause or null
     */
    private Clause checkConflict() {
        for (Clause c : this.formula.getClauses()) {
            boolean sat = false;
            int negateCounter = 0;
            for (Literal l : c.getLiterals()) {
                if(this.model.contains(l)) {
                    sat = true;
                    break;
                } else {
                    Literal negL = new Literal(l.getLit(), !(l.getPositive()));
                    if(this.model.contains(negL)) {
                        negateCounter++;
                    }
                }
            }
            if(!sat) {
                if(negateCounter == c.getLiterals().size()) {
                    return c;
                }
            }
        }

        return null;
    }

    public void findModel() {
        try {
            this.unitClauses = this.getUnaryClauses();

            /* TEST CASE */
            this.model.add(new Literal(1, true));
            this.model.add(new Literal(2, false));
            Clause conflict = this.checkConflict();
            if(conflict != null && this.currentLevel == 0) {
                throw new Exception("Conflict at level 0, so NOT SAT with model = " + this.model + " with conflict clause: " + conflict);
            }
            /* END TEST CASE */

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
