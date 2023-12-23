package structure;

import java.util.Objects;

public class Literal {

    private final int lit;
    private final boolean positive;

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

    public Literal getNegate() {
        return new Literal(this.lit, !this.positive);
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

    @Override
    public int hashCode() {
        return Objects.hash(lit, positive);
    }
}
