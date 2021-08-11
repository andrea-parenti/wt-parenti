package it.polimi.wt_parenti.filters;

import it.polimi.wt_parenti.beans.User;
import it.polimi.wt_parenti.utils.enumerations.UserRole;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "StudentFilter")
public class StudentFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        System.out.println("Student filter...\n");
        var req = (HttpServletRequest) request;
        var res = (HttpServletResponse) response;
        var s = req.getSession();
        var user = (User) s.getAttribute("user");
        if (user.getRole() != UserRole.STUDENT) {
            res.sendRedirect(req.getContextPath());
            return;
        }
        chain.doFilter(request, response);
    }
}
