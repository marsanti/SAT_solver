import structure.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CDCL {
    private final Formula formula;
    private final ArrayList<Literal> model;
    private final ArrayList<Literal> decidedLiterals;
    private final Map<Literal, Clause> justification;

    public CDCL(Formula f) {
        this.formula = f;
        this.model = new ArrayList<>();
        this.decidedLiterals = new ArrayList<>();
        this.justification = new HashMap<>();
    }

    private Literal decide() throws Exception {
        Map<Literal, Integer> occurrences = this.formula.getLiteralOccurrences();
        int sizeModel = this.model.size();
        for(Literal key : occurrences.keySet()) {
            Literal notKey = key.getNegate();
            if(!(this.decidedLiterals.contains(key)) && !(this.model.contains(notKey)) && !(this.model.contains(key))) {
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

    private boolean unitPropagate() {
        boolean changed = false;
        ArrayList<Literal> model_copy = new ArrayList<>(this.model);
        for(Literal l : model_copy) {
            for(Clause c : this.formula.getClauses()) {
                Literal notL = l.getNegate();
                if(!c.containsLiteral(notL)) continue;
                ArrayList<Literal> undefLitArray = c.getUndefinedLiterals(this.model);
                if(undefLitArray.size() == 1) {
                    Literal undefLit = undefLitArray.get(0);
                    Literal notUndefLit = undefLit.getNegate();
                    if(!(this.model.contains(notUndefLit))) {
                        this.model.add(undefLit);
                        this.justification.put(undefLit, c);
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    private Object twoWatchedLit() {
        int modelSize = this.model.size();

        for(Clause c : this.formula.getClauses()) {
            Object response = c.watchTwoLiterals(this.model);
            if(response instanceof Clause) {
                System.out.println(response + " is a conflict clause");
                return response;
            } else if(response instanceof Literal) {
                this.model.add((Literal) response);
                this.justification.put((Literal) response, c);
            }
        }

        return modelSize != this.model.size();
    }

    private ArrayList<Literal> getUnaryClauses() throws Exception {
        ArrayList<Literal> unitLiterals = new ArrayList<>();
        for(Clause c : this.formula.getClauses()) {
            if (c.getLiterals().size() == 1) {
                Literal l = c.getLiterals().get(0);
                unitLiterals.add(l);
                Literal notL = l.getNegate();
                if(unitLiterals.contains(notL)) {
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
                    Literal notL = l.getNegate();
                    if(this.model.contains(notL)) {
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

    private Clause explain(Clause conflict) {
        for(Literal l : conflict.getLiterals()) {
            Literal notL = l.getNegate();
            if(this.justification.containsKey(notL)) {
                return conflict.getResolvent(this.justification.get(notL));
            }
        }
        return null;
    }

    private void learn(Clause resolvent) {
        this.formula.addClause(resolvent);
    }

    private void backjump(Clause resolvent) {
        Literal lastDecidedLiteral = this.decidedLiterals.get(this.decidedLiterals.size()-1);
        this.decidedLiterals.remove(lastDecidedLiteral);
        int index = this.model.indexOf(lastDecidedLiteral);
        // remove all propagated literals
        for(Literal l : this.model.subList(index, this.model.size())) {
            this.justification.remove(l);
        }
        this.model.subList(index, this.model.size()).clear();
        // add the notDecided to the model and to the justification map
        Literal notDecided = lastDecidedLiteral.getNegate();
        this.model.add(notDecided);
        this.justification.put(notDecided, resolvent);

    }

    public void findModel() {
        try {
            ArrayList<Literal> unitClauses = this.getUnaryClauses();
            // propagate unit Clauses
            for(Literal l : unitClauses) {
                this.model.add(l);
                Clause c = new Clause();
                c.addLiteral(l);
                this.justification.put(l, c);
            }
            boolean sat = false;

            /* TEST CASE */
            while((this.model.size() != this.formula.getNumberOfLiterals()) && !sat) {
//                if(!this.unitPropagate()) {
//                    this.decide();
//                }
                Object response = this.twoWatchedLit();
                if(response instanceof Clause) {
                    Clause conflict = (Clause) response;
                    if(this.decidedLiterals.isEmpty()) {
                        throw new Exception("Conflict at level 0, formula NOT SAT with model = " + this.model + " with conflict clause: " + conflict);
                    } else {
                        Clause resolvent = this.explain(conflict);
                        if(resolvent == null) {
                            throw new Exception("UNSAT for Fail rule: model = " + this.model);
                        }
                        Clause resNeg = resolvent.getNegate();
                        if(formula.containsClause(resNeg)) {
                            throw new Exception("UNSAT for Fail rule: model = " + this.model + " with resolvent: " + resolvent);
                        }
                        this.learn(resolvent);
                        this.backjump(resolvent);
                    }
                } else {
                    boolean res = (boolean) response;
                    if(!res) {
                        this.decide();
                    }

                    sat = this.formula.isSatisfied(this.model);
                }
//                Clause conflict = this.checkConflict();
//                if(conflict != null) {
//                    if(this.decidedLiterals.isEmpty()) {
//                        throw new Exception("Conflict at level 0, formula NOT SAT with model = " + this.model + " with conflict clause: " + conflict);
//                    } else {
//                        Clause resolvent = this.explain(conflict);
//                        if(resolvent == null) {
//                            throw new Exception("UNSAT for Fail rule: model = " + this.model);
//                        }
//                        Clause resNeg = resolvent.getNegate();
//                        if(formula.containsClause(resNeg)) {
//                            throw new Exception("UNSAT for Fail rule: model = " + this.model + " with resolvent: " + resolvent);
//                        }
//                        this.learn(resolvent);
//                        this.backjump(resolvent);
//                    }
//                } else {
//                    sat = this.formula.isSatisfied(this.model);
//                }
            }
            System.out.println("The formula " + this.formula + " is SAT: \nmodel: " + this.model);
            /* END TEST CASE */

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
