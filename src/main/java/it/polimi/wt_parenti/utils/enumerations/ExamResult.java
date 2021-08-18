package it.polimi.wt_parenti.utils.enumerations;

public enum ExamResult {
    MISSING, REJECTED, SENT_BACK, PASSED;

    public static ExamResult fromString(final String result) {
        if (result == null) return null;
        return valueOf(result.replaceAll("\\s", "_").toUpperCase());
    }

    public String getValue() {
        return name();
    }
}
