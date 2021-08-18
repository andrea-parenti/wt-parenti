package it.polimi.wt_parenti.utils.enumerations;

/**
 * This enum lists all the possible order types and exposes utility methods to work with them.
 */
public enum OrderType {
    ASC, DESC;

    /**
     * @param current The current order type.
     * @return The other order type, or ASC if current is null.
     */
    public static OrderType invert(OrderType current) {
        return (current == ASC) ? DESC : ASC;
    }

    /**
     * @param order A String that can identify an order type.
     * @return The corresponding order type.
     * @throws IllegalArgumentException If the given String cannot be associated to any order type.
     */
    public static OrderType fromString(final String order) throws IllegalArgumentException {
        return valueOf(order.toUpperCase());
    }

    public String getValue() {
        return name();
    }
}
