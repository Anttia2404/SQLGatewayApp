# Hướng dẫn Setup Gmail để gửi Email

## 🎯 Mục tiêu

Hướng dẫn cách lấy Gmail credentials (App Password) để sử dụng Gmail SMTP trong ứng dụng Java.

---

## ⚠️ Lưu ý quan trọng

> [!WARNING]
> **Gmail không cho phép đăng nhập bằng mật khẩu thông thường trong ứng dụng!**
> 
> Bạn PHẢI sử dụng **App Password** (Mật khẩu ứng dụng) thay vì mật khẩu Gmail thông thường.

---

## 📋 Yêu cầu

1. **Tài khoản Gmail** (miễn phí)
2. **Bật xác thực 2 bước** (2-Step Verification)
3. **Tạo App Password**

---

## 🚀 Các bước thực hiện

### Bước 1: Bật xác thực 2 bước (2-Step Verification)

1. Truy cập: https://myaccount.google.com/security
2. Tìm phần **"How you sign in to Google"**
3. Click vào **"2-Step Verification"**
4. Click **"Get Started"** và làm theo hướng dẫn
5. Xác thực bằng số điện thoại hoặc app Authenticator

**Kết quả**: 2-Step Verification được bật ✅

---

### Bước 2: Tạo App Password

1. Sau khi bật 2-Step Verification, quay lại: https://myaccount.google.com/security
2. Tìm phần **"How you sign in to Google"**
3. Click vào **"App passwords"** (Mật khẩu ứng dụng)
   - Nếu không thấy, hãy search "App passwords" trong trang
4. Chọn **"Select app"** → Chọn **"Mail"**
5. Chọn **"Select device"** → Chọn **"Other (Custom name)"**
6. Nhập tên: **"SQLGatewayApp"**
7. Click **"Generate"**

**Kết quả**: Bạn sẽ thấy một mật khẩu 16 ký tự, ví dụ:
```
abcd efgh ijkl mnop
```

> [!IMPORTANT]
> **Lưu mật khẩu này lại!** Bạn sẽ không thể xem lại sau này.

---

### Bước 3: Cấu hình trong code

Mở file `MailUtilGmail.java` và thay đổi:

```java
// Gmail credentials - THAY ĐỔI THÔNG TIN NÀY
final String username = "your-email@gmail.com";  // ← Thay bằng Gmail của bạn
final String password = "your-app-password";      // ← Thay bằng App Password (16 ký tự)
```

**Ví dụ**:
```java
final String username = "johnsmith@gmail.com";
final String password = "abcdefghijklmnop";  // 16 ký tự, không có khoảng trắng
```

---

## ✅ Kiểm tra cấu hình

### Test 1: Chạy TestEmail.java

1. Mở file `TestEmail.java`
2. Uncomment dòng: `testGmailSMTP();`
3. Thay đổi email người nhận:
   ```java
   String to = "your-test-email@gmail.com";  // ← Email của bạn để test
   ```
4. Run file (Shift + F6)

**Kết quả mong đợi**:
```
=== BẮT ĐẦU TEST EMAIL ===

--- Test Gmail SMTP ---
Đang gửi email...
To: your-test-email@gmail.com
From: johnsmith@gmail.com
Subject: Test Email from SQLGatewayApp

✅ Email đã được gửi thành công qua Gmail SMTP!
Hãy kiểm tra inbox của: your-test-email@gmail.com
```

### Test 2: Đăng ký user mới

1. Chạy ứng dụng
2. Truy cập: http://localhost:8080/
3. Điền form đăng ký với email của bạn
4. Submit
5. Kiểm tra inbox Gmail

**Email bạn sẽ nhận được**:
```
Subject: Welcome to our Email List

Dear John,

Thank you for joining our email list!

Your information:
Name: John Doe
Email: john@example.com

Best regards,
SQL Gateway App Team
```

---

## 🐛 Troubleshooting

### Lỗi: "Username and Password not accepted"

**Nguyên nhân**: 
- Sai App Password
- Chưa bật 2-Step Verification
- Dùng mật khẩu Gmail thông thường thay vì App Password

**Giải pháp**:
1. Kiểm tra lại App Password (16 ký tự, không có khoảng trắng)
2. Đảm bảo 2-Step Verification đã bật
3. Tạo lại App Password mới

---

### Lỗi: "Could not connect to SMTP host"

**Nguyên nhân**: 
- Không có kết nối Internet
- Firewall chặn port 465
- Proxy/VPN can thiệp

**Giải pháp**:
1. Kiểm tra kết nối Internet
2. Tắt firewall tạm thời để test
3. Thử tắt VPN/Proxy

---

### Lỗi: "SSLException"

**Nguyên nhân**: 
- Vấn đề với SSL certificate
- Thiếu property `mail.smtps.quitwait`

**Giải pháp**:
Đảm bảo có dòng này trong `MailUtilGmail.java`:
```java
props.put("mail.smtps.quitwait", "false");
```

---

### Email vào Spam

**Nguyên nhân**: 
- Email gửi từ Gmail nhưng From address không phải Gmail
- Nội dung email giống spam

**Giải pháp**:
1. Thay đổi From address thành Gmail của bạn:
   ```java
   String from = "johnsmith@gmail.com";  // Dùng Gmail thật
   ```
2. Thêm nội dung có ý nghĩa vào email
3. Kiểm tra Spam folder

---

## 🔒 Bảo mật

### ❌ KHÔNG NÊN làm (trong production)

```java
// KHÔNG hard-code credentials trong code!
final String username = "johnsmith@gmail.com";
final String password = "abcdefghijklmnop";
```

### ✅ NÊN làm (trong production)

#### Cách 1: Sử dụng Environment Variables

```java
final String username = System.getenv("GMAIL_USERNAME");
final String password = System.getenv("GMAIL_PASSWORD");
```

Thiết lập trong hệ thống:
```bash
# Windows
set GMAIL_USERNAME=johnsmith@gmail.com
set GMAIL_PASSWORD=abcdefghijklmnop

# Linux/Mac
export GMAIL_USERNAME=johnsmith@gmail.com
export GMAIL_PASSWORD=abcdefghijklmnop
```

#### Cách 2: Sử dụng Properties File

Tạo file `email.properties` (KHÔNG commit vào Git):
```properties
gmail.username=johnsmith@gmail.com
gmail.password=abcdefghijklmnop
```

Thêm vào `.gitignore`:
```
email.properties
```

Đọc trong code:
```java
Properties emailProps = new Properties();
emailProps.load(new FileInputStream("email.properties"));
String username = emailProps.getProperty("gmail.username");
String password = emailProps.getProperty("gmail.password");
```

#### Cách 3: Sử dụng Configuration Service

Trong môi trường cloud (Render, Heroku, AWS):
- Lưu credentials trong Environment Variables của platform
- Không bao giờ commit credentials vào Git

---

## 📊 Giới hạn Gmail SMTP

Gmail có giới hạn số email gửi:

| Loại tài khoản | Giới hạn/ngày |
|----------------|---------------|
| Gmail miễn phí | 500 emails |
| Google Workspace | 2,000 emails |

**Lưu ý**: 
- Nếu vượt giới hạn, tài khoản có thể bị khóa tạm thời (24h)
- Không nên dùng Gmail SMTP cho production với lượng email lớn
- Cân nhắc dùng dịch vụ email chuyên nghiệp (SendGrid, Mailgun, AWS SES)

---

## 🎓 Tóm tắt

1. ✅ Bật 2-Step Verification
2. ✅ Tạo App Password (16 ký tự)
3. ✅ Cấu hình trong `MailUtilGmail.java`
4. ✅ Test với `TestEmail.java`
5. ✅ Kiểm tra inbox Gmail
6. ✅ Bảo mật credentials (không commit vào Git)

---

## 📚 Tài liệu tham khảo

- [Google Account Security](https://myaccount.google.com/security)
- [Gmail SMTP Settings](https://support.google.com/mail/answer/7126229)
- [App Passwords Help](https://support.google.com/accounts/answer/185833)

---

## ❓ Câu hỏi thường gặp

### Q: Có thể dùng mật khẩu Gmail thông thường không?
**A**: Không. Gmail yêu cầu App Password cho ứng dụng bên thứ 3.

### Q: App Password có hết hạn không?
**A**: Không, App Password không hết hạn trừ khi bạn xóa nó hoặc đổi mật khẩu Gmail.

### Q: Có thể tạo nhiều App Password không?
**A**: Có, bạn có thể tạo nhiều App Password cho các ứng dụng khác nhau.

### Q: Nếu quên App Password thì sao?
**A**: Xóa App Password cũ và tạo mới. Không thể xem lại App Password đã tạo.

### Q: Có an toàn không?
**A**: App Password an toàn hơn mật khẩu thông thường vì:
- Chỉ dùng cho một ứng dụng cụ thể
- Có thể thu hồi bất cứ lúc nào
- Không ảnh hưởng đến mật khẩu Gmail chính

---

**Chúc bạn setup thành công! 🎉**
