package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Student;
import it.polimi.wt.parenti.dao.CourseDAO;
import it.polimi.wt.parenti.dao.StudentDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
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

public class HomeStudentServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public HomeStudentServlet() {
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
        var sDao = new StudentDAO(connection);
        var cDao = new CourseDAO(connection);
        try {
            var courses = sDao.getAttendedCourses(s.getId());
            if (!courses.isEmpty()) {
                var parameter = StringEscapeUtils.escapeJava(request.getParameter("selectedCourse"));
                var selectedCourse = courses.get(0);
                if (parameter != null) {
                    try {
                        var selectedCourseID = Integer.parseInt(parameter);
                        var checkedCourse = sDao.checkCourse(s.getId(), selectedCourseID);
                        if (checkedCourse.isPresent()) selectedCourse = checkedCourse.get();
                    } catch (NumberFormatException e) {
                        // do nothing
                    }
                }
                var dates = cDao.findDates(selectedCourse.getId());
                context.setVariable("courses", courses);
                context.setVariable("selection", selectedCourse);
                context.setVariable("dates", dates);
            }
            templateEngine.process("home-student", context, response.getWriter());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
