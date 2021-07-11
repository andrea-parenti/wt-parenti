package it.polimi.wt_parenti.utils.enumerations;

/**
 * This enum lists all the possible order types and exposes utility methods to work with them.
 */
public enum Order {
    ASC, DESC;

    /**
     * @param current The current order type.
     * @return The other order type, or ASC if current is null.
     */
    public static Order invert(Order current) {
        return (current == ASC) ? DESC : ASC;
    }

    /**
     * @param order A String that can identify an order type.
     * @return The corresponding order type.
     * @throws IllegalArgumentException If the given String cannot be associated to any order type.
     */
    public static Order fromString(final String order) throws IllegalArgumentException {
        var str = order.toUpperCase();
        for (var o : values())
            if (str.equals(o.name())) return o;
        throw new IllegalArgumentException(order + " is not a valid order type!");
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
