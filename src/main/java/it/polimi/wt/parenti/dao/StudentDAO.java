package it.polimi.wt.parenti.dao;

import it.polimi.wt.parenti.beans.Course;
import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.beans.User;
import it.polimi.wt.parenti.utils.enumerations.UserRole;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
                    id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, user.getId());
            try (var result = statement.executeQuery()) {
                return result.isBeforeFirst();
            }
        }
    }

    public List<Course> getAttendedCourses(int studentID) throws SQLException {
        var query = """
                SELECT
                    C.id, C.code, C.name,
                    P.id, P.name, P.surname
                FROM
                    courses AS C
                    JOIN attendances AS A ON C.id = A.course_id
                    JOIN professors AS P ON C.professor_id = P.id
                WHERE
                    A.student_id = ?
                ORDER BY
                    C.name DESC;
                """;
        var courses = new ArrayList<Course>();
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentID);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var p = new Professor();
                    p.setId(result.getInt("P.id"));
                    p.setName(result.getString("P.name"));
                    p.setSurname(result.getString("P.surname"));
                    var c = new Course();
                    c.setId(result.getInt("C.id"));
                    c.setCode(result.getString("C.code"));
                    c.setName(result.getString("C.name"));
                    c.setProfessor(p);
                    courses.add(c);
                }
                return courses;
            }
        }
    }

    public Optional<Course> checkCourse(int studentID, int courseID) throws SQLException {
        var query = """
                SELECT
                    C.id, C.code, C.name,
                    P.id, P.name, P.surname
                FROM
                    courses AS C
                    JOIN attendances AS A ON C.id = A.course_id
                    JOIN professors AS P ON C.professor_id = P.id
                WHERE
                    A.student_id = ? AND C.id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, studentID);
            statement.setInt(2, courseID);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var p = new Professor();
                p.setId(result.getInt("P.id"));
                p.setName(result.getString("P.name"));
                p.setSurname(result.getString("P.surname"));
                var c = new Course();
                c.setId(result.getInt("C.id"));
                c.setCode(result.getString("C.code"));
                c.setName(result.getString("C.name"));
                c.setProfessor(p);
                return Optional.of(c);
            }
        }
    }
}
