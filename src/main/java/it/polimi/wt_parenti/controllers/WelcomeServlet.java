package it.polimi.wt_parenti.controllers;

import it.polimi.wt_parenti.beans.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "WelcomeServlet", value = "/")
public class WelcomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession s = request.getSession();
        final var user = (User) s.getAttribute("user");
        if (s.isNew() || user == null) {
            response.sendRedirect(getServletContext().getContextPath() + "/Login");
        } else {
            final var path = getServletContext().getContextPath() + switch (user.getRole()) {
                case STUDENT -> "/HomeStudent";
                case PROFESSOR -> "/HomeProfessor";
            };
            response.sendRedirect(path);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }
}
