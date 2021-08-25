package it.polimi.wt.parenti.controllers;

import it.polimi.wt.parenti.beans.User;
import it.polimi.wt.parenti.dao.UserDAO;
import it.polimi.wt.parenti.utils.ConnectionManager;
import it.polimi.wt.parenti.utils.Utility;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public LoginServlet() {
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
        HttpSession s = request.getSession(false);
        if (s == null || s.isNew() || s.getAttribute("user") == null) {
            final var context = new WebContext(request, response, getServletContext(), request.getLocale());
            templateEngine.process("login", context, response.getWriter());
        } else {
            var user = (User) s.getAttribute("user");
            String path = getServletContext().getContextPath() + switch (user.getRole()) {
                case STUDENT -> "/HomeStudent";
                case PROFESSOR -> "/HomeProfessor";
            };
            response.sendRedirect(path);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        var password = StringEscapeUtils.escapeJava(request.getParameter("password"));

        if ((username == null) || username.isBlank()) {
            var err = new HttpError(HttpServletResponse.SC_BAD_REQUEST, "Missing username!");
            Utility.writeHttpResponse(response, err.statusCode,
                    "application/json", err.serialize());
            return;
        }

        if ((password == null) || password.isBlank()) {
            var err = new HttpError(HttpServletResponse.SC_BAD_REQUEST, "Missing password!");
            Utility.writeHttpResponse(response, err.statusCode,
                    "application/json", err.serialize());
            return;
        }

        var userDao = new UserDAO(connection);
        User user;
        try {
            var u = userDao.checkCredentials(username, password);
            if (u.isEmpty()) {
                var err = new HttpError(HttpServletResponse.SC_BAD_GATEWAY, "Incorrect credentials!");
                Utility.writeHttpResponse(response, err.statusCode,
                        "application/json", err.serialize());
                return;
            }
            user = u.get();
        } catch (SQLException e) {
            var err = new HttpError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error on database query or connection!");
            Utility.writeHttpResponse(response, err.statusCode,
                    "application/json", err.serialize());
            return;
        }

        switch (user.getRole()) {
            case STUDENT:
                try {
                    var s = userDao.associateStudent(user);
                    if (s.isEmpty()) {
                        var err = new HttpError(HttpServletResponse.SC_BAD_GATEWAY, "No student found: database error!");
                        Utility.writeHttpResponse(response, err.statusCode,
                                "application/json", err.serialize());
                        return;
                    }
                    var student = s.get();
                    var userAttr = new UserAttributes<>(user, student);
                    Utility.writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", userAttr.serialize());
                    request.getSession().setAttribute("user", user);
                    request.getSession().setAttribute("student", student);
                } catch (SQLException e) {
                    var err = new HttpError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error on database query or connection!");
                    Utility.writeHttpResponse(response, err.statusCode,
                            "application/json", err.serialize());
                    return;
                }
                break;
            case PROFESSOR:
                try {
                    var p = userDao.associateProfessor(user);
                    if (p.isEmpty()) {
                        var err = new HttpError(HttpServletResponse.SC_BAD_GATEWAY, "No professor found: database error!");
                        Utility.writeHttpResponse(response, err.statusCode,
                                "application/json", err.serialize());
                        return;
                    }
                    var professor = p.get();
                    var userAttr = new UserAttributes<>(user, professor);
                    Utility.writeHttpResponse(response, HttpServletResponse.SC_OK, "application/json", userAttr.serialize());
                    request.getSession().setAttribute("user", user);
                    request.getSession().setAttribute("professor", professor);
                } catch (SQLException e) {
                    var err = new HttpError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error on database query or connection!");
                    Utility.writeHttpResponse(response, err.statusCode,
                            "application/json", err.serialize());
                    return;
                }
                break;
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

    private class HttpError implements Serializable {
        private final int statusCode;
        private final String displayMessage;

        private HttpError(int statusCode, String displayMessage) {
            this.statusCode = statusCode;
            this.displayMessage = displayMessage;
        }

        private String serialize() {
            return Utility.getJsonParser().toJson(this);
        }
    }
}
