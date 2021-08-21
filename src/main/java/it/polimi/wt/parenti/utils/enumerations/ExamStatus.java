package it.polimi.wt.parenti.utils.enumerations;

import java.util.Arrays;

/**
 * This enum lists all the possible statuses of an exam's mark.
 */
public enum ExamStatus {
    NOT_INSERTED_YET, INSERTED, PUBLISHED, REFUSED, REPORTED;

    public static ExamStatus fromString(final String status) {
        if (status == null) return null;
        var name = status.replaceAll("\\s+", "_").toUpperCase();
        if (Arrays.stream(values()).map(Enum::name).toList().contains(name)) return valueOf(name);
        else return null;
    }

    public String displayName() {
        return name().replaceAll("\\_+", "\s").toLowerCase();
    }
}
