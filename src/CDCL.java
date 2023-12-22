import structure.*;

import java.util.ArrayList;
import java.util.Map;

public class CDCL {
    private Formula formula;
    private ArrayList<Literal> model;
    private ArrayList<Literal> unitClauses;
    private ArrayList<Literal> decidedLiterals;

    public CDCL(Formula f) {
        this.formula = f;
        this.model = new ArrayList<>();
        this.unitClauses = new ArrayList<>();
        this.decidedLiterals = new ArrayList<>();
    }

    private Literal decide() throws Exception {
        if(!this.unitClauses.isEmpty()) {
            Literal unitLit = this.unitClauses.get(0);
            this.decidedLiterals.add(unitLit);
            this.model.add(unitLit);
            this.unitClauses.remove(0);
            return unitLit;
        } else {
            Map<Literal, Integer> occurrences = this.formula.getLiteralOccurrences();
            int sizeModel = this.model.size();
            for(Literal key : occurrences.keySet()) {
                Literal negateKey = new Literal(key.getLit(), !(key.isPositive()));
                if(!(this.decidedLiterals.contains(key)) && !(this.model.contains(negateKey))) {
                    this.decidedLiterals.add(key);
                    this.model.add(key);
                    return key;
                }
            }
            if(sizeModel == this.model.size()) {
                throw new Exception("there are no more literals to decide.");
            }
            return null;
        }
    }

    private void unitPropagate(Literal l) {
        for(Clause c : formula.getClauses()) {
            ArrayList<Literal> undefLit = c.getUndefinedLiterals(this.model);
            if(undefLit.size() == 1) {
                // TODO: add the lit to the model, and map the literal with the clause(the justification)
            }
        }
    }

    private ArrayList<Literal> getUnaryClauses() throws Exception {
        ArrayList<Literal> unitLiterals = new ArrayList<>();
        for(Clause c : this.formula.getClauses()) {
            if (c.getLiterals().size() == 1) {
                Literal l = c.getLiterals().get(0);
                unitLiterals.add(l);
                Literal negL = new Literal(l.getLit(), !(l.isPositive()));
                if(unitLiterals.contains(negL)) {
                    throw new Exception("Conflict: there are two unary clauses that are opposite!");
                }
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
                    Literal negL = new Literal(l.getLit(), !(l.isPositive()));
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
            while(this.model.size() != this.formula.getNumberOfLiterals()) {
                Literal decidedLit = this.decide();
                if(decidedLit != null) {
                    this.unitPropagate(decidedLit);
                }
                Clause conflict = this.checkConflict();
                if(conflict != null) {
                    if(this.decidedLiterals.size() == 0) {
                        throw new Exception("Conflict at level 0, so NOT SAT with model = " + this.model + " with conflict clause: " + conflict);
                    } else {

                    }
                }
            }
            System.out.println("The formula " + this.formula + " is SAT: \nmodel: " + this.model);
            /* END TEST CASE */

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
