package it.polimi.wt_parenti.controllers;

import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.dao.UserDAO;
import it.polimi.wt_parenti.utils.ConnectionManager;
import org.apache.commons.text.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet(name = "CheckLogin", value = "/Login")
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
        HttpSession s = request.getSession();
        if (s.isNew() || s.getAttribute("user") == null) {
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
            final var context = new WebContext(request, response, getServletContext(), request.getLocale());
            context.setVariable("errorMsg", "Missing username!");
            templateEngine.process("login", context, response.getWriter());
            return;
        }

        if ((password == null) || password.isBlank()) {
            final var context = new WebContext(request, response, getServletContext(), request.getLocale());
            context.setVariable("errorMsg", "Missing password!");
            templateEngine.process("login", context, response.getWriter());
            return;
        }

        var userDao = new UserDAO(connection);
        Optional<User> user;
        try {
            user = userDao.checkCredentials(username, password);
        } catch (SQLException e) {
            // error handling servlet?
            return;
        }

        if (user.isEmpty()) {
            final var context = new WebContext(request, response, getServletContext(), request.getLocale());
            context.setVariable("errorMsg", "Incorrect credentials!");
            templateEngine.process("login", context, response.getWriter());
            return;
        }

        var u = user.get();
        request.getSession().setAttribute("user", u);
        var path = getServletContext().getContextPath();
        path += switch (u.getRole()) {
            case STUDENT -> "/HomeStudent";
            case PROFESSOR -> "/HomeProfessor";
        };
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
