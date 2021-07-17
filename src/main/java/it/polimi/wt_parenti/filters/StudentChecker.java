package it.polimi.wt_parenti.filters;

import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.utils.enumerations.UserRole;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static it.polimi.wt_parenti.utils.ServerLogger.LOGGER;

public class StudentChecker implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        LOGGER.info("Student checker filter executing ...");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String loginPath = req.getServletContext().getContextPath() + "/login.html";

        HttpSession s = req.getSession();
        var u = (User) s.getAttribute("user");
        if (u == null || u.getRole() != UserRole.STUDENT) {
            res.sendRedirect(loginPath);
            return;
        }

        // TODO: server-side login check (student's id = user's id)

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
