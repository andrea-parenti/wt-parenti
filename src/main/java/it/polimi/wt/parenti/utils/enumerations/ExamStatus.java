package it.polimi.wt.parenti.utils.enumerations;

import java.util.Locale;

/**
 * This enum lists all the possible statuses of an exam's mark.
 */
public enum ExamStatus {
    NOT_INSERTED_YET, INSERTED, PUBLISHED, REFUSED, REPORTED;

    public static ExamStatus fromString(final String status) {
        if (status == null) return null;
        return valueOf(status.replaceAll("\\s+", "_").toUpperCase());
    }

    public String displayName() {
        return name().replaceAll("\\_+", "\s").toLowerCase();
    }
}
