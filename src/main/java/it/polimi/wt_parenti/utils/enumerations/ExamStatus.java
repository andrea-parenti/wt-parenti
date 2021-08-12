package it.polimi.wt_parenti.utils.enumerations;

/**
 * This enum lists all the possible statuses of an exam's mark.
 */
public enum ExamStatus {
    NOT_INSERTED_YET, INSERTED, PUBLISHED, REFUSED, REPORTED;

    public static ExamStatus fromString(final String status) {
        return valueOf(status.replaceAll("\\s", "_").toUpperCase());
    }
}
