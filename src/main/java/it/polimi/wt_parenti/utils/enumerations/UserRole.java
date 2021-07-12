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

    @Override
    public String toString() {
        return super.toString();
    }
}
