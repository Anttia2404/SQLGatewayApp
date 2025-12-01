package murach.email;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Utility class để gửi email qua Gmail SMTP Server
 * Yêu cầu Gmail credentials (email và App Password)
 */
public class MailUtilGmail {
    
    /**
     * Gửi email qua Gmail SMTP server
     * 
     * @param to Địa chỉ email người nhận
     * @param from Địa chỉ email người gửi (phải là Gmail account)
     * @param subject Tiêu đề email
     * @param body Nội dung email
     * @param bodyIsHTML true nếu nội dung là HTML, false nếu là plain text
     * @throws MessagingException nếu có lỗi khi gửi email
     */
    public static void sendMail(String to, String from, 
                                String subject, String body, 
                                boolean bodyIsHTML) 
            throws MessagingException {
        
        // Gmail credentials - Đọc từ Environment Variables (an toàn cho production)
        // Hoặc fallback về hardcoded values cho local development
        final String username = System.getenv("GMAIL_USERNAME") != null 
                              ? System.getenv("GMAIL_USERNAME") 
                              : "tanloc01293@gmail.com";
        final String password = System.getenv("GMAIL_PASSWORD") != null 
                              ? System.getenv("GMAIL_PASSWORD") 
                              : "ifzv vjpc gspu xglp";
        
        // 1. Get a mail session (Lấy phiên mail)
        // Sử dụng port 587 với STARTTLS thay vì port 465 (SSL)
        // vì Render có thể chặn port 465
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);  // Bật debug để xem log
        
        // 2. Create a message (Tạo tin nhắn)
        Message message = new MimeMessage(session);
        message.setSubject(subject);
        
        if (bodyIsHTML) {
            message.setContent(body, "text/html");
        } else {
            message.setText(body);
        }
        
        // 3. Address the message (Đặt địa chỉ)
        Address fromAddress = new InternetAddress(from);
        Address toAddress = new InternetAddress(to);
        
        message.setFrom(fromAddress);
        message.setRecipient(Message.RecipientType.TO, toAddress);
        
        // 4. Send the message (Gửi tin nhắn - có xác thực)
        Transport transport = session.getTransport("smtp");
        transport.connect("smtp.gmail.com", 587, username, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
