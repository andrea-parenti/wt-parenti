package it.polimi.wt.parenti.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession s = req.getSession(false);
        final var contextPath = req.getContextPath();
        final var servletPath = req.getServletPath();
        System.out.println("Login filter...\t" + servletPath);
        if (s == null || s.isNew() || s.getAttribute("user") == null) {
            res.sendRedirect(contextPath + "/Login");
            return;
        }
        chain.doFilter(request, response);
    }
}
