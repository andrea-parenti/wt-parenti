package it.polimi.wt_parenti;

import it.polimi.wt_parenti.utils.ConnectionManager;

import java.io.*;
import javax.servlet.UnavailableException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    @Override
    public void init() {
        message = "Hello World!";
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html");

        try {
            ConnectionManager.openConnection(request.getServletContext());
        } catch (UnavailableException e) {
            message = "Aiuto: " + e.getMessage();
        }

        // Hello
        try {
            var out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>" + message + "</h1>");
            out.println("</body></html>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        // non è interessante per la demo
    }
}