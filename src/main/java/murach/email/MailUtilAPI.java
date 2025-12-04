package murach.email;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Utility class để gửi email qua Brevo API (HTTP)
 * Sử dụng java.net.http.HttpClient (có sẵn từ Java 11)
 * Không cần SMTP - phù hợp cho Render free tier
 */
public class MailUtilAPI {
    
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    
    /**
     * Gửi email qua Brevo API
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
        
        // Đọc Brevo API key từ environment variable
        String apiKey = System.getenv("BREVO_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException(
                "BREVO_API_KEY environment variable is not set. " +
                "Please set it in your Render dashboard or local environment."
            );
        }
        
        // Tạo JSON payload cho Brevo API
        // Format: https://developers.brevo.com/reference/sendtransacemail
        String contentKey = bodyIsHTML ? "htmlContent" : "textContent";
        String jsonPayload = "{" +
            "\"sender\":{\"email\":\"" + escapeJson(from) + "\"}," +
            "\"to\":[{\"email\":\"" + escapeJson(to) + "\"}]," +
            "\"subject\":\"" + escapeJson(subject) + "\"," +
            "\"" + contentKey + "\":\"" + escapeJson(body) + "\"" +
            "}";
        
        // Tạo HTTP client và request
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BREVO_API_URL))
            .header("accept", "application/json")
            .header("api-key", apiKey)
            .header("content-type", "application/json")
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
            System.out.println("Email sent successfully via Brevo API to: " + to);
            System.out.println("Response: " + response.body());
        } else {
            String errorMsg = String.format(
                "Failed to send email via Brevo API. Status: %d, Response: %s",
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
