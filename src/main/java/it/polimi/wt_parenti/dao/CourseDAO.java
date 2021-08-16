package it.polimi.wt_parenti.dao;

import it.polimi.wt_parenti.beans.Course;
import it.polimi.wt_parenti.beans.ExamSession;
import it.polimi.wt_parenti.beans.Professor;
import it.polimi.wt_parenti.beans.Student;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private final Connection connection;

    public CourseDAO(Connection connection) {
        this.connection = connection;
    }

    public List<ExamSession> findDates(int courseID) throws SQLException {
        var query = """
                SELECT
                    ES.exam_session_id, ES.date,
                    C.course_id, C.code, C.name,
                    P.professor_id, P.name, P.surname
                FROM
                    exam_sessions AS ES JOIN courses AS C ON ES.course_id = C.course_id JOIN professors AS P ON C.professor_id = P.professor_id
                WHERE
                    C.course_id = ?
                ORDER BY
                    ES.date DESC
                """;
        var dates = new ArrayList<ExamSession>();
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseID);
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
                    var date = new ExamSession();
                    date.setId(result.getInt("ES.exam_session_id"));
                    date.setDate(result.getDate("date").toLocalDate());
                    date.setCourse(c);
                    dates.add(date);
                }
                return dates;
            }
        }
    }

    public List<Student> getAttendingStudents(int courseID) throws SQLException {
        var query = """
                SELECT
                    S.student_id, S.name, S.surname, S.email, S.bachelor_course
                FROM
                    students AS S JOIN attendances AS A ON S.student_id = A.student_id
                WHERE
                    A.course_id = ?
                """;
        var students = new ArrayList<Student>();
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseID);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var s = new Student();
                    s.setId(result.getInt("S.student_id"));
                    s.setName(result.getString("S.name"));
                    s.setSurname(result.getString("S.surname"));
                    s.setEmail(result.getString("S.email"));
                    s.setBachelorCourse(result.getString("S.bachelor_course"));
                    students.add(s);
                }
                return students;
            }
        }
    }
}
