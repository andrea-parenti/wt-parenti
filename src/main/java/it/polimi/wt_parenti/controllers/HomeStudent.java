package it.polimi.wt_parenti.controllers;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "HomeStudent", value = "/HomeStudent")
public class HomeStudent extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // not implemented yet
    }
}
