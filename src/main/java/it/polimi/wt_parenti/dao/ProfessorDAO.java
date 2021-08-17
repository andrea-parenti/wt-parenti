package it.polimi.wt_parenti.dao;

import it.polimi.wt_parenti.beans.Course;
import it.polimi.wt_parenti.beans.ExamSession;
import it.polimi.wt_parenti.beans.Professor;
import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.utils.enumerations.UserRole;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    public List<Course> getTaughtCourses(int professorID) throws SQLException {
        var query = """
                SELECT
                    C.course_id, C.code, C.name,
                    P.professor_id, P.name, P.surname
                FROM
                    courses AS C
                    JOIN professors AS P ON C.professor_id = P.professor_id
                WHERE
                    P.professor_id = ?
                ORDER BY
                    C.name DESC;
                """;
        var courses = new ArrayList<Course>();
        try (var statement = connection.prepareStatement(query)){
            statement.setInt(1, professorID);
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

    public Optional<Course> checkCourse(int professorID, int courseID) throws SQLException {
        var query = """
                SELECT
                    C.course_id, C.code, C.name,
                    P.professor_id, P.name, P.surname
                FROM
                    courses AS C
                    JOIN professors AS P ON C.professor_id = P.professor_id
                WHERE
                    P.professor_id = ? AND C.course_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, professorID);
            statement.setInt(2, courseID);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var p = new Professor();
                p.setId(result.getInt("P.professor_id"));
                p.setName(result.getString("P.name"));
                p.setSurname(result.getString("P.surname"));
                var c = new Course();
                c.setId(result.getInt("C.course_id"));
                c.setCode(result.getString("C.code"));
                c.setName(result.getString("C.name"));
                c.setProfessor(p);
                return Optional.of(c);
            }
        }
    }

    public Optional<ExamSession> checkExamSession(int professorID, int examSessionID) throws SQLException {
        var query = """
                SELECT
                    ES.exam_session_id, ES.date,
                    C.course_id, C.code, C.name,
                    P.professor_id, P.name, P.surname
                FROM
                    exam_sessions AS ES
                    JOIN courses AS C ON ES.course_id = C.course_id
                    JOIN professors AS P ON C.professor_id = P.professor_id
                WHERE
                    P.professor_id = ? AND ES.exam_session_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, professorID);
            statement.setInt(2, examSessionID);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var p = new Professor();
                p.setId(result.getInt("P.professor_id"));
                p.setName(result.getString("P.name"));
                p.setSurname(result.getString("P.surname"));
                var c = new Course();
                c.setId(result.getInt("C.course_id"));
                c.setCode(result.getString("C.code"));
                c.setName(result.getString("C.name"));
                c.setProfessor(p);
                var es = new ExamSession();
                es.setId(result.getInt("ES.exam_session_id"));
                es.setDate(result.getDate("ES.date").toLocalDate());
                es.setCourse(c);
                return Optional.of(es);
            }
        }
    }
}
