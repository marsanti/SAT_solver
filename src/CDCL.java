import structure.*;
import utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CDCL {
    private final Formula formula;
    private final ArrayList<Literal> model;
    private final ArrayList<Literal> decidedLiterals;
    private final Map<Literal, Clause> justification;
    public final Map<ArrayList<Clause>, Clause> proofMapper;
    private int k;
    private static final int VSIDS_LIMIT_FOR_DECAY = 2;
    private static final double DECAY_CONSTANT = 0.95;
    public int n_decide_step;
    public int n_conflict;

    public CDCL(Formula f) {
        this.formula = f;
        this.model = new ArrayList<>();
        this.decidedLiterals = new ArrayList<>();
        this.justification = new HashMap<>();
        this.proofMapper = new HashMap<>();
        this.k = 0;
        this.n_decide_step = 0;
        this.n_conflict = 0;
    }
    public ArrayList<Literal> getModel() {
        return model;
    }

    /**
     * Decide step: based on VSIDS map.
     * @throws Exception if there are no literals left to be decided.
     */
    private void decide() throws Exception {
        Map<Literal, Double> VSIDS = this.formula.getVSIDS();
        int sizeModel = this.model.size();
        for(Literal key : VSIDS.keySet()) {
            if(this.decidedLiterals.contains(key) || this.model.contains(key)) continue;
            Literal notKey = key.getNegate();
            if(!(this.model.contains(notKey))) {
                key.setLevel(this.decidedLiterals.size() + 1);
                this.decidedLiterals.add(key);
                this.model.add(key);
                this.n_decide_step++;
                return;
            }
        }
        if(sizeModel == this.model.size()) {
            throw new Exception("there are no more literals to decide.");
        }
    }

    /**
     * Unit propagation in FUIP.
     * @return true if the model has changed.
     */
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
                    undefLit.setLevel(this.decidedLiterals.size());
                    this.model.add(undefLit);
                    this.justification.put(undefLit, c);
                    changed = true;
                }
            }
        }
        return initialModelSize != this.model.size();
    }

    /**
     * Two Watched Literals Technique propagation.
     * @return an Object representing a conflict Clause if one was found or a boolean if the model has changed and no conflict clause has been found.
     */
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
                    Literal lit = (Literal) response;
                    changed = true;
                    lit.setLevel(this.decidedLiterals.size());
                    this.model.add(lit);
                    this.justification.put(lit, c);
                }
            }
        }
        return modelSize != this.model.size();
    }

    /**
     * TWL util function:
     * adjust the TwoWatchedLiterals array for each clause.
     */
    private void adjustTWLArrays() {
        for(Clause c : this.formula.getClauses()) {
            c.fixTWLArray(this.model);
        }
    }

    /**
     * if there is any conflict clause return it.
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

    /**
     * This function collects all the literals at the current level.
     * @return ArrayList with the current level literals.
     */
    private ArrayList<Literal> getCurrentLevelLiterals() {
        int currentLevel = this.decidedLiterals.size();
        ArrayList<Literal> newList = new ArrayList<>();
        for(Literal l : this.model) {
            if(l.getLevel() == currentLevel) {
                newList.add(l);
            }
        }
        return newList;
    }

    /**
     * This function does the explain step.
     * it loops until the resolvent is an AssertionClause
     * @param conflict the conflict clause
     * @return the resolvent
     */
    private Clause explain(Clause conflict) {
        int currentLevel = this.decidedLiterals.size();
        Clause resolvent = conflict;
        this.n_conflict++;

        ArrayList<Literal> currentLiterals = getCurrentLevelLiterals();
        Collections.reverse(currentLiterals);

        do {
            conflict = resolvent;
            Literal lit = currentLiterals.get(0);
            if(this.decidedLiterals.contains(lit)) return resolvent;
            currentLiterals.remove(0);
            Clause justClause = this.justification.get(lit);
            resolvent = conflict.getResolvent(justClause);
            ArrayList<Clause> parents = new ArrayList<>();
            parents.add(conflict);
            parents.add(justClause);
            this.proofMapper.put(parents, resolvent);
        } while(!resolvent.isAssertionClause(this.model, currentLevel));

        return resolvent;
    }

    /**
     * Learn step: add the resolvent to the formula
     * @param resolvent the resolvent clause
     */
    private void learn(Clause resolvent) {
        if(!formula.containsClause(resolvent)) {
            this.formula.addClause(resolvent);
        }
    }

    /**
     * Backjump step: jump at the previous decided literals, negate it, and put it in the model with justification the resolvent.
     * @param resolvent the resolvent for justification map
     */
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
        notDecided.setLevel(this.decidedLiterals.size());
        this.model.add(notDecided);
        this.justification.put(notDecided, resolvent);

    }

    /**
     * "First Unique Implication Point" or "First Assertion Clause Heuristic".
     * @throws Exception if an Exception is thrown then the formula is UNSAT, otherwise it's SAT
     */
    private void FUIP() throws Exception {
        boolean sat = false;
        while((this.model.size() != this.formula.getNumberOfLiterals()) || !sat) {
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

    /**
     * Two Watched Literals technique.
     * @throws Exception if an Exception is thrown then the formula is UNSAT, otherwise it's SAT
     */
    private void TWL() throws Exception {
        boolean sat = false;
        while((this.model.size() != this.formula.getNumberOfLiterals()) || !sat) {
            Object response = this.twoWatchedLit();
            if(response instanceof Clause) {
                Clause conflict = (Clause) response;
                this.solveConflict(conflict);
                this.adjustTWLArrays();
            } else {
                boolean res = (boolean) response;
                if(!res) {
                    this.decide();
                }
            }
            sat = this.formula.isSatisfied(this.model);
        }
    }

    /**
     * In this function we try to solve the conflict.
     * We, also, handle the VSIDS map, increasing counters and each k steps we decay the values.
     * @throws Exception if an Exception is thrown then the formula is UNSAT, otherwise it's SAT
     */
    private void solveConflict(Clause conflict) throws Exception {
        this.formula.increaseVSIDSCounters(conflict);
        if(k >= VSIDS_LIMIT_FOR_DECAY) {
            this.formula.decayVSIDS(DECAY_CONSTANT);
            k = 0;
        } else {
            k++;
        }
        if(this.decidedLiterals.isEmpty()) {
            throw new Exception("Result: UNSATISFIABLE, conflict at level 0\nConflict clause: " + conflict + "\n\nmodel: " + this.model + "\nmodel size: " + this.model.size() + "\n");
        } else {
            Clause resolvent = this.explain(conflict);
            if(resolvent == null) {
                throw new Exception("Result: UNSATISFIABLE, Fail rule\nConflict clause: " + conflict + "\n\nmodel: " + this.model + "\nmodel size: " + this.model.size() + "\n");
            }
            if(resolvent.getLiterals().size() == 1 && formula.containsClause(resolvent.getNegate())) {
                throw new Exception("Result: UNSATISFIABLE, Fail rule (Not resolvent (unary) in formula)\nResolvent: " + resolvent + "\n\nmodel: " + this.model + "\nmodel size: " + this.model.size() + "\n");
            }
            this.learn(resolvent);
            this.backjump(resolvent);
        }
    }

    /**
     * Where all the magic starts.
     * @param strategy FUIP or TWL
     * @return true if SAT.
     * @throws IllegalArgumentException if Strategy is not implemented.
     * @throws Exception if UNSAT then an exception is thrown.
     */
    public boolean findModel(Strategy strategy) throws Exception {
        switch (strategy) {
            case FUIP:
                this.FUIP();
                break;
            case TWL:
                this.TWL();
                break;
            default:
                throw new IllegalArgumentException(strategy + " not implemented yet!\n Available strategies: FUIP, TWL.");
        }
        return true;
    }
}
