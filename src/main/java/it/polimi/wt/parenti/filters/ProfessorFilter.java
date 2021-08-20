package it.polimi.wt.parenti.filters;

import it.polimi.wt.parenti.beans.Professor;
import it.polimi.wt.parenti.beans.User;
import it.polimi.wt.parenti.utils.enumerations.UserRole;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProfessorFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;
        var contextPath = req.getContextPath();
        var servletPath = req.getServletPath();
        var s = req.getSession(false);
        if (s == null || s.isNew() || s.getAttribute("user") == null) {
            res.sendRedirect(contextPath + "/Login");
            return;
        }
        var user = (User) s.getAttribute("user");
        System.out.println("Professor filter...\t" + servletPath);
        if (user.getRole() != UserRole.PROFESSOR) {
            res.sendRedirect(req.getContextPath());
            return;
        }
        if (s.getAttribute("professor") == null) {
            s.invalidate();
            res.sendRedirect(contextPath + "/Login");
            return;
        }
        chain.doFilter(request, response);
    }
}
