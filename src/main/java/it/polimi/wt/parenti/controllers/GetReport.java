package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Exam;
import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.dao.ProfessorDAO;
import it.polimi.wt.parenti.dao.ReportDAO;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.Utility;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@MultipartConfig
public class GetReport extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public GetReport() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var selectedReport = StringEscapeUtils.escapeJava(request.getParameter("reportId"));
        if ((selectedReport == null) || selectedReport.isBlank()) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing necessary \"reportId\" parameter!");
            return;
        }
        var p = (Professor) request.getSession().getAttribute("professor");
        var pDao = new ProfessorDAO(connection);
        var eDao = new ExamDAO(connection);
        var erDao = new ReportDAO(connection);
        try {
            var reportId = Integer.parseInt(selectedReport);
            var report = erDao.getReport(reportId);
            if (report.isEmpty()) {
                writeHttpResponse(response, HttpServletResponse.SC_NOT_FOUND, "text/plain", "No report found!");
            }
            var checkedReport = report.get();
            var examSessionId = eDao.getExamSessionIdByReport(checkedReport.getId());
            if (examSessionId.isEmpty()) {
                writeHttpResponse(response, HttpServletResponse.SC_NOT_FOUND, "text/plain", "No exam session found!");
            }
            var checkedExamSessionId = examSessionId.get();
            var checkedExamSession = pDao.checkExamSession(p.getId(), checkedExamSessionId);
            if (checkedExamSession.isEmpty()) {
                writeHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, "text/plain", "Forbidden query on course!");
                return;
            }
            var course = checkedExamSession.get().getCourse().getName();
            var date = checkedExamSession.get().getDate();
            var exams = eDao.getReportedExams(checkedReport.getId());
            if (!exams.isEmpty()) {
                var json = new ReportJson(checkedReport.getCreation(), course, date, exams);
                writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", json.serialize());
            } else {
                writeHttpResponse(response, HttpServletResponse.SC_NOT_FOUND, "text/plain", "No exams found for the requested exam session!");
            }
            return;
        } catch (NumberFormatException e) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Invalid \"examSessionId\" parameter!");
            e.printStackTrace();
        } catch (SQLException e) {
            writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
            e.printStackTrace();
        }
    }

    private class ReportJson implements Serializable {
        private final String creation;
        private final String course;
        private final String date;
        private final List<Exam> exams;

        private ReportJson(String creation, String course, String date, List<Exam> exams) {
            this.creation = creation;
            this.course = course;
            this.date = date;
            this.exams = new ArrayList<>(exams);
        }

        private String serialize() {
            return Utility.getJsonParser().toJson(this);
        }
    }
}
