package it.polimi.wt.parenti.dao;

import it.polimi.wt.parenti.beans.ExamReport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class ReportDAO {
    private final Connection connection;

    public ReportDAO(Connection connection) {
        this.connection = connection;
    }

    public Optional<ExamReport> getLastReport() throws SQLException {
        var query = """
                SELECT
                    LAST_INSERT_ID()
                FROM
                    exam_reports
                """;
        try (var statement = connection.createStatement()) {
            try (var result = statement.executeQuery(query)) {
                result.next();
                var examReportID = result.getInt(1);
                return getReport(examReportID);
            }
        }
    }

    public Optional<ExamReport> getReport(int reportId) throws SQLException {
        var query = """
                SELECT
                    *
                FROM
                    exam_reports
                WHERE
                    id = ?
                """;
        try (var statement = connection.prepareStatement(query)) {
            statement.setInt(1, reportId);
            try (var result = statement.executeQuery()) {
                if (!result.isBeforeFirst()) return Optional.empty();
                result.next();
                var er = new ExamReport();
                er.setId(result.getInt("id"));
                er.setCreation(result.getTimestamp("created_at").toLocalDateTime());
                return Optional.of(er);
            }
        }
    }
}
