package it.polimi.wt_parenti.utils;

import javax.servlet.ServletContext;
import javax.servlet.UnavailableException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {

    private ConnectionManager() {
        // Nascondo il costruttore pubblico di default.
    }

    public static Connection openConnection(ServletContext context) throws UnavailableException {

        final var driver = context.getInitParameter("DB_DRIVER");
        final var url = context.getInitParameter("DB_URL");
        final var user = context.getInitParameter("DB_USER");
        final var password = context.getInitParameter("DB_PASSWORD");

        try {
            Class.forName(driver);
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new UnavailableException("Impossibile connettersi al database: credenziali non valide!");
        } catch (ClassNotFoundException e) {
            throw new UnavailableException("Impossibile connettersi al database: driver non trovato!");
        }

    }

    public static void closeConnection(Connection connection) throws  SQLException {

        if (connection != null) {
            connection.close();
        }

    }
}
