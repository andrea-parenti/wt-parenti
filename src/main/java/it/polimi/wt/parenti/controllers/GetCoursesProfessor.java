package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.dao.CourseDAO;
import it.polimi.wt.parenti.dao.ProfessorDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.Utility;
import org.apache.commons.text.StringEscapeUtils;

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
public class GetCoursesProfessor extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public GetCoursesProfessor() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        var p = (Professor) request.getSession().getAttribute("professor");
        var pDao = new ProfessorDAO(connection);
        try {
            var courses = pDao.getTaughtCourses(p.getId());
            if (!courses.isEmpty()) {
                var json = Utility.getJsonParser().toJson(courses);
                writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", json);
            } else {
                writeHttpResponse(response, HttpServletResponse.SC_NOT_FOUND, "text/plain", "No taught courses found!");
            }
            return;
        } catch (SQLException e) {
            writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
            e.printStackTrace();
        }
    }
}
