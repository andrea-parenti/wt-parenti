package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Student;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.enumerations.ExamResult;
import it.polimi.wt.parenti.utils.enumerations.ExamStatus;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;

@MultipartConfig
public class Refuse extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public Refuse() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var selectedExamId = StringEscapeUtils.escapeJava(request.getParameter("examId"));
        if ((selectedExamId == null) || selectedExamId.isBlank()) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing necessary \"examId\" parameter!");
            return;
        }
        var s = (Student) request.getSession().getAttribute("student");
        var eDao = new ExamDAO(connection);
        try {
            var examId = Integer.parseInt(selectedExamId);
            var e = eDao.getDetails(examId);
            if (e.isPresent() &&
                    e.get().getStudent().getId() == s.getId() &&
                    e.get().getResult() == ExamResult.PASSED &&
                    e.get().getStatus() == ExamStatus.PUBLISHED) {
                eDao.refuse(examId);
                writeHttpResponse(response, HttpServletResponse.SC_OK, "text/plain", null);
            } else {
                writeHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, "text/plain", "Forbidden query on course!");
            }
        } catch (NumberFormatException e) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Invalid \"examId\" parameter!");
            e.printStackTrace();
        } catch (SQLException e) {
            writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
            e.printStackTrace();
        }
    }
}
