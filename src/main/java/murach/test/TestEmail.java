package murach.test;

import murach.email.MailUtilGmail;
import murach.email.MailUtilLocal;

/**
 * Test class để kiểm tra chức năng gửi email
 * Chạy class này như một Java Application để test email
 */
public class TestEmail {
    
    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU TEST EMAIL ===\n");
        
        // CHỌN TEST NÀO BẠN MUỐN CHẠY
        // Uncomment dòng tương ứng:
        
        // testGmailSMTP();      // Test Gmail SMTP (cần credentials)
        // testLocalSMTP();      // Test Local SMTP (cần local SMTP server)
        
        System.out.println("\n⚠️  Hãy uncomment một trong các test methods ở trên để chạy test!");
        System.out.println("=== KẾT THÚC TEST EMAIL ===");
    }
    
    /**
     * Test gửi email qua Gmail SMTP
     * YÊU CẦU: Phải cấu hình Gmail credentials trong MailUtilGmail.java
     */
    private static void testGmailSMTP() {
        System.out.println("--- Test Gmail SMTP ---");
        
        try {
            String to = "tanloc251095@gmail.com";        // ← Thay bằng email người nhận
            String from = "tanloc01293@gmail.com";       // ← Thay bằng Gmail của bạn
            String subject = "Test Email from SQLGatewayApp";
            String body = "This is a test email sent via Gmail SMTP.\n\n" +
                         "If you receive this, the email functionality is working!";
            boolean isBodyHTML = false;
            
            System.out.println("Đang gửi email...");
            System.out.println("To: " + to);
            System.out.println("From: " + from);
            System.out.println("Subject: " + subject);
            
            MailUtilGmail.sendMail(to, from, subject, body, isBodyHTML);
            
            System.out.println("\n✅ Email đã được gửi thành công qua Gmail SMTP!");
            System.out.println("Hãy kiểm tra inbox của: " + to);
            
        } catch (Exception e) {
            System.err.println("\n❌ LỖI khi gửi email:");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("\n💡 HƯỚNG DẪN KHẮC PHỤC:");
            System.out.println("1. Kiểm tra Gmail credentials trong MailUtilGmail.java");
            System.out.println("2. Đảm bảo đã tạo App Password (xem GMAIL_SETUP.md)");
            System.out.println("3. Kiểm tra kết nối Internet");
        }
    }
    
    /**
     * Test gửi email qua Local SMTP Server
     * YÊU CẦU: Phải có SMTP server chạy trên localhost:25
     */
    private static void testLocalSMTP() {
        System.out.println("--- Test Local SMTP ---");
        
        try {
            String to = "test@example.com";
            String from = "noreply@sqlgatewayapp.com";
            String subject = "Test Email from Local SMTP";
            String body = "This is a test email sent via Local SMTP server.\n\n" +
                         "If you receive this, the local SMTP is working!";
            boolean isBodyHTML = false;
            
            System.out.println("Đang gửi email...");
            System.out.println("To: " + to);
            System.out.println("From: " + from);
            System.out.println("Subject: " + subject);
            
            MailUtilLocal.sendMail(to, from, subject, body, isBodyHTML);
            
            System.out.println("\n✅ Email đã được gửi thành công qua Local SMTP!");
            
        } catch (Exception e) {
            System.err.println("\n❌ LỖI khi gửi email:");
            System.err.println("Message: " + e.getMessage());
            e.printStackTrace();
            
            System.out.println("\n💡 HƯỚNG DẪN KHẮC PHỤC:");
            System.out.println("1. Cài đặt local SMTP server (ví dụ: Papercut, FakeSMTP)");
            System.out.println("2. Chạy SMTP server trên localhost:25");
            System.out.println("3. Thử lại test");
        }
    }
    
    /**
     * Test gửi email HTML
     */
    private static void testHTMLEmail() {
        System.out.println("--- Test HTML Email ---");
        
        try {
            String to = "recipient@example.com";
            String from = "your-email@gmail.com";
            String subject = "Test HTML Email";
            String body = "<h1>Welcome!</h1>" +
                         "<p>This is a <strong>HTML</strong> email.</p>" +
                         "<ul>" +
                         "<li>Item 1</li>" +
                         "<li>Item 2</li>" +
                         "</ul>";
            boolean isBodyHTML = true;  // ← HTML format
            
            MailUtilGmail.sendMail(to, from, subject, body, isBodyHTML);
            
            System.out.println("✅ HTML email đã được gửi!");
            
        } catch (Exception e) {
            System.err.println("❌ LỖI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
