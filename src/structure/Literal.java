package structure;

import java.util.Objects;

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

    public boolean getPositive() {
        return this.positive;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Literal)) return false;
        Literal literal = (Literal) o;
        return this.lit == literal.lit && this.positive == literal.positive;
    }
}
