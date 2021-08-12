package it.polimi.wt_parenti.utils;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * This class manages database connections by exposing static methods for opening or closing a connection.
 */
public class ConnectionManager {
    private ConnectionManager() {
        // default constructor hiding mechanism
    }

    /**
     * @param context The context of the servlet that has to contain the class of the driver to use, the database url,
     *                the database user and its own password.
     * @return A newly opened connection.
     * @throws UnavailableException If at least one of the given parameters is invalid.
     */
    public static Connection openConnection(ServletContext context) throws UnavailableException {
        if (context == null)
            throw new UnavailableException("Unable to connect to the database: context was null!");

        final var driver = context.getInitParameter("DB_DRIVER");
        if (driver == null)
            throw new UnavailableException("Unable to connect to the database: driver was null!");

        final var url = context.getInitParameter("DB_URL");
        if (url == null)
            throw new UnavailableException("Unable to connect to the database: url was null!");

        final var user = context.getInitParameter("DB_USER");
        if (user == null)
            throw new UnavailableException("Unable to connect to the database: user was null!");

        final var password = context.getInitParameter("DB_PASSWORD");
        if (password == null)
            throw new UnavailableException("Unable to connect to the database: password vas null!");

        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new UnavailableException("Unable to connect to the database: credentials were invalid!");
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Unable to connect to the database: driver not found!");
        }
    }

    /**
     * @param connection The connection to close.
     * @throws SQLException If a database access error occurs
     */
    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
