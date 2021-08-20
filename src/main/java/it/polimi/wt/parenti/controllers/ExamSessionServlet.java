package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Exam;
import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.dao.ProfessorDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
import it.polimi.wt.parenti.utils.enumerations.OrderType;
import it.polimi.wt.parenti.utils.enumerations.SortableField;
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
import java.util.List;

public class ExamSessionServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

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
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final var context = new WebContext(request, response, getServletContext(), request.getLocale());
        var session = request.getSession(false);
        var p = (Professor) session.getAttribute("professor");
        var pDao = new ProfessorDAO(connection);
        var eDao = new ExamDAO(connection);
        List<Exam> exams;
        var examSessionParam = StringEscapeUtils.escapeJava(request.getParameter("examSessionId"));
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
        try {
            if (pDao.checkExamSession(p.getId(), examSessionID).isEmpty()) {
                response.sendRedirect(request.getContextPath());
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        var sortingFieldParam = StringEscapeUtils.escapeJava(request.getParameter("orderBy"));
        if (sortingFieldParam == null) {
            session.setAttribute("sortingField", SortableField.MATRICULATION);
            session.setAttribute("sortingOrder", OrderType.ASC);
        } else {
            var sortingField = SortableField.fromString(sortingFieldParam);
            if (sortingField != null) {
                if (sortingField == session.getAttribute("sortingField"))
                    session.setAttribute("sortingOrder",
                            OrderType.invert(((OrderType) session.getAttribute("sortingOrder"))));
                else {
                    session.setAttribute("sortingField", sortingField);
                    session.setAttribute("sortingOrder", OrderType.ASC);
                }
            } else {
                session.setAttribute("sortingField", SortableField.MATRICULATION);
                session.setAttribute("sortingOrder", OrderType.ASC);
            }
        }
        try {
            exams = eDao.getSessionDetails(examSessionID,
                    (SortableField) session.getAttribute("sortingField"),
                    (OrderType) session.getAttribute("sortingOrder"));
            context.setVariable("exams", exams);
            context.setVariable("examSessionId", examSessionID);
            templateEngine.process("exam-session-details", context, response.getWriter());
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
