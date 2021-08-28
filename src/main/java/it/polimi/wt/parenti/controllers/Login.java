package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.User;
import it.polimi.wt.parenti.dao.UserDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
import it.polimi.wt.parenti.utils.Controller;
import it.polimi.wt.parenti.utils.Utility;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

@MultipartConfig
public class Login extends Controller {
    @Serial
    private static final long serialVersionUID = 1L;

    public Login() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        var username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        var password = StringEscapeUtils.escapeJava(request.getParameter("password"));

        if ((username == null) || username.isBlank()) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing username!");
            return;
        }

        if ((password == null) || password.isBlank()) {
            writeHttpResponse(response, HttpServletResponse.SC_BAD_REQUEST, "text/plain", "Missing password!");
            return;
        }

        var userDao = new UserDAO(connection);
        User user;
        try {
            var u = userDao.checkCredentials(username, password);
            if (u.isEmpty()) {
                writeHttpResponse(response, HttpServletResponse.SC_BAD_GATEWAY, "text/plain", "Incorrect credentials!");
                return;
            }
            user = u.get();
        } catch (SQLException e) {
            writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
            return;
        }

        switch (user.getRole()) {
            case STUDENT:
                try {
                    var s = userDao.associateStudent(user);
                    if (s.isEmpty()) {
                        writeHttpResponse(response, HttpServletResponse.SC_BAD_GATEWAY, "text/plain", "No student found: database error!");
                        return;
                    }
                    var student = s.get();
                    var userAttr = new UserAttributes<>(user, student);
                    writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", userAttr.serialize());
                    request.getSession().setAttribute("user", user);
                    request.getSession().setAttribute("student", student);
                } catch (SQLException e) {
                    writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
                    return;
                }
                break;
            case PROFESSOR:
                try {
                    var p = userDao.associateProfessor(user);
                    if (p.isEmpty()) {
                        writeHttpResponse(response, HttpServletResponse.SC_BAD_GATEWAY, "text/plain", "No professor found: database error!");
                        return;
                    }
                    var professor = p.get();
                    var userAttr = new UserAttributes<>(user, professor);
                    writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", userAttr.serialize());
                    request.getSession().setAttribute("user", user);
                    request.getSession().setAttribute("professor", professor);
                } catch (SQLException e) {
                    writeHttpResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "text/plain", "Error on database query or connection!");
                    return;
                }
                break;
        }
    }

    private class UserAttributes<T> implements Serializable {
        private final User loginData;
        private final T personalData;

        private UserAttributes(User loginData, T personalData) {
            this.loginData = loginData;
            this.personalData = personalData;
        }

        private String serialize() {
            return Utility.getJsonParser().toJson(this);
        }
    }
}
