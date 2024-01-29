package structure;

import java.util.ArrayList;
import java.util.Optional;

public class Clause {

    private final ArrayList<Literal> literals;
    // size of max 2
    private final ArrayList<Literal> twoWatchedLiterals;

    public Clause() {
        this.literals = new ArrayList<>();
        this.twoWatchedLiterals = new ArrayList<>();
    }

    public ArrayList<Literal> getLiterals() {
        return this.literals;
    }

    public void addLiteral(Literal lit) {
        this.literals.add(lit);
    }

    public boolean containsLiteral(Literal l) {
        return this.literals.contains(l);
    }

    public ArrayList<Literal> getUndefinedLiterals(ArrayList<Literal> model) {
        ArrayList<Literal> undefinedLiterals = new ArrayList<>();
        if(this.isSatisfied(model)) return undefinedLiterals;
        for(Literal l : this.literals) {
            Literal lNeg = l.getNegate();
            if(!(model.contains(l)) && !(model.contains(lNeg))) {
                undefinedLiterals.add(l);
            }
        }
        return undefinedLiterals;
    }

    public boolean isSatisfied(ArrayList<Literal> model) {
        for(Literal l : this.literals) {
            if(model.contains(l)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function return if the clause is an Assertion Clause.
     * one literal of the clause is false at the current level, whereas the others are false on a smaller level of the trail
     * @param model the actual model
     * @return true if is an assertion clause, false otherwise
     */
    public boolean isAssertionClause(ArrayList<Literal> model, int currentLevel) {
        boolean oneFound = false;
        for(Literal l : this.getLiterals()) {
            int index = model.indexOf(l.getNegate());
            if(index == -1) {
                return false;
            }
            Literal modelLit = model.get(index);
            if(modelLit.getLevel() == currentLevel) {
                if(oneFound) {
                    return false;
                } else {
                    oneFound = true;
                }
            }
        }
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder("( ");

        for (int i = 0; i < this.literals.size(); i++) {
            Literal lit = this.literals.get(i);
            str.append(lit.toString());
            if (i < this.literals.size() - 1) {
                str.append(" ∨ ");
            }
        }

        str.append(" )");

        return str.toString();
    }

    public Clause getResolvent(Clause c) {
        Clause resolvent = new Clause();
        ArrayList<Literal> allLit = new ArrayList<>(this.getLiterals());
        allLit.addAll(c.getLiterals());
        for(Literal l : allLit) {
            if(!allLit.contains(l.getNegate()) && !resolvent.containsLiteral(l))
                resolvent.addLiteral(l);
        }
        return resolvent;
    }

    public Clause getNegate() {
        Clause newClause = new Clause();
        for(Literal l : this.literals) {
            Literal lNeg = l.getNegate();
            newClause.addLiteral(lNeg);
        }
        return newClause;
    }

    private Literal getNonFalseLiteral(ArrayList<Literal> model) {
        for(Literal lit : this.literals) {
            Literal notLit = lit.getNegate();
            if(model.contains(lit) && !this.twoWatchedLiterals.contains(lit)) {
                return lit;
            } else if(!model.contains(notLit) && !this.twoWatchedLiterals.contains(lit)) {
                return lit;
            }
        }
        return null;
    }

    private Object initTwoWatchedLiterals(ArrayList<Literal> model) {
        // init two watched array
        if(this.twoWatchedLiterals.isEmpty()) {
            Literal lit = this.getNonFalseLiteral(model);
            if(lit == null) {
                return false;
            } else {
                this.twoWatchedLiterals.add(lit);
            }
            Literal lit2 = this.getNonFalseLiteral(model);
            if(lit2 == null) {
                if(!model.contains(lit)) {
                    // since no other non-false Lit was found, then we can say that lit is the prop unit
                    return lit;
                }
            } else {
                this.twoWatchedLiterals.add(lit2);
            }
        }
        return null;
    }
    public void fixTWLArray(ArrayList<Literal> model) {
        if(this.twoWatchedLiterals.size() < 2) {
            for(Literal l : this.literals) {
                if(!model.contains(l.getNegate()) && !this.twoWatchedLiterals.contains(l)) {
                    this.twoWatchedLiterals.add(l);
                    if(this.twoWatchedLiterals.size() == 2) return;
                }
            }
        }
    }

    public Object watchTwoLiterals(ArrayList<Literal> model) {
        this.checkForFalsifiedElements(model);
        Object response = initTwoWatchedLiterals(model);

        // if response is boolean then return it, otherwise go ahead
        if(response != null) {
            if(response instanceof Literal) {
                return response;
            } else if((boolean) response) {
                return true;
            } else {
                return this;
            }
        }

        if(this.twoWatchedLiterals.size() == 1) {
            if(!model.contains(this.twoWatchedLiterals.get(0))) {
                return this.twoWatchedLiterals.get(0);
            }
            return true;
        }

        Literal lit1 = this.twoWatchedLiterals.get(0);
        Literal lit2 = this.twoWatchedLiterals.get(1);
        if(model.contains(lit1.getNegate())) {
            if(model.contains(lit2.getNegate())) {
                // both lit1 and lit2 are now false, so we remove them from the watched literals and redo the init function
                this.twoWatchedLiterals.remove(lit1);
                this.twoWatchedLiterals.remove(lit2);
                Object response1 = initTwoWatchedLiterals(model);

                // if response1 is boolean then return it, otherwise go ahead
                if(response1 != null) {
                    if(response1 instanceof Literal) {
                        return response1;
                    } else if((boolean) response1) {
                        return true;
                    } else {
                        return this;
                    }
                }
            } else {
                // only lit1 is inside the model
                this.twoWatchedLiterals.remove(lit1);
                lit1 = getNonFalseLiteral(model);
                if(lit1 == null) {
                    if(!model.contains(lit2)) {
                        // since no other non-false Lit was found, then we can say that lit2 is the prop unit
                        return lit2;
                    }
                } else {
                    this.twoWatchedLiterals.add(lit1);
                }
            }
        } else if (model.contains(lit2.getNegate())) {
            // only lit2 is inside the model
            this.twoWatchedLiterals.remove(lit2);
            lit2 = getNonFalseLiteral(model);
            if(lit2 == null) {
                if(!model.contains(lit1)) {
                    // since no other non-false Lit was found, then we can say that lit1 is the prop unit
                    return lit1;
                }
            } else {
                this.twoWatchedLiterals.add(lit2);
            }
        }

        // all the literals are false
        return true;
    }

    private void checkForFalsifiedElements(ArrayList<Literal> model) {
        ArrayList<Literal> copy = new ArrayList<>(this.twoWatchedLiterals);
        for(Literal l : copy) {
            // if model contains notL then the literal has been falsified
            if(model.contains(l.getNegate())) {
                this.twoWatchedLiterals.remove(l);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clause clause = (Clause) o;
        if(this.literals.size() != clause.literals.size()) return false;
        for(Literal l : this.literals) {
            if(!(clause.literals.contains(l))) {
                return false;
            }
        }
        return true;
    }

}
