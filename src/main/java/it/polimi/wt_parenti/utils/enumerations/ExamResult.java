package it.polimi.wt_parenti.utils.enumerations;

public enum ExamResult {
    MISSING, REJECTED, SENT_BACK, PASSED;

    public static ExamResult fromString(final String result) {
        return valueOf(result.replaceAll("\\s", "_").toUpperCase());
    }
}
