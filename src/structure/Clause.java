package structure;

import java.util.ArrayList;

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
            if(!model.contains(l)) {
                undefinedLiterals.add(l);
            }
        }
        return undefinedLiterals;
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

}
