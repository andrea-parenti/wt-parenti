package it.polimi.wt_parenti.utils.enumerations;

/**
 * This enum lists all the possible statuses of an exam's mark.
 */
public enum ExamStatus {
    NOT_INSERTED_YET("NOT_INSERTED_YET"), INSERTED("INSERTED"), PUBLISHED("PUBLISHED"),
    REFUSED("REFUSED"), REPORTED("REPORTED");

    private final String value;

    ExamStatus(String value) {
        this.value = value;
    }

    public static ExamStatus fromString(final String status) {
        if (status == null) return null;
        return valueOf(status.replaceAll("\\s", "_").toUpperCase());
    }

    public String getValue() {
        return value;
    }
}
