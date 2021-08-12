package it.polimi.wt_parenti.filters;

import it.polimi.wt_parenti.beans.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "LoginFilter", urlPatterns = {"/", "/HomeStudent", "/HomeProfessor"})
public class LoginFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession s = req.getSession();
        final var contextPath = req.getContextPath();
        final var servletPath = req.getServletPath();
        final var user = (User) s.getAttribute("user");
        System.out.println("Login filter...\t" + servletPath);
        if (s.isNew() || user == null) {
            res.sendRedirect(contextPath + "/Login");
            return;
        }
        if (servletPath.equals("/")) {
            final var path = contextPath + switch (user.getRole()) {
                case STUDENT -> "/HomeStudent";
                case PROFESSOR -> "/HomeProfessor";
            };
            res.sendRedirect(path);
            return;
        }
        chain.doFilter(request, response);
    }
}
