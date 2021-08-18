package it.polimi.wt_parenti.controllers;

import it.polimi.wt_parenti.beans.Exam;
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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ExamSessionServlet")
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
        List<Exam> exams;
        try {
            var examSessionParam = request.getParameter("examSessionId");
            int examSessionID;
            if (examSessionParam == null) {
                response.sendRedirect(request.getContextPath());
                return;
            }
            try {
                examSessionID = Integer.parseInt(examSessionParam);
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath());
                return;
            }
            if (pDao.checkExamSession(p.getId(), examSessionID).isEmpty()) {
                response.sendRedirect(request.getContextPath());
                return;
            }
            var sortingFieldParam = request.getParameter("orderBy");
            if (sortingFieldParam != null) {
                var tempField = SortableField.fromString(sortingFieldParam);
                if (tempField != null) {
                    if (tempField == sortingField) orderType = OrderType.invert(orderType);
                    else {
                        sortingField = tempField;
                        orderType = OrderType.ASC;
                    }
                } else {
                    sortingField = SortableField.SURNAME;
                    orderType = OrderType.ASC;
                }
            } else {
                sortingField = SortableField.SURNAME;
                orderType = OrderType.ASC;
            }
            exams = eDao.getSessionDetails(examSessionID, sortingField, orderType);
            context.setVariable("exams", exams);
            templateEngine.process("home-professor", context, response.getWriter());
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
