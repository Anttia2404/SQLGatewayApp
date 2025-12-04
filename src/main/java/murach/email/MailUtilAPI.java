package murach.email;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Utility class để gửi email qua Resend API (HTTP)
 * Sử dụng java.net.http.HttpClient (có sẵn từ Java 11)
 * Không cần SMTP - phù hợp cho Render free tier
 */
public class MailUtilAPI {
    
    private static final String RESEND_API_URL = "https://api.resend.com/emails";
    
    /**
     * Gửi email qua Resend API
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
        
        // Đọc Resend API key từ environment variable
        String apiKey = System.getenv("RESEND_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException(
                "RESEND_API_KEY environment variable is not set. " +
                "Please set it in your Render dashboard or local environment."
            );
        }
        
        // Tạo JSON payload cho Resend API
        // Format: https://resend.com/docs/api-reference/emails/send-email
        String contentKey = bodyIsHTML ? "html" : "text";
        String jsonPayload = String.format("""
            {
              "from": "%s",
              "to": ["%s"],
              "subject": "%s",
              "%s": "%s"
            }
            """,
            escapeJson(from),
            escapeJson(to),
            escapeJson(subject),
            contentKey,
            escapeJson(body)
        );
        
        // Tạo HTTP client và request
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(RESEND_API_URL))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
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
            System.out.println("Email sent successfully via Resend API to: " + to);
            System.out.println("Response: " + response.body());
        } else {
            String errorMsg = String.format(
                "Failed to send email via Resend API. Status: %d, Response: %s",
                statusCode,
                response.body()
            );
            System.err.println(errorMsg);
            throw new IOException(errorMsg);
        }
    }
    
    /**
     * Escape special characters trong JSON string
     */
    private static String escapeJson(String str) {
        if (str == null) return "";
        return str
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
