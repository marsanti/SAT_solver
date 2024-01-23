import structure.*;
import utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CDCL {
    private final Formula formula;
    private final ArrayList<Literal> model;
    private final ArrayList<Literal> decidedLiterals;
    private final Map<Literal, Clause> justification;
    private final Map<ArrayList<Clause>, Clause> proofMapper;
    private int k;
    private static final int VSIDS_LIMIT_FOR_DECAY = 2;
    private static final double DECAY_CONSTANT = 0.95;

    public CDCL(Formula f) {
        this.formula = f;
        this.model = new ArrayList<>();
        this.decidedLiterals = new ArrayList<>();
        this.justification = new HashMap<>();
        this.proofMapper = new HashMap<>();
        k = 0;
    }
    public ArrayList<Literal> getModel() {
        return model;
    }
    private void decide() throws Exception {
        Map<Literal, Double> VSIDS = this.formula.getVSIDS();
        int sizeModel = this.model.size();
        for(Literal key : VSIDS.keySet()) {
            if(this.decidedLiterals.contains(key) || this.model.contains(key)) continue;
            Literal notKey = key.getNegate();
            if(!(this.model.contains(notKey))) {
                this.decidedLiterals.add(key);
                this.model.add(key);
                return;
            }
        }
        if(sizeModel == this.model.size()) {
            throw new Exception("there are no more literals to decide.");
        }
    }

    private boolean unitPropagate() {
        boolean changed = true;
        int initialModelSize = this.model.size();
        // while model changes
        while(changed) {
            changed = false;
            // for each clause of the formula
            for (Clause c : this.formula.getClauses()) {
                // get undefined literals (L and notL are not in the model)
                ArrayList<Literal> undefLitArray = c.getUndefinedLiterals(this.model);
                // if there is only one undefined Literal then add it to the model and to justification map
                if (undefLitArray.size() == 1) {
                    Literal undefLit = undefLitArray.get(0);
                    this.model.add(undefLit);
                    this.justification.put(undefLit, c);
                    changed = true;
                }
            }
        }
        return initialModelSize != this.model.size();
    }

    private Object twoWatchedLit() {
        int modelSize = this.model.size();
        boolean changed = true;
        while(changed) {
            changed = false;
            for (Clause c : this.formula.getClauses()) {
                Object response = c.watchTwoLiterals(this.model);
                if (response instanceof Clause) {
                    return response;
                } else if (response instanceof Literal) {
                    changed = true;
                    this.model.add((Literal) response);
                    this.justification.put((Literal) response, c);
                }
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
                Clause justClause = this.justification.get(notL);
                Clause resolvent = conflict.getResolvent(justClause);
                ArrayList<Clause> parents = new ArrayList<>();
                parents.add(conflict);
                parents.add(justClause);
                this.proofMapper.put(parents, resolvent);
                return resolvent;
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

    private void FUIP() throws Exception {
        boolean sat = false;
        while((this.model.size() != this.formula.getNumberOfLiterals()) && !sat) {
            if(!this.unitPropagate()) {
                this.decide();
            }
            Clause conflict = this.checkConflict();
            if(conflict != null) {
                this.solveConflict(conflict);
            }
            sat = this.formula.isSatisfied(this.model);
        }
    }

    private void TWL() throws Exception {
        boolean sat = false;
        while((this.model.size() != this.formula.getNumberOfLiterals()) || !sat) {
            Object response = this.twoWatchedLit();
            if(response instanceof Clause) {
                Clause conflict = (Clause) response;
                this.solveConflict(conflict);
            } else {
                boolean res = (boolean) response;
                if(!res) {
                    this.decide();
                }
            }
            sat = this.formula.isSatisfied(this.model);
        }
    }

    private void solveConflict(Clause conflict) throws Exception {
        this.formula.increaseVSIDSCounters(conflict);
        if(k >= VSIDS_LIMIT_FOR_DECAY) {
            this.formula.decayVSIDS(DECAY_CONSTANT);
            k = 0;
        } else {
            k++;
        }
        if(this.decidedLiterals.isEmpty()) {
            throw new Exception("Result: UNSATISFIABLE, conflict at level 0\nmodel: " + this.model +"\nConflict clause: " + conflict);
        } else {
            Clause resolvent = this.explain(conflict);
            if(resolvent == null) {
                throw new Exception("Result: UNSATISFIABLE, Fail rule\nmodel: " + this.model +"\nConflict clause: " + conflict);
            }
            Clause resNeg = resolvent.getNegate();
            if(formula.containsClause(resNeg)) {
                throw new Exception("Result: UNSATISFIABLE, Fail rule (resolvent in formula)\nmodel: " + this.model +"\nResolvent: " + resolvent);
            }
            this.learn(resolvent);
            this.backjump(resolvent);
        }
    }

    public boolean findModel(Strategy strategy) throws Exception {
        try {
            switch (strategy) {
                case FUIP:
                    this.FUIP();
                    break;
                case TWL:
                    this.TWL();
                    break;
                default:
                    throw new Exception(strategy + " not implemented yet!\n Available strategies: FUIP, TWL.");
            }
            return true;
        } catch(Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
