package it.polimi.wt_parenti.dao;

import it.polimi.wt_parenti.beans.Professor;
import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.utils.enumerations.UserRole;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ProfessorDAO {
    private final Connection connection;

    public ProfessorDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean checkProfessor(User user) throws SQLException {
        if (user == null || user.getRole() != UserRole.PROFESSOR) return false;
        var query = """
                SELECT
                    COUNT(*)
                FROM
                    professors
                WHERE
                    professor_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            try (var result = statement.executeQuery()) {
                return result.isBeforeFirst();
            }
        }
    }

    public Optional<Professor> associateProfessor(User user) throws SQLException {
        if (user == null || user.getRole() != UserRole.PROFESSOR) return Optional.empty();
        var query = """
                SELECT
                    professor_id, name, surname
                FROM
                    professors
                WHERE
                    professor_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                var p = new Professor();
                p.setId(result.getInt("professor_id"));
                p.setName(result.getString("name"));
                p.setSurname(result.getString("surname"));
                return Optional.of(p);
            }
        }
    }
}
