package structure;

import java.util.ArrayList;

public class Formula {
    private ArrayList<Clause> clauses;

    public Formula() {
        clauses = new ArrayList<Clause>();
    }

    public void addClause(Clause c) {
        clauses.add(c);
    }

    public ArrayList<Clause> getClauses() {
        return clauses;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("{ ");

        for (int i = 0; i < this.clauses.size(); i++) {
            str.append(this.clauses.get(i).toString());
            if (i < this.clauses.size() - 1) {
                str.append(" ∧ ");
            }
        }

        str.append(" }");

        return str.toString();
    }
}
