# Hướng dẫn Gửi Email trên Localhost - Chi tiết cho Sinh viên

## 📋 Tổng quan

Khi chạy ứng dụng trên **localhost** (máy tính cá nhân), chúng ta sử dụng **Gmail SMTP** để gửi email. Tài liệu này giải thích chi tiết cách hoạt động.

---

## 🔄 So sánh Localhost vs Render

| Đặc điểm | Localhost (Máy tính) | Render (Server) |
|----------|---------------------|-----------------|
| **Môi trường** | Development (phát triển) | Production (triển khai) |
| **Phương thức gửi email** | Gmail SMTP | Formspree API |
| **Cần gì?** | Gmail account + App Password | Formspree Form ID |
| **Port sử dụng** | 587 (SMTP) | 443 (HTTPS) |
| **Class sử dụng** | `MailUtilGmail.java` | `MailUtilAPI.java` |
| **Biến môi trường** | `GMAIL_USERNAME`, `GMAIL_PASSWORD` | `FORMSPREE_FORM_ID` |

---

## 🎯 Cách hoạt động trên Localhost

### Bước 1: Kiểm tra môi trường

```java
String environment = System.getenv("RENDER");
if (environment != null) {
    // Đang chạy trên Render → dùng Formspree API
} else {
    // Đang chạy trên Localhost → dùng Gmail SMTP
}
```

**Giải thích:**
- `System.getenv("RENDER")` đọc biến môi trường `RENDER`
- Trên **localhost**: biến này **null** (không tồn tại)
- Trên **Render**: biến này được tự động set bởi Render
- → Code tự động biết đang chạy ở đâu!

---

### Bước 2: Gửi email qua Gmail SMTP

```java
MailUtilGmail.sendMail(to, from, subject, body, isBodyHTML);
```

**Chi tiết các tham số:**

| Tham số | Ví dụ | Giải thích |
|---------|-------|------------|
| `to` | `"john@example.com"` | Email người nhận (user vừa đăng ký) |
| `from` | `"noreply@sqlgatewayapp.com"` | Email người gửi (hiển thị trong inbox) |
| `subject` | `"Welcome to our Email List"` | Tiêu đề email |
| `body` | `"Dear John,\n\nThank you..."` | Nội dung email |
| `isBodyHTML` | `false` | `true` = HTML, `false` = plain text |

---

## 📧 Gmail SMTP - Cách hoạt động

### Sơ đồ luồng

```
┌─────────────────────────────────────────────────────────────┐
│                    Localhost Email Flow                      │
└─────────────────────────────────────────────────────────────┘

1. User đăng ký
   ↓
2. EmailListServlet nhận request
   ↓
3. Lưu user vào PostgreSQL database
   ↓
4. Gọi MailUtilGmail.sendMail()
   ↓
5. MailUtilGmail kết nối Gmail SMTP server
   │
   ├─ Host: smtp.gmail.com
   ├─ Port: 587 (STARTTLS)
   ├─ Username: tanloc01293@gmail.com
   └─ Password: App Password (ifzv vjpc gspu xglp)
   ↓
6. Gmail SMTP server nhận email
   ↓
7. Gmail gửi email đến người nhận
   ↓
8. User nhận email trong inbox
```

---

## 🔐 Gmail App Password - Tại sao cần?

### Vấn đề bảo mật

❌ **Không thể dùng mật khẩu Gmail thông thường** vì:
- Google chặn "less secure apps"
- Bảo vệ tài khoản khỏi bị hack
- Yêu cầu 2-Factor Authentication

✅ **Phải dùng App Password** vì:
- Đây là mật khẩu riêng cho ứng dụng
- Có thể thu hồi bất cứ lúc nào
- Không ảnh hưởng đến mật khẩu Gmail chính

### Cách lấy App Password

```
Bước 1: Vào Google Account
        https://myaccount.google.com/

Bước 2: Security → 2-Step Verification
        (Bật nếu chưa bật)

Bước 3: App passwords
        Chọn app: Mail
        Chọn device: Windows Computer

Bước 4: Google tạo mật khẩu 16 ký tự
        Ví dụ: ifzv vjpc gspu xglp
        
Bước 5: Copy và dùng trong code
```

---

## 💻 Code chi tiết - MailUtilGmail.java

### Phần 1: Đọc credentials

```java
final String username = System.getenv("GMAIL_USERNAME") != null 
                      ? System.getenv("GMAIL_USERNAME") 
                      : "tanloc01293@gmail.com";

final String password = System.getenv("GMAIL_PASSWORD") != null 
                      ? System.getenv("GMAIL_PASSWORD") 
                      : "ifzv vjpc gspu xglp";
```

**Giải thích:**
- Ưu tiên đọc từ **Environment Variables** (an toàn)
- Nếu không có → dùng **hardcoded values** (cho local dev)
- **Lưu ý**: Không nên commit hardcoded credentials lên Git!

---

### Phần 2: Cấu hình SMTP

```java
Properties props = new Properties();
props.put("mail.smtp.host", "smtp.gmail.com");
props.put("mail.smtp.port", "587");
props.put("mail.smtp.auth", "true");
props.put("mail.smtp.starttls.enable", "true");
props.put("mail.smtp.starttls.required", "true");
```

**Bảng giải thích:**
bg
| Property | Giá trị | Ý nghĩa |
|----------|---------|---------|
| `mail.smtp.host` | `smtp.gmail.com` | Địa chỉ Gmail SMTP server |
| `mail.smtp.port` | `587` | Port cho STARTTLS (mã hóa) |
| `mail.smtp.auth` | `true` | Yêu cầu xác thực (username/password) |
| `mail.smtp.starttls.enable` | `true` | Bật mã hóa TLS |
| `mail.smtp.starttls.required` | `true` | Bắt buộc phải dùng TLS |

**Tại sao dùng port 587?**
- Port 25: Thường bị ISP chặn
- Port 465: SSL (cũ, ít dùng)
- **Port 587**: STARTTLS (khuyến nghị, an toàn)

---

### Phần 3: Tạo Session

```java
Session session = Session.getDefaultInstance(props);
session.setDebug(true);
```

**Giải thích:**
- `Session`: Đại diện cho phiên kết nối email
- `getDefaultInstance(props)`: Tạo session với cấu hình SMTP
- `setDebug(true)`: Bật debug log (xem chi tiết quá trình gửi)

---

### Phần 4: Tạo Message

```java
Message message = new MimeMessage(session);
message.setSubject(subject);

if (bodyIsHTML) {
    message.setContent(body, "text/html");
} else {
    message.setText(body);
}
```

**Giải thích:**
- `MimeMessage`: Class đại diện cho email
- `setSubject()`: Đặt tiêu đề
- `setContent()` vs `setText()`:
  - `setContent()`: Cho HTML (có thể format)
  - `setText()`: Cho plain text (không format)

---

### Phần 5: Đặt địa chỉ

```java
Address fromAddress = new InternetAddress(from);
Address toAddress = new InternetAddress(to);

message.setFrom(fromAddress);
message.setRecipient(Message.RecipientType.TO, toAddress);
```

**Giải thích:**
- `InternetAddress`: Class đại diện cho email address
- `setFrom()`: Người gửi (hiển thị trong "From:")
- `setRecipient()`: Người nhận
  - `RecipientType.TO`: Người nhận chính
  - `RecipientType.CC`: Carbon copy (sao chép)
  - `RecipientType.BCC`: Blind carbon copy (ẩn)

---

### Phần 6: Gửi email

```java
Transport transport = session.getTransport("smtp");
transport.connect("smtp.gmail.com", 587, username, password);
transport.sendMessage(message, message.getAllRecipients());
transport.close();
```

**Giải thích từng bước:**

| Bước | Code | Ý nghĩa |
|------|------|---------|
| 1 | `getTransport("smtp")` | Lấy SMTP transport |
| 2 | `connect(host, port, user, pass)` | Kết nối + đăng nhập Gmail |
| 3 | `sendMessage(message, recipients)` | Gửi email |
| 4 | `close()` | Đóng kết nối (giải phóng tài nguyên) |

---

## 🔍 Debug - Xem log chi tiết

Khi `session.setDebug(true)`, bạn sẽ thấy log như này:

```
DEBUG: JavaMail version 1.6.2
DEBUG: successfully loaded resource: /META-INF/javamail.default.providers
DEBUG SMTP: useEhlo true, useAuth true
DEBUG SMTP: trying to connect to host "smtp.gmail.com", port 587
220 smtp.gmail.com ESMTP
DEBUG SMTP: connected to host "smtp.gmail.com", port: 587

EHLO localhost
250-smtp.gmail.com at your service
250-STARTTLS
250 ENHANCEDSTATUSCODES

STARTTLS
220 2.0.0 Ready to start TLS

EHLO localhost
250-smtp.gmail.com at your service
250-AUTH LOGIN PLAIN XOAUTH2
250 ENHANCEDSTATUSCODES

DEBUG SMTP: Attempt to authenticate using mechanisms: LOGIN PLAIN DIGEST-MD5 NTLM XOAUTH2 
DEBUG SMTP: Using mechanism LOGIN
DEBUG SMTP: AUTH LOGIN command trace suppressed

235 2.7.0 Accepted

MAIL FROM:<noreply@sqlgatewayapp.com>
250 2.1.0 OK

RCPT TO:<john@example.com>
250 2.1.5 OK

DATA
354 Go ahead

Subject: Welcome to our Email List
From: noreply@sqlgatewayapp.com
To: john@example.com

Dear John,

Thank you for joining our email list!
.
250 2.0.0 OK

QUIT
221 2.0.0 closing connection
```

**Giải thích log:**
1. Kết nối đến `smtp.gmail.com:587`
2. Bắt đầu TLS (mã hóa)
3. Xác thực với username/password
4. Gửi email (MAIL FROM, RCPT TO, DATA)
5. Đóng kết nối (QUIT)

---

## ⚠️ Xử lý lỗi

### Try-Catch trong EmailListServlet

```java
try {
    MailUtilGmail.sendMail(to, from, subject, body, isBodyHTML);
    System.out.println("Email sent via Gmail SMTP to: " + email);
} catch (Exception e) {
    System.err.println("Error sending email: " + e.getMessage());
    e.printStackTrace();
    // KHÔNG throw exception - vẫn cho phép user đăng ký thành công
}
```

**Tại sao không throw exception?**
- User đã đăng ký thành công vào database
- Email chỉ là tính năng phụ (nice-to-have)
- Nếu email lỗi → không nên fail toàn bộ request
- **Graceful degradation**: Chức năng chính vẫn hoạt động

---

## 🎓 Các lỗi thường gặp

### Lỗi 1: Authentication failed

```
javax.mail.AuthenticationFailedException: 535-5.7.8 Username and Password not accepted
```

**Nguyên nhân:**
- Sai username hoặc password
- Chưa bật 2-Step Verification
- Chưa tạo App Password

**Giải pháp:**
1. Kiểm tra username/password
2. Bật 2-Step Verification
3. Tạo App Password mới

---

### Lỗi 2: Connection timeout

```
javax.mail.MessagingException: Could not connect to SMTP host: smtp.gmail.com, port: 587
```

**Nguyên nhân:**
- Firewall chặn port 587
- Không có internet
- Antivirus chặn kết nối

**Giải pháp:**
1. Tắt firewall tạm thời
2. Kiểm tra internet
3. Thêm exception trong antivirus

---

### Lỗi 3: Less secure apps

```
Please log in via your web browser and then try again
```

**Nguyên nhân:**
- Google chặn "less secure apps"

**Giải pháp:**
- **PHẢI dùng App Password**, không dùng mật khẩu Gmail thông thường

---

## 📊 So sánh SMTP vs API

| Đặc điểm | SMTP (Localhost) | API (Render) |
|----------|------------------|--------------|
| **Protocol** | SMTP (port 587) | HTTPS (port 443) |
| **Authentication** | Username + Password | API Key / Form ID |
| **Độ phức tạp** | Cao (nhiều config) | Thấp (simple POST) |
| **Bị chặn trên Render?** | ✅ Có | ❌ Không |
| **Tốc độ** | Nhanh | Nhanh |
| **Reliability** | Trung bình | Cao |
| **Cost** | Free (dùng Gmail) | Free tier có giới hạn |

---

## 🎯 Tóm tắt

### Localhost (Development)
```
User đăng ký
    ↓
Lưu vào database
    ↓
MailUtilGmail.sendMail()
    ↓
Kết nối Gmail SMTP (smtp.gmail.com:587)
    ↓
Xác thực với App Password
    ↓
Gửi email
    ↓
User nhận email
```

### Render (Production)
```
User đăng ký
    ↓
Lưu vào database
    ↓
MailUtilAPI.sendMail()
    ↓
POST đến Formspree API (HTTPS)
    ↓
Formspree gửi email
    ↓
User nhận email
```

---

## 📚 Tài liệu tham khảo

- [JavaMail API Documentation](https://javaee.github.io/javamail/)
- [Gmail SMTP Settings](https://support.google.com/mail/answer/7126229)
- [Google App Passwords](https://support.google.com/accounts/answer/185833)
- [Formspree Documentation](https://help.formspree.io/)

---

**Chúc các bạn học tốt! 🎓**
