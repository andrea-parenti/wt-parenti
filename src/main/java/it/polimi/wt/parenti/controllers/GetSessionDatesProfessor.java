package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.dao.CourseDAO;
import it.polimi.wt.parenti.dao.ProfessorDAO;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.Utility;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serial;
import java.sql.SQLException;

@MultipartConfig
public class GetSessionDatesProfessor extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public GetSessionDatesProfessor() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        var selectedCourse = StringEscapeUtils.escapeJava(request.getParameter("courseId"));
        if ((selectedCourse == null) || selectedCourse.isBlank()) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing necessary \"courseId\" parameter!");
            return;
        }
        var p = (Professor) request.getSession().getAttribute("professor");
        var pDao = new ProfessorDAO(connection);
        var cDao = new CourseDAO(connection);
        try {
            var courseId = Integer.parseInt(selectedCourse);
            var checkedCourse = pDao.checkCourse(p.getId(), courseId);
            if (checkedCourse.isEmpty()) {
                writeHttpResponse(response, HttpServletResponse.SC_FORBIDDEN, "text/plain", "Forbidden query on course!");
                return;
            }
            var dates = cDao.findDates(checkedCourse.get().getId());
            if (!dates.isEmpty()) {
                var json = Utility.getJsonParser().toJson(dates);
                writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", json);
            } else {
                writeHttpResponse(response, HttpServletResponse.SC_NOT_FOUND, "text/plain", "No dates found for the requested course!");
            }
            return;
        } catch (NumberFormatException e) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Invalid \"courseId\" parameter!");
            e.printStackTrace();
        } catch (SQLException e) {
            writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
            e.printStackTrace();
        }
    }
}
