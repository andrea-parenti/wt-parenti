package it.polimi.wt.parenti.utils.enumerations;

import java.util.Arrays;

public enum ExamResult {
    MISSING, REJECTED, SENT_BACK, PASSED;

    public static ExamResult fromString(final String result) {
        if (result == null) return null;
        var name = result.replaceAll("\\s+", "_").toUpperCase();
        if (Arrays.stream(values()).map(Enum::name).toList().contains(name)) return valueOf(name);
        else return null;
    }

    public String displayName() {
        return name().replaceAll("\\_+", "\s").toLowerCase();
    }
}
