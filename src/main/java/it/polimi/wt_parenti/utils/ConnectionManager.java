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
        // Nascondo il costruttore pubblico di default.
    }

    /**
     * @param context The context of the servlet that has to contain the class of the driver to use, the database url,
     *                the database user and its own password.
     * @return A newly opened connection.
     * @throws UnavailableException If at least one of the given parameters is invalid.
     */
    public static Connection openConnection(ServletContext context) throws UnavailableException {
        if (context == null)
            throw new UnavailableException("Impossibile connettersi al database: ricevuto un contesto nullo!");

        final var driver = context.getInitParameter("DB_DRIVER");
        if (driver == null)
            throw new UnavailableException("Impossibile connettersi al database: driver non fornito!");

        final var url = context.getInitParameter("DB_URL");
        if (url == null)
            throw new UnavailableException("Impossibile connettersi al database: url non fornito!");

        final var user = context.getInitParameter("DB_USER");
        if (user == null)
            throw new UnavailableException("Impossibile connettersi al database: utenza non fornita!");

        final var password = context.getInitParameter("DB_PASSWORD");
        if (password == null)
            throw new UnavailableException("Impossibile connettersi al database: password non fornita!");

        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new UnavailableException("Impossibile connettersi al database: credenziali non valide!");
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Impossibile connettersi al database: driver non trovato!");
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
