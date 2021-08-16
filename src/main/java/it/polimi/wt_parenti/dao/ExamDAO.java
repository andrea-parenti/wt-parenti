package it.polimi.wt_parenti.dao;

import it.polimi.wt_parenti.beans.*;
import it.polimi.wt_parenti.utils.enumerations.ExamResult;
import it.polimi.wt_parenti.utils.enumerations.ExamStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ExamDAO {
    private final Connection connection;

    public ExamDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<Exam> getDetails(int examID) throws SQLException {
        var query = """
                SELECT
                    E.exam_id, E.status, E.result, E.grade, E.laude,
                    ES.exam_session_id, ES.date,
                    C.course_id, C.code, C.name,
                    P.professor_id, P.name, P.surname,
                    S.student_id, S.name, S.surname, S.email, S.bachelor_course,
                    ER.exam_report_id, ER.created_at
                FROM
                    exams AS E
                    NATURAL JOIN exam_sessions AS ES
                    NATURAL JOIN courses AS C
                    NATURAL JOIN professors AS P
                    NATURAL JOIN students AS S
                    NATURAL JOIN exam_reports AS ER
                WHERE
                    E.exam_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examID);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var er = new ExamReport();
                er.setId(result.getInt("ER.exam_report_id"));
                er.setCreation(result.getTimestamp("ER.created_at").toLocalDateTime());
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
                e.setReport(er);
                return Optional.of(e);
            }
        }
    }

    public Optional<Exam> getDetails(int examSessionID, int studentID) throws SQLException {
        var query = """
                SELECT
                    E.exam_id, E.status, E.result, E.grade, E.laude,
                    ES.exam_session_id, ES.date,
                    C.course_id, C.code, C.name,
                    P.professor_id, P.name, P.surname,
                    S.student_id, S.name, S.surname, S.email, S.bachelor_course,
                    ER.exam_report_id, ER.created_at
                FROM
                    exams AS E
                    JOIN exam_sessions AS ES ON E.exam_session_id = ES.exam_session_id
                    JOIN courses AS C ON ES.course_id = C.course_id
                    JOIN professors AS P ON C.professor_id = P.professor_id
                    JOIN students AS S ON E.student_id = S.student_id
                    JOIN exam_reports AS ER ON E.exam_report_id = ER.exam_report_id
                WHERE
                    ES.exam_session_id = ? AND S.student_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examSessionID);
            statement.setInt(2, studentID);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var er = new ExamReport();
                er.setId(result.getInt("ER.exam_report_id"));
                er.setCreation(result.getTimestamp("ER.created_at").toLocalDateTime());
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
                e.setReport(er);
                return Optional.of(e);
            }
        }
    }
}
