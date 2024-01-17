package utils;

public enum Strategy {
    FUIP("FUIP"),
    TWL("TWL")
    ;

    private final String text;

    Strategy(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
