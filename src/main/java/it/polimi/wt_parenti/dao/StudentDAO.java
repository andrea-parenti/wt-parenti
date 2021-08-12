package it.polimi.wt_parenti.dao;

import it.polimi.wt_parenti.beans.Course;
import it.polimi.wt_parenti.beans.Professor;
import it.polimi.wt_parenti.beans.Student;
import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.utils.enumerations.UserRole;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

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

    public List<Course> getAttendedCourses(int studentID) throws SQLException {
        var query = """
                SELECT
                    C.course_id, C.code, C.name, P.professor_id, P.name, P.surname
                FROM
                    courses AS C NATURAL JOIN professors AS P NATURAL JOIN attendances AS A
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
                    p.setId(result.getInt("P.professor_id"));
                    p.setName(result.getString("P.name"));
                    p.setSurname(result.getString("P.surname"));
                    var c = new Course();
                    c.setId(result.getInt("C.course_id"));
                    c.setCode(result.getString("C.code"));
                    c.setName(result.getString("C.name"));
                    c.setProfessor(p);
                    courses.add(c);
                }
                return courses;
            }
        }
    }
}
