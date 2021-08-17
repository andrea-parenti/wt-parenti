package it.polimi.wt_parenti.dao;

import it.polimi.wt_parenti.beans.*;
import it.polimi.wt_parenti.utils.enumerations.ExamResult;
import it.polimi.wt_parenti.utils.enumerations.ExamStatus;
import it.polimi.wt_parenti.utils.enumerations.OrderType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExamDAO {
    private final Connection connection;

    public ExamDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Exam> getDetails(int examID) throws SQLException {
        var query = """
                SELECT
                    E.exam_id, E.status, E.result, E.grade, E.laude, E.exam_report_id,
                    ES.exam_session_id, ES.date,
                    C.course_id, C.code, C.name,
                    P.professor_id, P.name, P.surname,
                    S.student_id, S.name, S.surname, S.email, S.bachelor_course
                FROM
                    exams AS E
                    JOIN exam_sessions AS ES ON E.exam_session_id = ES.exam_session_id
                    JOIN courses AS C ON ES.course_id = C.course_id
                    JOIN professors AS P ON C.professor_id = P.professor_id
                    JOIN students AS S ON E.student_id = S.student_id
                WHERE
                    E.exam_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examID);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var s = new Student();
                s.setId(result.getInt("S.student_id"));
                s.setName(result.getString("S.name"));
                s.setSurname(result.getString("S.surname"));
                s.setEmail(result.getString("S.email"));
                s.setBachelorCourse(result.getString("S.bachelor_course"));
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
                var e = new Exam();
                e.setId(result.getInt("E.exam_id"));
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
                                exam_report_id = ?
                            """;
                    try (var additionalStatement = connection.prepareStatement(additionalQuery)) {
                        additionalStatement.setInt(1, er.getId());
                        try (var additionalResult = additionalStatement.executeQuery()) {
                            if (result.isBeforeFirst()) {
                                additionalResult.next();
                                er.setCreation(result.getTimestamp("ER.created_at").toLocalDateTime());
                            }
                        }
                    }
                    e.setReport(er);
                }
                return Optional.of(e);
            }
        }
    }

    public Optional<Exam> getDetails(int examSessionID, int studentID) throws SQLException {
        var query = """
                SELECT
                    E.exam_id, E.status, E.result, E.grade, E.laude, E.exam_report_id,
                    ES.exam_session_id, ES.date,
                    C.course_id, C.code, C.name,
                    P.professor_id, P.name, P.surname,
                    S.student_id, S.name, S.surname, S.email, S.bachelor_course
                FROM
                    exams AS E
                    JOIN exam_sessions AS ES ON E.exam_session_id = ES.exam_session_id
                    JOIN courses AS C ON ES.course_id = C.course_id
                    JOIN professors AS P ON C.professor_id = P.professor_id
                    JOIN students AS S ON E.student_id = S.student_id
                WHERE
                    ES.exam_session_id = ? AND S.student_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examSessionID);
            statement.setInt(2, studentID);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var s = new Student();
                s.setId(result.getInt("S.student_id"));
                s.setName(result.getString("S.name"));
                s.setSurname(result.getString("S.surname"));
                s.setEmail(result.getString("S.email"));
                s.setBachelorCourse(result.getString("S.bachelor_course"));
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
                var e = new Exam();
                e.setId(result.getInt("E.exam_id"));
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
                                exam_report_id = ?
                            """;
                    try (var additionalStatement = connection.prepareStatement(additionalQuery)) {
                        additionalStatement.setInt(1, er.getId());
                        try (var additionalResult = additionalStatement.executeQuery()) {
                            if (result.isBeforeFirst()) {
                                additionalResult.next();
                                er.setCreation(result.getTimestamp("ER.created_at").toLocalDateTime());
                            }
                        }
                    }
                    e.setReport(er);
                }
                return Optional.of(e);
            }
        }
    }

    public List<Exam> getSessionDetails(int examSessionID, String sortingField, OrderType orderType) throws SQLException {
        var query = """
                SELECT
                    E.exam_id, E.status, E.result, E.grade, E.laude, E.exam_report_id,
                    ES.exam_session_id, ES.date,
                    C.course_id, C.code, C.name,
                    P.professor_id, P.name, P.surname,
                    S.student_id, S.name, S.surname, S.email, S.bachelor_course
                FROM
                    exams AS E
                    JOIN exam_sessions AS ES ON E.exam_session_id = ES.exam_session_id
                    JOIN courses AS C ON ES.course_id = C.course_id
                    JOIN professors AS P ON C.professor_id = P.professor_id
                    JOIN students AS S ON E.student_id = S.student_id
                WHERE
                    ES.exam_session_id = ?
                """;
        var sorter = switch (sortingField) {
            case "NAME" -> " ORDER BY S.name " + orderType;
            case "SURNAME" -> " ORDER BY S.surname " + orderType;
            case "EMAIL" -> " ORDER BY S.email " + orderType;
            case "BACHELORCOURSE" -> " ORDER BY S.bachelorCourse " + orderType;
            case "GRADE" -> " ORDER BY E.result" + orderType + ", E.grade " + orderType + ", E.laude " + orderType;
            case "STATUS" -> " ORDER BY E.status " + orderType;
            default -> "";
        };
        var exams = new ArrayList<Exam>();
        try (var statement = connection.prepareStatement(query+sorter)) {
            statement.setInt(1, examSessionID);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var s = new Student();
                    s.setId(result.getInt("S.student_id"));
                    s.setName(result.getString("S.name"));
                    s.setSurname(result.getString("S.surname"));
                    s.setEmail(result.getString("S.email"));
                    s.setBachelorCourse(result.getString("S.bachelor_course"));
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
                    var e = new Exam();
                    e.setId(result.getInt("E.exam_id"));
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
                                    exam_report_id = ?
                                """;
                        try (var additionalStatement = connection.prepareStatement(additionalQuery)) {
                            additionalStatement.setInt(1, er.getId());
                            try (var additionalResult = additionalStatement.executeQuery()) {
                                if (result.isBeforeFirst()) {
                                    additionalResult.next();
                                    er.setCreation(result.getTimestamp("ER.created_at").toLocalDateTime());
                                }
                            }
                        }
                        e.setReport(er);
                    }
                }
                return exams;
            }
        }
    }
}
