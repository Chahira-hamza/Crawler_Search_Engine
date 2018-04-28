package QueryPackage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

@WebServlet("QueryX")
public class QueryX extends HttpServlet {

    String queryInput;
    ResultSet results;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        queryInput = request.getParameter("SQuery");

        if (queryInput.isEmpty()){
            if(null != request.getParameter("Lumos")) {
                response.sendRedirect("index.jsp");
                return;
            }
            else if(null != request.getParameter("Felix Felicis")) {
                response.sendRedirect("https://www.pottermore.com/");
                return;
            }
        }

        try {
            HPEngine engine = new HPEngine(queryInput);
            results = engine.getresult();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
