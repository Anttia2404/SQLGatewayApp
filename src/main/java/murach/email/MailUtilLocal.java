package murach.email;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * Utility class để gửi email qua Local SMTP Server
 * Sử dụng cho môi trường development với SMTP server chạy trên localhost
 */
public class MailUtilLocal {
    
    /**
     * Gửi email qua local SMTP server (localhost:25)
     * 
     * @param to Địa chỉ email người nhận
     * @param from Địa chỉ email người gửi
     * @param subject Tiêu đề email
     * @param body Nội dung email
     * @param bodyIsHTML true nếu nội dung là HTML, false nếu là plain text
     * @throws MessagingException nếu có lỗi khi gửi email
     */
    public static void sendMail(String to, String from, 
                                String subject, String body, 
                                boolean bodyIsHTML) 
            throws MessagingException {
        
        // 1. Get a mail session (Lấy phiên mail)
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", 25);
        
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
        
        // 4. Send the message (Gửi tin nhắn)
        Transport.send(message);
    }
}
