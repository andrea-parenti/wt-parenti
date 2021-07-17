package it.polimi.wt_parenti.dao;

import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.utils.enumerations.UserRole;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<User> checkCredentials(String username, String password) throws SQLException {
        if (username == null) return Optional.empty();
        var query = """
                SELECT
                    id, username, role
                FROM
                    users
                WHERE
                    username = ? AND password = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var loggedUser = new User();
                loggedUser.setId(result.getInt("id"));
                loggedUser.setUsername(result.getString("username"));
                loggedUser.setRole(UserRole.fromString(result.getString("role")));
                return Optional.of(loggedUser);
            }
        }
    }

    public boolean checkUser(User user) throws SQLException {
        var query = """
                SELECT
                    COUNT(*)
                FROM
                    users
                WHERE
                    id = ? AND username = ? AND role = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getRole().toString());
            try (var result = statement.executeQuery()) {
                return result.isBeforeFirst();
            }
        }
    }
}
