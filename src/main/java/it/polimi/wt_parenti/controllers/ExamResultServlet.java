package it.polimi.wt_parenti.controllers;

import it.polimi.wt_parenti.beans.Student;
import it.polimi.wt_parenti.dao.ExamDAO;
import it.polimi.wt_parenti.utils.ConnectionManager;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "ExamResultServlet")
public class ExamResultServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public ExamResultServlet() {
        super();
    }

    @Override
    public void init() throws ServletException {
        final var servletContext = getServletContext();
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
        var s = (Student) request.getSession().getAttribute("student");
        var eDao = new ExamDAO(connection);
        var parameter = request.getParameter("examSessionId");
        if (parameter != null) {
            try {
                var examSessionID = Integer.parseInt(parameter);
                var e = eDao.getDetails(examSessionID, s.getId());
                e.ifPresent(exam -> context.setVariable("exam", exam));
            } catch (NumberFormatException | SQLException e) {
                e.printStackTrace();
            }
        }
        templateEngine.process("exam-details", context, response.getWriter());
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
