package it.polimi.wt_parenti.controllers;

import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.dao.UserDAO;
import it.polimi.wt_parenti.utils.ConnectionManager;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@WebServlet("/CheckLogin")
public class CheckLogin extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    public CheckLogin() {
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
        templateResolver.setSuffix(".html");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Only serves POST requests!");
        } catch (IOException e) {
            // do nothing
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        var username = request.getParameter("username");
        var password = request.getParameter("password");

        if ((username == null) || username.isBlank()) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing username!");
            } catch (IOException e) {
                // do nothing
            }
            return;
        }

        if ((password == null) || password.isBlank()) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing password!");
            } catch (IOException e) {
                // do nothing
            }
            return;
        }

        var userDao = new UserDAO(connection);
        Optional<User> user;
        try {
            user = userDao.checkCredentials(username, password);
        } catch (SQLException outerEx) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure in database credential checking!");
            } catch (IOException innerEx) {
                // do nothing
            }
            return;
        }

        var path = getServletContext().getContextPath();
        if (user.isEmpty()) {
            path = getServletContext().getContextPath() + "/index.html";
        } else {
            var u = user.get();
            request.getSession().setAttribute("user", u);
            /*
            TODO: in base al ruolo occorre gestire la situazione, settando un attributo in più in sessione e cambiando
                il path della redirect.
             */
        }

        try {
            response.sendRedirect(path);
        } catch (IOException e) {
            // do nothing
        }
    }
}
