package it.polimi.wt_parenti.dao;

import it.polimi.wt_parenti.beans.Student;
import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.utils.enumerations.UserRole;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class StudentDAO {
    private final Connection connection;

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean checkStudent(User user) throws SQLException {
        if (user == null || user.getRole() != UserRole.STUDENT) return false;
        var query = """
                SELECT
                    COUNT(*)
                FROM
                    students
                WHERE
                    student_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            try (var result = statement.executeQuery()) {
                return result.isBeforeFirst();
            }
        }
    }

    public Optional<Student> associateStudent(User user) throws SQLException {
        if (user == null || user.getRole() != UserRole.STUDENT) return Optional.empty();
        var query = """
                SELECT
                    student_id, name, surname, email, bachelor_course
                FROM
                    students
                WHERE
                    student_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                var s = new Student();
                s.setId(result.getInt("student_id"));
                s.setName(result.getString("name"));
                s.setSurname(result.getString("surname"));
                s.setEmail(result.getString("email"));
                s.setBachelorCourse(result.getString("bachelor_course"));
                return Optional.of(s);
            }
        }
    }
}
