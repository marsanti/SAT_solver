package structure;

public class Literal {

    private int lit;
    private boolean positive;

    public Literal(int lit, boolean positive) {
        this.lit = lit;
        this.positive = positive;
    }

    public boolean isPositive() {
        return this.positive;
    }

    public int getLit() {
        return this.lit;
    }

    public void setLit(int lit) {
        this.lit = lit;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        if (!(this.isPositive())) {
            str.append("¬");
        }

        str.append("X").append(this.lit);

        return str.toString();
    }
}
