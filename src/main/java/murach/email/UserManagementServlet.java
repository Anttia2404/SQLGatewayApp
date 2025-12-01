package murach.email;

import java.io.*;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import murach.business.User;
import murach.data.UserDAO;

@WebServlet("/userManagement")
public class UserManagementServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get all users from database
        try {
            List<User> users = UserDAO.selectAll();
            request.setAttribute("users", users);
        } catch (Exception e) {
            request.setAttribute("message", "Error loading users: " + e.getMessage());
            request.setAttribute("messageType", "error");
            e.printStackTrace();
        }
        
        // Forward to users.jsp
        getServletContext()
                .getRequestDispatcher("/users.jsp")
                .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if ("delete".equals(action)) {
            String userIdStr = request.getParameter("userId");
            
            try {
                int userId = Integer.parseInt(userIdStr);
                
                // Delete user from database
                int result = UserDAO.delete(userId);
                
                if (result > 0) {
                    request.setAttribute("message", "User deleted successfully!");
                    request.setAttribute("messageType", "");
                } else {
                    request.setAttribute("message", "Failed to delete user.");
                    request.setAttribute("messageType", "error");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("message", "Invalid user ID.");
                request.setAttribute("messageType", "error");
            } catch (Exception e) {
                request.setAttribute("message", "Error deleting user: " + e.getMessage());
                request.setAttribute("messageType", "error");
                e.printStackTrace();
            }
        }
        
        // Reload users and forward to users.jsp
        doGet(request, response);
    }
}
