package it.polimi.wt.parenti.utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@MultipartConfig
public abstract class Controller extends HttpServlet {
    protected Connection connection;

    @Override
    public void init() throws ServletException {
        final var servletContext = getServletContext();
        connection = ConnectionManager.openConnection(servletContext);
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            ConnectionManager.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static final void writeHttpResponse(HttpServletResponse response, int sc, String mime, String content)  {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(sc);
        response.setContentType(mime);
        try {
            response.getWriter().println(content);
        } catch (IOException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
