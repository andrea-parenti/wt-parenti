package it.polimi.wt_parenti.dao;

import it.polimi.wt_parenti.beans.Course;
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

    public List<Student> getAttendingStudents(int courseID) throws SQLException {
        var query = """
                SELECT
                    student_id, name, surname, email, bachelor_course
                FROM
                    students NATURAL JOIN attendances
                WHERE
                    course_id = ?
                """;
        var students = new ArrayList<Student>();
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseID);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var s = new Student();
                    s.setId(result.getInt("student_id"));
                    s.setName(result.getString("name"));
                    s.setSurname(result.getString("surname"));
                    s.setEmail(result.getString("email"));
                    s.setBachelorCourse(result.getString("bachelor_course"));
                    students.add(s);
                }
                return students;
            }
        }
    }
}
