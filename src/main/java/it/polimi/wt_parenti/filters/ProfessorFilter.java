package it.polimi.wt_parenti.filters;

import it.polimi.wt_parenti.beans.Professor;
import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.utils.enumerations.UserRole;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "ProfessorFilter", urlPatterns = {"/HomeProfessor"})
public class ProfessorFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;
        var contextPath = req.getContextPath();
        var servletPath = req.getServletPath();
        var s = req.getSession(false);
        if (s == null) {
            res.sendRedirect(contextPath + "/Login");
            return;
        }
        var user = (User) s.getAttribute("user");
        var professor = (Professor) s.getAttribute("professor");
        System.out.println("Professor filter...\t" + servletPath);
        if (user.getRole() != UserRole.PROFESSOR) {
            res.sendRedirect(req.getContextPath());
            return;
        }
        if (professor == null) {
            s.invalidate();
            res.sendRedirect(contextPath + "/Login");
            return;
        }
        chain.doFilter(request, response);
    }
}
