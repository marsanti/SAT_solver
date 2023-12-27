package structure;

import java.util.ArrayList;
import java.util.Objects;

public class Clause {

    private ArrayList<Literal> literals;
    public Clause() {
        this.literals = new ArrayList<>();
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
        for(Literal l : this.literals) {
            Literal lNeg = new Literal(l.getLit(), !(l.isPositive()));
            if(!(model.contains(l) || model.contains(lNeg))) {
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
        for(Literal l : this.getLiterals()) {
            if(c.containsLiteral(l)) {
                resolvent.addLiteral(l);
            }
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
