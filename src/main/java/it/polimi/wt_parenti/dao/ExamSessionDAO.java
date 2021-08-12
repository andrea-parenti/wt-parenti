package it.polimi.wt_parenti.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExamSessionDAO {
    private final Connection connection;

    public ExamSessionDAO(Connection connection) {
        this.connection = connection;
    }

    public List<LocalDate> findDates(int courseID) throws SQLException {
        var query = """
                SELECT
                    date
                FROM
                    exam_sessions
                WHERE
                    course_id = ?
                ORDER BY
                    date DESC
                """;
        var dates = new ArrayList<LocalDate>();
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, courseID);
            try (var result = statement.executeQuery()) {
                while (result.next()) {
                    var date = result.getDate("date").toLocalDate();
                    dates.add(date);
                }
                return dates;
            }
        }
    }
}
