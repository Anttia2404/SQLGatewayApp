package murach.test;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import murach.business.User;
import murach.data.UserDAO;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/testDB")
public class TestDatabaseServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Database Test</title></head>");
        out.println("<body>");
        out.println("<h1>Database Connection Test</h1>");
        
        try {
            out.println("<h2>Testing UserDAO.selectAll()...</h2>");
            
            List<User> users = UserDAO.selectAll();
            
            out.println("<p><strong>Query executed successfully!</strong></p>");
            out.println("<p>Number of users found: " + users.size() + "</p>");
            
            if (users.isEmpty()) {
                out.println("<p style='color: orange;'>⚠️ No users in database</p>");
            } else {
                out.println("<h3>Users:</h3>");
                out.println("<ul>");
                for (User user : users) {
                    out.println("<li>");
                    out.println("ID: " + user.getUserId() + ", ");
                    out.println("Name: " + user.getFirstName() + " " + user.getLastName() + ", ");
                    out.println("Email: " + user.getEmail());
                    out.println("</li>");
                }
                out.println("</ul>");
            }
            
            out.println("<p style='color: green;'>✅ Database connection working!</p>");
            
        } catch (Exception e) {
            out.println("<p style='color: red;'>❌ Error occurred:</p>");
            out.println("<pre>");
            out.println("Error message: " + e.getMessage());
            out.println("\nStack trace:");
            e.printStackTrace(out);
            out.println("</pre>");
        }
        
        out.println("<hr>");
        out.println("<p><a href='/userManagement'>Go to User Management</a></p>");
        out.println("<p><a href='/sqlGateway'>Go to SQL Gateway</a></p>");
        out.println("</body>");
        out.println("</html>");
    }
}
