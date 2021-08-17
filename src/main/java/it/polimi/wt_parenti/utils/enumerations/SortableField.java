package it.polimi.wt_parenti.utils.enumerations;

public enum SortableField {
    NAME, SURNAME, EMAIL, BACHELOR_COURSE, GRADE, STATUS;

    public static SortableField fromString(final String field) {
        if (field == null) {
            return null;
        }
        return switch (field.toUpperCase()) {
            case "NAME" -> NAME;
            case "SURNAME" -> SURNAME;
            case "EMAIL" -> EMAIL;
            case "BACHELOR_COURSE", "BACHELOR", "COURSE" -> BACHELOR_COURSE;
            case "GRADE", "RESULT", "LAUDE" -> GRADE;
            case "STATUS" -> STATUS;
            default -> null;
        };
    }
}
