package it.polimi.wt.parenti.dao;

import it.polimi.wt.parenti.beans.*;
import it.polimi.wt.parenti.utils.enumerations.ExamResult;
import it.polimi.wt.parenti.utils.enumerations.ExamStatus;
import it.polimi.wt.parenti.utils.enumerations.OrderType;
import it.polimi.wt.parenti.utils.enumerations.SortableField;

import java.sql.*;
import java.time.LocalDateTime;
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
                    E.id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examID);
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

    public Optional<Exam> getDetails(int examSessionID, int studentID) throws SQLException {
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
                    ES.id = ? AND S.id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examSessionID);
            statement.setInt(2, studentID);
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

    public List<Exam> getSessionDetails(int examSessionID, SortableField sortingField, OrderType orderType) throws SQLException {
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
                    ES.id = ?
                """;
        var sorter = switch (sortingField) {
            case MATRICULATION -> " ORDER BY S.matriculation " + orderType.name();
            case NAME -> " ORDER BY S.surname " + orderType.name() + ", S.name " + orderType.name();
            case EMAIL -> " ORDER BY S.email " + orderType.name();
            case BACHELOR_COURSE -> " ORDER BY S.bachelor_course " + orderType.name();
            case GRADE -> " ORDER BY E.result " + orderType.name() + ", E.grade " + orderType.name() + ", E.laude " + orderType.name();
            case STATUS -> " ORDER BY E.status " + orderType.name();
        };
        var exams = new ArrayList<Exam>();
        try (var statement = connection.prepareStatement(query + sorter)) {
            statement.setInt(1, examSessionID);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
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
                    exams.add(e);
                }
                return exams;
            }
        }
    }

    public void updateGrade(int examID, ExamResult result, Integer grade, Boolean laude) throws SQLException {
        var query = """
                UPDATE
                    exams
                SET
                    status = 'inserted', result = ?, grade = ?, laude = ?
                WHERE
                    id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setString(1, result.displayName());

            if (grade == null) statement.setNull(2, Types.NULL);
            else statement.setInt(2, grade);

            if (laude == null) statement.setNull(3, Types.NULL);
            else statement.setBoolean(3, laude);

            statement.setInt(4, examID);
            statement.executeUpdate();
        }
    }

    public void publish(int examSessionID) throws SQLException {
        var query = """
                UPDATE
                    exams
                SET
                    status = 'published'
                WHERE
                    status = 'inserted' AND exam_session_id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examSessionID);
            statement.executeUpdate();
        }
    }

    public void report(int examSessionID) throws SQLException {
        var createReport = """
                INSERT INTO
                    exam_reports (created_at)
                VALUES
                    (?)
                """;
        PreparedStatement s1 = null;
        var selectReportId = """
                SELECT
                    LAST_INSERT_ID()
                FROM
                    exam_reports
                """;
        Statement s2 = null;
        var updateRefused = """
                UPDATE
                    exams
                SET
                    result = 'rejected'
                WHERE
                    status = 'refused' AND exam_session_id = ?
                """;
        PreparedStatement s3 = null;
        var updateStatus = """
                UPDATE
                    exams
                SET
                    status = 'reported',
                    exam_report_id = ?
                WHERE
                    (status = 'published' OR status = 'refused') AND exam_session_id = ?
                """;
        PreparedStatement s4 = null;
        connection.setAutoCommit(false);
        try {
            s1 = connection.prepareStatement(createReport);
            var created_at = Timestamp.valueOf(LocalDateTime.now());
            s1.setTimestamp(1, created_at);
            s1.executeUpdate();

            s2 = connection.createStatement();
            var result = s2.executeQuery(selectReportId);
            result.next();
            var examReportID = result.getInt(1);

            s3 = connection.prepareStatement(updateRefused);
            s3.setInt(1, examSessionID);
            s3.executeUpdate();

            s4 = connection.prepareStatement(updateStatus);
            s4.setInt(1, examReportID);
            s4.setInt(2, examSessionID);
            s4.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
            if (s1 != null) s1.close();
            if (s2 != null) s1.close();
            if (s3 != null) s1.close();
            if (s4 != null) s1.close();
        }
    }

    public void refuse(int examId) throws SQLException {
        var query = """
                UPDATE
                    exams
                SET
                    status = 'refused'
                WHERE
                    id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examId);
            statement.executeUpdate();
        }
    }

    public boolean isThereAnyReportableExam(int examSessionId) throws SQLException {
        var query = """
                SELECT
                    COUNT(*)
                FROM
                    exams AS E
                    JOIN exam_sessions AS ES ON E.exam_session_id = ES.id
                    JOIN courses AS C ON ES.course_id = C.id
                    JOIN professors AS P ON C.professor_id = P.id
                    JOIN students AS S ON E.student_id = S.id
                WHERE
                    ES.id = ? AND E.status = 'published'
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examSessionId);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return false;
                result.next();
                return result.getInt(1) == 0 ? false : true;
            }
        }
    }

    public boolean isThereAnyPublishableExam(int examSessionId) throws SQLException {
        var query = """
                SELECT
                    COUNT(*)
                FROM
                    exams AS E
                    JOIN exam_sessions AS ES ON E.exam_session_id = ES.id
                    JOIN courses AS C ON ES.course_id = C.id
                    JOIN professors AS P ON C.professor_id = P.id
                    JOIN students AS S ON E.student_id = S.id
                WHERE
                    ES.id = ? AND E.status = 'inserted'
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, examSessionId);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return false;
                result.next();
                return result.getInt(1) == 0 ? false : true;
            }
        }
    }
}
