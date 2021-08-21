package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.dao.ProfessorDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
import it.polimi.wt.parenti.utils.Constants;
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
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UpdateGradeServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;
    private static final List<String> options = Constants.initializeOptions();

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
        try {
            var e = eDao.getDetails(examID);
            e.ifPresent(exam -> {
                context.setVariable("options", options);
                context.setVariable("exam", exam);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        templateEngine.process("update-grade", context, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var session = request.getSession(false);
        var p = (Professor) session.getAttribute("professor");
        var pDao = new ProfessorDAO(connection);

        var examIdParam = StringEscapeUtils.escapeJava(request.getParameter("examId"));
        int examID;
        int examSessionId = 0;
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
                // log out
            } else {
                var exam = checkedExam.get();
                examSessionId = exam.getExamSession().getId();
                if (exam.getStatus() != ExamStatus.NOT_INSERTED_YET && exam.getStatus() != ExamStatus.INSERTED) {
                    // cannot modify redirect to exam session
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        var gradeParam = StringEscapeUtils.escapeJava(request.getParameter("grade"));
        ExamResult result = null;
        Integer grade = null;
        Boolean laude = null;
        if (gradeParam == null || !options.contains(gradeParam)) {
            response.sendRedirect(request.getContextPath());
            return;
        }
        result = ExamResult.fromString(gradeParam);
        if (result == null || result == ExamResult.PASSED) {
            if (gradeParam.equals("30L")) {
                grade = 30;
                laude = true;
                result = ExamResult.PASSED;
            } else {
                try {
                    grade = Integer.parseInt(gradeParam);
                    if (grade < 18 || grade > 30) {
                        // show error in the current page
                    }
                    result = ExamResult.PASSED;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    // show error in the current page
                }
            }
        }
        var eDao = new ExamDAO(connection);
        try {
            eDao.updateGrade(examID, result, grade, laude);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        var path = request.getContextPath();
        if (examSessionId != 0) path += "/RegisteredStudents?examSessionId=" + examSessionId;
        else path += "/HomeProfessor";
        response.sendRedirect(path);
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
