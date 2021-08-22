package it.polimi.wt.parenti.dao;

import it.polimi.wt.parenti.beans.*;
import it.polimi.wt.parenti.utils.enumerations.ExamResult;
import it.polimi.wt.parenti.utils.enumerations.ExamStatus;
import it.polimi.wt.parenti.utils.enumerations.UserRole;

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

    public List<Course> getTaughtCourses(int professorID) throws SQLException {
        var query = """
                SELECT
                    C.id, C.code, C.name,
                    P.id, P.name, P.surname
                FROM
                    courses AS C
                    JOIN professors AS P ON C.professor_id = P.id
                WHERE
                    P.id = ?
                ORDER BY
                    C.name DESC;
                """;
        var courses = new ArrayList<Course>();
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, professorID);
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

    public Optional<Course> checkCourse(int professorID, int courseID) throws SQLException {
        var query = """
                SELECT
                    C.id, C.code, C.name,
                    P.id, P.name, P.surname
                FROM
                    courses AS C
                    JOIN professors AS P ON C.professor_id = P.id
                WHERE
                    P.id = ? AND C.id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, professorID);
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

    public Optional<ExamSession> checkExamSession(int professorID, int examSessionID) throws SQLException {
        var query = """
                SELECT
                    ES.id, ES.date,
                    C.id, C.code, C.name,
                    P.id, P.name, P.surname
                FROM
                    exam_sessions AS ES
                    JOIN courses AS C ON ES.course_id = C.id
                    JOIN professors AS P ON C.professor_id = P.id
                WHERE
                    P.id = ? AND ES.id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, professorID);
            statement.setInt(2, examSessionID);
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
                var es = new ExamSession();
                es.setId(result.getInt("ES.id"));
                es.setDate(result.getDate("ES.date").toLocalDate());
                es.setCourse(c);
                return Optional.of(es);
            }
        }
    }

    public Optional<Exam> checkExam(int professorID, int examID) throws SQLException {
        var query = """
                SELECT
                    E.id, E.status, E.result, E.grade, E.laude, E.exam_report_id,
                    ES.id, ES.date,
                    C.id, C.code, C.name,
                    P.id, P.name, P.surname,
                    S.id, S.matriculation, S.name, S.surname, S.email, S.bachelor_course
                FROM
                    exams AS E
                    JOIN exam_sessions AS ES ON E.exam_session_id = ES.id
                    JOIN courses AS C ON ES.course_id = C.id
                    JOIN professors AS P ON C.professor_id = P.id
                    JOIN students AS S ON E.student_id = S.id
                WHERE
                    E.id = ? AND P.id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examID);
            statement.setInt(2, professorID);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var s = new Student();
                s.setId(result.getInt("S.id"));
                s.setMatriculation(result.getInt("S.matriculation"));
                s.setName(result.getString("S.name"));
                s.setSurname(result.getString("S.surname"));
                s.setEmail(result.getString("S.email"));
                s.setBachelorCourse(result.getString("S.bachelor_course"));
                var p = new Professor();
                p.setId(result.getInt("P.id"));
                p.setName(result.getString("P.name"));
                p.setSurname(result.getString("P.surname"));
                var c = new Course();
                c.setId(result.getInt("C.id"));
                c.setCode(result.getString("C.code"));
                c.setName(result.getString("C.name"));
                c.setProfessor(p);
                var es = new ExamSession();
                es.setId(result.getInt("ES.id"));
                es.setDate(result.getDate("ES.date").toLocalDate());
                es.setCourse(c);
                var e = new Exam();
                e.setId(result.getInt("E.id"));
                e.setStatus(ExamStatus.fromString(result.getString("E.status")));
                e.setResult(ExamResult.fromString(result.getString("E.result")));
                e.setGrade(result.getInt("E.grade"));
                e.setLaude(result.getBoolean("E.laude"));
                e.setExamSession(es);
                e.setStudent(s);
                var er = new ExamReport();
                er.setId(result.getInt("E.exam_report_id"));
                if (er.getId() != 0) {
                    var additionalQuery = """
                            SELECT
                                created_at
                            FROM
                                exam_reports
                            WHERE
                                id = ?
                            """;
                    try (var additionalStatement = connection.prepareStatement(additionalQuery)) {
                        additionalStatement.setInt(1, er.getId());
                        try (var additionalResult = additionalStatement.executeQuery()) {
                            if (result.isBeforeFirst()) {
                                additionalResult.next();
                                er.setCreation(result.getTimestamp("created_at").toLocalDateTime());
                            }
                        }
                    }
                    e.setReport(er);
                }
                return Optional.of(e);
            }
        }
    }
}
