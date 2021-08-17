package it.polimi.wt_parenti.controllers;

import it.polimi.wt_parenti.beans.Professor;
import it.polimi.wt_parenti.dao.ExamDAO;
import it.polimi.wt_parenti.dao.ProfessorDAO;
import it.polimi.wt_parenti.utils.ConnectionManager;
import it.polimi.wt_parenti.utils.enumerations.OrderType;
import it.polimi.wt_parenti.utils.enumerations.SortableField;
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

@WebServlet(name = "ExamSessionServlet", value = "/RegisteredStudents")
public class ExamSessionServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private OrderType orderType;
    private SortableField sortingField;

    public ExamSessionServlet() {
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
        orderType = OrderType.ASC;
        sortingField = SortableField.SURNAME;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final var context = new WebContext(request, response, getServletContext(), request.getLocale());
        var p = (Professor) request.getSession().getAttribute("professor");
        var pDao = new ProfessorDAO(connection);
        var eDao = new ExamDAO(connection);
        try {
            var parameter = request.getParameter("examSessionId");
            if (parameter != null) {
                try {
                    throw new SQLException(); // just to avoid an error temporarily
                } catch (NumberFormatException e) {
                    // do nothing
                }
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
