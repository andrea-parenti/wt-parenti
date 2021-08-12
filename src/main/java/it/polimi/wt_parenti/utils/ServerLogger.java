package it.polimi.wt_parenti.utils;

import java.util.logging.Logger;

/**
 * This class provides a shared server-side logger.
 */
public class ServerLogger {
    public static final Logger LOGGER = Logger.getLogger("Server");

    private ServerLogger() {
        // default constructor hiding mechanism
    }
}
