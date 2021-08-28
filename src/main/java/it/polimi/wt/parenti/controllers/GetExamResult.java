package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Student;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.Utility;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;

public class GetExamResult extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public GetExamResult() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var selectedExamSession = StringEscapeUtils.escapeJava(request.getParameter("examSessionId"));
        if ((selectedExamSession == null) || selectedExamSession.isBlank()) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing necessary \"examSessionId\" parameter!");
            return;
        }
        var s = (Student) request.getSession().getAttribute("student");
        var eDao = new ExamDAO(connection);
        try {
            var examSessionId = Integer.parseInt(selectedExamSession);
            var exam = eDao.getDetails(examSessionId, s.getId());
            if (exam.isPresent()) {
                var json = Utility.getJsonParser().toJson(exam);
                writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", json);
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
}
