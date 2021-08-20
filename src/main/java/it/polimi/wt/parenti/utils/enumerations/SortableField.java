package it.polimi.wt.parenti.utils.enumerations;

public enum SortableField {
    MATRICULATION, NAME, EMAIL, BACHELOR_COURSE, GRADE, STATUS;

    public static SortableField fromString(final String field) {
        if (field == null) {
            return null;
        }
        return switch (field.toUpperCase()) {
            case "MATRICULATION" -> MATRICULATION;
            case "NAME" -> NAME;
            case "EMAIL" -> EMAIL;
            case "BACHELOR_COURSE", "BACHELOR", "COURSE" -> BACHELOR_COURSE;
            case "GRADE", "RESULT", "LAUDE" -> GRADE;
            case "STATUS" -> STATUS;
            default -> null;
        };
    }
}
