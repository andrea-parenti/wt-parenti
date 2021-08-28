package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Student;
import it.polimi.wt.parenti.dao.StudentDAO;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.Utility;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serial;
import java.sql.SQLException;

@MultipartConfig
public class GetCoursesStudent extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public GetCoursesStudent() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        var s = (Student) request.getSession().getAttribute("student");
        var sDao = new StudentDAO(connection);
        try {
            var courses = sDao.getAttendedCourses(s.getId());
            if (!courses.isEmpty()) {
                var json = Utility.getJsonParser().toJson(courses);
                writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", json);
            } else {
                writeHttpResponse(response, HttpServletResponse.SC_NOT_FOUND, "text/plain", "No attended courses found!");
            }
            return;
        } catch (SQLException e) {
            writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
            e.printStackTrace();
        }
    }
}
