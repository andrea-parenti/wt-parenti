package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Exam;
import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.dao.ProfessorDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;

public class UpdateGradeServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public UpdateGradeServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        var servletContext = getServletContext();
        connection = ConnectionManager.openConnection(servletContext);
        var templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final var context = new WebContext(request, response, getServletContext(), request.getLocale());
        var session = request.getSession(false);
        var p = (Professor) session.getAttribute("professor");
        var pDao = new ProfessorDAO(connection);
        var eDao = new ExamDAO(connection);
        var examIdParam = StringEscapeUtils.escapeJava(request.getParameter("examId"));
        int examID;
        if (examIdParam == null) {
            response.sendRedirect(request.getContextPath());
            return;
        }
        try {
            examID = Integer.parseInt(examIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath());
            return;
        }
        try {
            var checkedExam = pDao.checkExam(p.getId(), examID);
            if (checkedExam.isEmpty()) {
                response.sendRedirect(request.getContextPath());
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            ConnectionManager.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
