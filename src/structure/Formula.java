package structure;

import java.util.*;

public class Formula {
    private final ArrayList<Clause> clauses;
    private int numberOfClauses;
    private int numberOfLiterals;
    private Map<Literal, Double> VSIDS;

    public Formula() {
        clauses = new ArrayList<>();
        this.VSIDS = new HashMap<>();
    }

    public void initVSIDS() {
        this.VSIDS = getLiteralOccurrences();
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
    public Map<Literal, Double> getVSIDS() {
        return sortByValue(this.VSIDS);
    }
    public void setNumberOfClauses(int numberOfClauses) {
        this.numberOfClauses = numberOfClauses;
    }
    public ArrayList<Clause> getClauses() {
        return clauses;
    }

    /**
     * @param c input clause
     * @return true if formula contains the input clause.
     */
    public Boolean containsClause(Clause c) {
        return this.clauses.contains(c);
    }

    /**
     * this function counts every occurrence of every literal in the formula.
     * Useful during the VSIDS initialization.
     * @return a map representing the occurrences of every literal.
     */
    public Map<Literal, Double> getLiteralOccurrences() {
        Map<Literal, Double> occurrences = new HashMap<>();
        for(Clause c : this.clauses) {
            for(Literal l : c.getLiterals()) {
                occurrences.put(l, occurrences.getOrDefault(l, (double) 0) + 1);
            }
        }

        return sortByValue(occurrences);
    }

    /**
     * Use this function to increase the VSIDS counters.
     * @param conflict it's the conflict clause.
     */
    public void increaseVSIDSCounters(Clause conflict) {
        for(Literal l : conflict.getLiterals()) {
            this.VSIDS.put(l, this.VSIDS.getOrDefault(l, (double) 0) + 1);
        }
    }

    /**
     * Use this function to multiply a constant to decay VSIDS counters.
     * @param constant The constant by which to multiply the current counters for decay.
     */
    public void decayVSIDS(double constant) {
        for(Literal key : this.VSIDS.keySet()) {
            this.VSIDS.put(key, this.VSIDS.getOrDefault(key, (double) 0) * constant);
        }
    }

    /**
     * Function used to sort a Map by value
     * @param map map to sort
     * @return sorted map
     */
    private static Map<Literal, Double> sortByValue(Map<Literal, Double> map) {
        List<Map.Entry<Literal, Double>> list = new LinkedList<>(map.entrySet());

        // Sort the list based on values
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<Literal, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Literal, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * this function check for satisfiability of the whole formula
     * @param model the actual trail
     * @return true if SAT
     */
    public boolean isSatisfied(ArrayList<Literal> model) {
        for(Clause c : this.clauses) {
            if(!(c.isSatisfied(model))) {
                return false;
            }
        }
        return true;
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
