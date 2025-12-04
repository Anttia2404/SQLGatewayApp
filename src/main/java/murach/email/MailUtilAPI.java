package murach.email;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Utility class để gửi email qua Elastic Email API (HTTP)
 * Sử dụng java.net.http.HttpClient (có sẵn từ Java 11)
 * Không cần SMTP - phù hợp cho Render free tier
 */
public class MailUtilAPI {
    
    private static final String ELASTIC_API_URL = "https://api.elasticemail.com/v2/email/send";
    
    /**
     * Gửi email qua Elastic Email API
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
        
        // Đọc Elastic Email API key từ environment variable
        String apiKey = System.getenv("ELASTIC_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalStateException(
                "ELASTIC_API_KEY environment variable is not set. " +
                "Please set it in your Render dashboard or local environment."
            );
        }
        
        // Tạo form data cho Elastic Email API
        // Format: https://elasticemail.com/developers/api-documentation/rest-api#operation/emailsPost
        String bodyType = bodyIsHTML ? "html" : "text";
        String formData = "apikey=" + urlEncode(apiKey) +
                         "&from=" + urlEncode(from) +
                         "&to=" + urlEncode(to) +
                         "&subject=" + urlEncode(subject) +
                         "&body" + bodyType + "=" + urlEncode(body);
        
        // Tạo HTTP client và request
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(ELASTIC_API_URL))
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
            System.out.println("Email sent successfully via Elastic Email API to: " + to);
            System.out.println("Response: " + response.body());
        } else {
            String errorMsg = String.format(
                "Failed to send email via Elastic Email API. Status: %d, Response: %s",
                statusCode,
                response.body()
            );
            System.err.println(errorMsg);
            throw new IOException(errorMsg);
        }
    }
    
    /**
     * URL encode string for form data
     */
    private static String urlEncode(String str) {
        if (str == null) return "";
        try {
            return java.net.URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }
}
