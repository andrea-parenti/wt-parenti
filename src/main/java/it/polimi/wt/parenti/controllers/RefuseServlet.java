package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Student;
import it.polimi.wt.parenti.dao.ExamDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.enumerations.ExamResult;
import it.polimi.wt.parenti.utils.enumerations.ExamStatus;
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
public class RefuseServlet extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public RefuseServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
                    return;
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
