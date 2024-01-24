package structure;

import java.util.Objects;

public class Literal {

    private final int lit;
    private final boolean positive;
    private int level;

    public Literal(int lit, boolean positive, int level) {
        this.lit = lit;
        this.positive = positive;
        this.level = level;
    }
    public boolean isPositive() {
        return this.positive;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getLevel() {
        return this.level;
    }
    public Literal getNegate() {
        return new Literal(this.lit, !this.positive, this.level);
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
