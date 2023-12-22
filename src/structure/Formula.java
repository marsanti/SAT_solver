package structure;

import java.util.*;

public class Formula {
    private ArrayList<Clause> clauses;
    private int numberOfClauses;
    private int numberOfLiterals;

    public Formula() {
        clauses = new ArrayList<>();
    }

    public void addClause(Clause c) {
        clauses.add(c);
    }

    public int getNumberOfLiterals() {
        return numberOfLiterals;
    }

    public void setNumberOfLiterals(int numberOfLiterals) {
        this.numberOfLiterals = numberOfLiterals;
    }

    public int getNumberOfClauses() {
        return numberOfClauses;
    }

    public void setNumberOfClauses(int numberOfClauses) {
        this.numberOfClauses = numberOfClauses;
    }

    public ArrayList<Clause> getClauses() {
        return clauses;
    }

    public ArrayList<Literal> getAllLiterals() {
        ArrayList<Literal> lits = new ArrayList<>();

        for(Clause c : clauses) {
            lits.addAll(c.getLiterals());
        }

        return lits;
    }

    public Map<Literal, Integer> getLiteralOccurrences() {
        Map<Literal, Integer> occurrences = new HashMap<>();
        for(Clause c : this.clauses) {
            for(Literal l : c.getLiterals()) {
                occurrences.put(l, occurrences.getOrDefault(l, 0) + 1);
            }
        }
        return occurrences;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("F = ");

        for (int i = 0; i < this.clauses.size(); i++) {
            str.append(this.clauses.get(i).toString());
            if (i < this.clauses.size() - 1) {
                str.append(" ∧ ");
            }
        }

        return str.toString();
    }
}
