package murach.test;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/testJDBC")
public class TestJDBCServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>JDBC Test</title></head>");
        out.println("<body>");
        out.println("<h1>Direct JDBC Test (Same as SQL Gateway)</h1>");
        
        try {
            // Exact same code as SQL Gateway
            Class.forName("org.postgresql.Driver");
            
            String dbURL = "jdbc:postgresql://dpg-d47cvdi4d50c73834gmg-a.oregon-postgres.render.com:5432/murach";
            String username = "my_portfolio_db_vxq1_user";
            String password = "E3XY5g7Z35scTCzeB49CtUZFOAJVUiPG";
            
            Connection connection = DriverManager.getConnection(dbURL, username, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT \"user\", email, firstname, lastname FROM \"user\" ORDER BY \"user\"");
            
            out.println("<h2>Query: SELECT * FROM user ORDER BY userid</h2>");
            out.println("<table border='1'>");
            out.println("<tr><th>userid</th><th>email</th><th>firstname</th><th>lastname</th></tr>");
            
            int count = 0;
            while (resultSet.next()) {
                count++;
                out.println("<tr>");
                out.println("<td>" + resultSet.getLong("user") + "</td>");
                out.println("<td>" + resultSet.getString("email") + "</td>");
                out.println("<td>" + resultSet.getString("firstname") + "</td>");
                out.println("<td>" + resultSet.getString("lastname") + "</td>");
                out.println("</tr>");
            }
            
            out.println("</table>");
            out.println("<p><strong>Total users found: " + count + "</strong></p>");
            
            resultSet.close();
            statement.close();
            connection.close();
            
            out.println("<p style='color: green;'>✅ JDBC query successful!</p>");
            
        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Error:</p>");
            out.println("<pre>");
            out.println("Error message: " + e.getMessage());
            out.println("\nStack trace:");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.println("<hr>");
        out.println("<p><a href='/testDB'>Test UserDAO</a></p>");
        out.println("<p><a href='/userManagement'>User Management</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
