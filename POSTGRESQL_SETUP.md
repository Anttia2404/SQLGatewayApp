# Hướng dẫn Setup PostgreSQL cho SQL Gateway App

## 1. Cấu hình Database

Bạn cần tạo database và bảng trong PostgreSQL.

1.  Mở **pgAdmin** hoặc **psql** terminal.
2.  Chạy script trong file `database_postgresql.sql` (đã được tạo trong project).
    *   Lưu ý: Script này tạo bảng tên là `"user"` (có dấu ngoặc kép vì user là từ khóa trong Postgres).

Nội dung tóm tắt của script:
```sql
-- Tạo bảng "user"
CREATE TABLE "user" (
    userid SERIAL PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL
);

-- Thêm dữ liệu mẫu
INSERT INTO "user" (email, firstname, lastname) 
VALUES ('jsmith@gmail.com', 'John', 'Smith');
```

## 2. Cấu hình Kết nối (context.xml)

Tôi đã cấu hình sẵn trong `src/main/webapp/META-INF/context.xml` với thông tin mặc định:
*   **URL**: `jdbc:postgresql://localhost:5432/murach`
*   **Username**: `postgres`
*   **Password**: `postgres`

**QUAN TRỌNG:** Nếu password của bạn khác `postgres`, hãy mở file `context.xml` và sửa lại attribute `password="..."`.

## 3. Chạy Ứng dụng

1.  Trong NetBeans: **Right-click Project** -> **Clean and Build**.
2.  **Restart Tomcat** (để nhận driver mới và config mới).
3.  **Run** Project.

## 4. Lưu ý khi dùng SQL Gateway

Khi nhập câu lệnh SQL trên giao diện web, vì bảng tên là `"user"`, bạn phải luôn dùng dấu ngoặc kép:

*   ✅ Đúng: `SELECT * FROM "user"`
*   ❌ Sai: `SELECT * FROM user` (Sẽ báo lỗi syntax)
