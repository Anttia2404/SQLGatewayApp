package murach.email;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Utility class để gửi email qua Formspree API (HTTP)
 * Sử dụng java.net.http.HttpClient (có sẵn từ Java 11)
 * Không cần SMTP - phù hợp cho Render free tier
 */
public class MailUtilAPI {
    
    /**
     * Gửi email qua Formspree API
     * 
     * @param to Địa chỉ email người nhận
     * @param from Địa chỉ email người gửi
     * @param subject Tiêu đề email
     * @param body Nội dung email
     * @param bodyIsHTML true nếu nội dung là HTML, false nếu là plain text
     * @throws IOException nếu có lỗi khi gửi email
     * @throws InterruptedException nếu request bị gián đoạn
     */
    public static void sendMail(String to, String from, 
                                String subject, String body, 
                                boolean bodyIsHTML) 
            throws IOException, InterruptedException {
        
        // Đọc Formspree Form ID từ environment variable
        String formId = System.getenv("FORMSPREE_FORM_ID");
        if (formId == null || formId.isEmpty()) {
            throw new IllegalStateException(
                "FORMSPREE_FORM_ID environment variable is not set. " +
                "Please set it in your Render dashboard or local environment. " +
                "Example: FORMSPREE_FORM_ID=abc123xyz"
            );
        }
        
        String formspreeUrl = "https://formspree.io/f/" + formId;
        
        // Tạo form data (application/x-www-form-urlencoded)
        // Formspree expects: email, subject, message
        String formData = String.format(
            "email=%s&_replyto=%s&subject=%s&message=%s&name=%s",
            urlEncode(to),
            urlEncode(from),
            urlEncode(subject),
            urlEncode(body),
            urlEncode("SQLGatewayApp User")
        );
        
        // Tạo HTTP client và request
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(formspreeUrl))
            .header("Accept", "application/json")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(formData))
            .timeout(Duration.ofSeconds(30))
            .build();
        
        // Gửi request
        HttpResponse<String> response = client.send(
            request, 
            HttpResponse.BodyHandlers.ofString()
        );
        
        // Kiểm tra response
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            System.out.println("Email sent successfully via Formspree to: " + to);
            System.out.println("Response: " + response.body());
        } else {
            String errorMsg = String.format(
                "Failed to send email via Formspree. Status: %d, Response: %s",
                statusCode,
                response.body()
            );
            System.err.println(errorMsg);
            throw new IOException(errorMsg);
        }
    }
    
    /**
     * URL encode string cho form data
     */
    private static String urlEncode(String str) {
        if (str == null) return "";
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
