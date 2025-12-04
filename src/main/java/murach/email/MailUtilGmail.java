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
        // Sử dụng port 465 với SSL thay vì port 587 (STARTTLS)
        // vì Render free tier chặn port 587 nhưng không chặn 465
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");  // Enable SSL
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        
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
        transport.connect("smtp.gmail.com", 465, username, password);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }
}
