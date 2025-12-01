package murach.email;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import murach.business.User;
import murach.data.UserDAO;


public class EmailListServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        String url = "/index.html";
        
        // get current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "join";  // default action
        }
        
        // perform action and set URL to appropriate page
        if (action.equals("join")) {
            url = "/index.jsp";  // the "join" page
        }
        else if (action.equals("add")) {
            // get parameters from the request
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            
            // store data in User object
            User user = new User(firstName, lastName, email);
            
            // validate the parameters
            String message;
            if (UserDAO.emailExists(user.getEmail())) {
                message = "This email address already exists.<br>" +
                         "Please enter another email address.";
                url = "/index.jsp";
            }
            else {
                message = "";
                url = "/thanks.jsp";
                UserDAO.insert(user);
                
                // Send confirmation email
                try {
                    String to = email;
                    String from = "noreply@sqlgatewayapp.com";
                    String subject = "Welcome to our Email List";
                    String body = "Dear " + firstName + ",\n\n" +
                                "Thank you for joining our email list!\n\n" +
                                "Your information:\n" +
                                "Name: " + firstName + " " + lastName + "\n" +
                                "Email: " + email + "\n\n" +
                                "Best regards,\n" +
                                "SQL Gateway App Team";
                    boolean isBodyHTML = false;
                    
                    // Sử dụng Gmail SMTP
                    MailUtilGmail.sendMail(to, from, subject, body, isBodyHTML);
                    
                    System.out.println("Confirmation email sent to: " + email);
                } catch (Exception e) {
                    System.err.println("Error sending email: " + e.getMessage());
                    e.printStackTrace();
                    // Không throw exception - vẫn cho phép user đăng ký thành công
                }
            }
            request.setAttribute("user", user);
            request.setAttribute("message", message);
        }
        getServletContext()
                .getRequestDispatcher(url)
                .forward(request, response);
    }
}
