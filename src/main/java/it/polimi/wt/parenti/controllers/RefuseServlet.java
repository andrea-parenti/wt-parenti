package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Student;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
import it.polimi.wt.parenti.utils.enumerations.ExamResult;
import it.polimi.wt.parenti.utils.enumerations.ExamStatus;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;

public class RefuseServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public RefuseServlet() {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final var context = new WebContext(request, response, getServletContext(), request.getLocale());
        var s = (Student) request.getSession().getAttribute("student");
        var eDao = new ExamDAO(connection);
        var parameter = StringEscapeUtils.escapeJava(request.getParameter("examId"));
        if (parameter != null) {
            try {
                var examID = Integer.parseInt(parameter);
                var e = eDao.getDetails(examID);
                if (e.isPresent() &&
                        e.get().getStudent().getId() == s.getId() &&
                        e.get().getResult() == ExamResult.PASSED &&
                        e.get().getStatus() == ExamStatus.PUBLISHED) {
                    eDao.refuse(examID);
                    response.sendRedirect(request.getContextPath() + "/ExamResult?examId=" + examID);
                }
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect(request.getContextPath());
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
