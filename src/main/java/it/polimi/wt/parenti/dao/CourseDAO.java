package it.polimi.wt.parenti.dao;

import it.polimi.wt.parenti.beans.Course;
import it.polimi.wt.parenti.beans.ExamSession;
import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.beans.Student;

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
                    ES.id, ES.date,
                    C.id, C.code, C.name,
                    P.id, P.name, P.surname
                FROM
                    exam_sessions AS ES
                    JOIN courses AS C ON ES.course_id = C.id
                    JOIN professors AS P ON C.professor_id = P.id
                WHERE
                    C.id = ?
                ORDER BY
                    ES.date DESC
                """;
        var dates = new ArrayList<ExamSession>();
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseID);
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
                    var date = new ExamSession();
                    date.setId(result.getInt("ES.id"));
                    date.setDate(result.getDate("ES.date").toLocalDate());
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
                    S.id, S.matriculation, S.name, S.surname, S.email, S.bachelor_course
                FROM
                    students AS S
                    JOIN attendances AS A ON S.id = A.student_id
                WHERE
                    A.course_id = ?
                """;
        var students = new ArrayList<Student>();
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseID);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var s = new Student();
                    s.setId(result.getInt("S.id"));
                    s.setMatriculation(result.getInt("S.matriculation"));
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
