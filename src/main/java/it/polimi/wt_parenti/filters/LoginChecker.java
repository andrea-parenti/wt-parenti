package it.polimi.wt_parenti.filters;

import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.dao.UserDAO;
import it.polimi.wt_parenti.utils.ConnectionManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

import static it.polimi.wt_parenti.utils.ServerLogger.LOGGER;

public class LoginChecker implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        LOGGER.info("Login checker filter executing ...");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String loginPath = req.getServletContext().getContextPath() + "/login.html";

        HttpSession s = req.getSession();
        if (s.isNew() || s.getAttribute("user") == null) {
            res.sendRedirect(loginPath);
            return;
        }

        var user = (User) s.getAttribute("user");
        var userDAO = new UserDAO(ConnectionManager.openConnection(request.getServletContext()));
        try {
            if (userDAO.checkUser(user)) {
                res.sendRedirect(loginPath);
                return;
            }
        } catch (SQLException e) {
            res.sendRedirect(loginPath);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
