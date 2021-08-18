package it.polimi.wt_parenti.utils.enumerations;

/**
 * This enum lists all the possible roles of a user and exposes utility methods to work with them.
 */
public enum UserRole {
    STUDENT, PROFESSOR;

    /**
     * @param role A String that can identify a role.
     * @return True if the given String identifies a role, false otherwise.
     */
    public static boolean isValid(final String role) {
        var str = role.toUpperCase();
        for (var r : values())
            if (str.equals(r.name())) return true;
        return false;
    }

    /**
     * @param role A String that can identify a user role.
     * @return The corresponding role.
     * @throws IllegalArgumentException If the given String cannot be associated to any user role.
     */
    public static UserRole fromString(final String role) throws IllegalArgumentException {
        return valueOf(role.toUpperCase());
    }

    public String getValue() {
        return name();
    }
}
