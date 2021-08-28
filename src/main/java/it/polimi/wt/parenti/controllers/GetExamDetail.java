package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.dao.ProfessorDAO;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.Utility;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;

@MultipartConfig
public class GetExamDetail extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public GetExamDetail() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var selectedExam = StringEscapeUtils.escapeJava(request.getParameter("examId"));
        if ((selectedExam == null) || selectedExam.isBlank()) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing necessary \"examId\" parameter!");
            return;
        }
        var p = (Professor) request.getSession().getAttribute("professor");
        var pDao = new ProfessorDAO(connection);
        try {
            var examId = Integer.parseInt(selectedExam);
            var checkedExam = pDao.checkExam(p.getId(), examId);
            if (checkedExam.isPresent()) {
                var json = Utility.getJsonParser().toJson(checkedExam.get());
                writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", json);
            } else {
                writeHttpResponse(response, HttpServletResponse.SC_NOT_FOUND, "text/plain", "No exams found!");
            }
            return;
        } catch (NumberFormatException e) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Invalid \"examId\" parameter!");
            e.printStackTrace();
        } catch (SQLException e) {
            writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
            e.printStackTrace();
        }
    }
}
