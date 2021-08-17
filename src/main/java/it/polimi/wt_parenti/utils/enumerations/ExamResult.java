package it.polimi.wt_parenti.utils.enumerations;

public enum ExamResult {
    MISSING("MISSING"), REJECTED("REJECTED"), SENT_BACK("SENT_BACK"), PASSED("PASSED");

    private final String value;

    ExamResult(String value) {
        this.value = value;
    }

    public static ExamResult fromString(final String result) {
        if (result == null) return null;
        return valueOf(result.replaceAll("\\s", "_").toUpperCase());
    }

    public String getValue() {
        return value;
    }
}
