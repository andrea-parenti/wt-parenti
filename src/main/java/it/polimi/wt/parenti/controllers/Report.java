package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.dao.ProfessorDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
import it.polimi.wt.parenti.utils.Controller;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;

@MultipartConfig
public class Report extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public Report() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var selectedExamSession = StringEscapeUtils.escapeJava(request.getParameter("examSessionId"));
        if ((selectedExamSession == null) || selectedExamSession.isBlank()) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing necessary \"examSessionId\" parameter!");
            return;
        }
        var p = (Professor) request.getSession().getAttribute("professor");
        var pDao = new ProfessorDAO(connection);
        var eDao = new ExamDAO(connection);
        try {
            var examSessionId = Integer.parseInt(selectedExamSession);
            var checkedExamSession = pDao.checkExamSession(p.getId(), examSessionId);
            if (checkedExamSession.isEmpty()) {
                writeHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, "text/plain", "Forbidden query on course!");
                return;
            }
            eDao.publish(examSessionId);
            writeHttpResponse(response, HttpServletResponse.SC_OK, "text/plain", null);
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
