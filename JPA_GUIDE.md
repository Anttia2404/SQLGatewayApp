# Hướng dẫn sử dụng JPA trong SQLGatewayApp

## Tổng quan

Dự án đã được cập nhật để sử dụng **JPA (Java Persistence API)** với **Hibernate** làm provider thay vì JDBC thuần. Điều này giúp code dễ bảo trì hơn và giảm thiểu SQL thủ công.

## Các file đã thêm/sửa đổi

### 1. **pom.xml** - Thêm dependencies
```xml
<!-- Jakarta Persistence API (JPA) -->
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>

<!-- Hibernate Core (JPA Implementation) -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.2.7.Final</version>
</dependency>
```

### 2. **persistence.xml** - File cấu hình JPA
- **Vị trí**: `src/main/resources/META-INF/persistence.xml`
- **Chức năng**: Cấu hình kết nối database, Hibernate properties, connection pool

**Lưu ý quan trọng**: 
- Persistence unit name: `MurachPU`
- Đã cấu hình kết nối đến PostgreSQL trên Render
- `hibernate.hbm2ddl.auto = validate` - Chỉ validate schema, không tự động tạo/sửa bảng

### 3. **User.java** - Entity class
- **Thêm annotations**:
  - `@Entity` - Đánh dấu đây là JPA entity
  - `@Table(name = "\"user\"")` - Map với bảng "user" trong database
  - `@Id` - Đánh dấu primary key
  - `@GeneratedValue(strategy = GenerationType.IDENTITY)` - Auto-increment
  - `@Column` - Map với các cột trong database

- **Thêm field mới**: `userId` (Long) - Primary key

### 4. **JPAUtil.java** - Utility class
- **Chức năng**: Quản lý EntityManagerFactory và EntityManager
- **Methods**:
  - `getEntityManager()` - Tạo EntityManager mới
  - `closeEntityManager(em)` - Đóng EntityManager
  - `close()` - Đóng EntityManagerFactory (gọi khi shutdown app)

### 5. **UserDAO.java** - Data Access Object (JPA version)
Thay thế `UserDB.java` với implementation sử dụng JPA:

**Methods**:
- `insert(User user)` - Thêm user mới
- `update(User user)` - Cập nhật user
- `delete(User user)` - Xóa user
- `emailExists(String email)` - Kiểm tra email tồn tại
- `selectUser(String email)` - Tìm user theo email

**Ưu điểm so với JDBC**:
- Không cần viết SQL thủ công
- Tự động quản lý transaction
- Sử dụng JPQL (Java Persistence Query Language) thay vì SQL
- Type-safe với TypedQuery

### 6. **EmailListServlet.java** - Cập nhật
- Thay đổi import từ `UserDB` sang `UserDAO`
- Các method calls vẫn giữ nguyên interface

## Cách sử dụng JPA

### Ví dụ 1: Insert user mới
```java
User user = new User("John", "Doe", "john@example.com");
int result = UserDAO.insert(user);
if (result > 0) {
    System.out.println("User inserted successfully!");
}
```

### Ví dụ 2: Tìm user theo email
```java
User user = UserDAO.selectUser("john@example.com");
if (user != null) {
    System.out.println("Found: " + user.getFirstName());
}
```

### Ví dụ 3: Update user
```java
User user = UserDAO.selectUser("john@example.com");
if (user != null) {
    user.setFirstName("Jane");
    UserDAO.update(user);
}
```

### Ví dụ 4: Delete user
```java
User user = UserDAO.selectUser("john@example.com");
if (user != null) {
    UserDAO.delete(user);
}
```

## JPQL (Java Persistence Query Language)

JPQL là ngôn ngữ query của JPA, tương tự SQL nhưng làm việc với entities thay vì tables.

### Ví dụ JPQL trong UserDAO:
```java
// Đếm số user có email cụ thể
TypedQuery<Long> query = em.createQuery(
    "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
query.setParameter("email", email);
Long count = query.getSingleResult();

// Tìm user theo email
TypedQuery<User> query = em.createQuery(
    "SELECT u FROM User u WHERE u.email = :email", User.class);
query.setParameter("email", email);
User user = query.getSingleResult();
```

## Transaction Management

JPA sử dụng transactions để đảm bảo tính toàn vẹn dữ liệu:

```java
EntityManager em = JPAUtil.getEntityManager();
EntityTransaction transaction = em.getTransaction();

try {
    transaction.begin();
    
    // Thực hiện các thao tác database
    em.persist(user);
    
    transaction.commit(); // Lưu thay đổi
} catch (Exception e) {
    if (transaction.isActive()) {
        transaction.rollback(); // Hủy thay đổi nếu có lỗi
    }
    e.printStackTrace();
} finally {
    JPAUtil.closeEntityManager(em);
}
```

## So sánh JDBC vs JPA

### JDBC (UserDB.java - cũ):
```java
String query = "INSERT INTO \"user\" (email, firstname, lastname) VALUES (?, ?, ?)";
PreparedStatement ps = connection.prepareStatement(query);
ps.setString(1, user.getEmail());
ps.setString(2, user.getFirstName());
ps.setString(3, user.getLastName());
ps.executeUpdate();
```

### JPA (UserDAO.java - mới):
```java
em.persist(user);
```

**Lợi ích**:
- Code ngắn gọn hơn
- Không cần viết SQL
- Tự động mapping giữa Java object và database
- Type-safe, ít lỗi runtime

## Build và Deploy

### 1. Build project với Maven:
```bash
mvn clean install
```

### 2. Kiểm tra WAR file:
File WAR sẽ được tạo tại: `target/ROOT.war`

### 3. Deploy lên Render:
- Push code lên Git
- Render sẽ tự động build và deploy
- JPA sẽ tự động kết nối đến PostgreSQL database

## Troubleshooting

### Lỗi: "EntityManagerFactory is not initialized"
- Kiểm tra file `persistence.xml` có đúng vị trí không
- Kiểm tra thông tin kết nối database trong `persistence.xml`

### Lỗi: "No Persistence provider for EntityManager"
- Đảm bảo đã thêm Hibernate dependency trong `pom.xml`
- Chạy `mvn clean install` để tải dependencies

### Lỗi kết nối database:
- Kiểm tra URL, username, password trong `persistence.xml`
- Đảm bảo PostgreSQL database đang chạy

### Xem SQL queries:
Trong `persistence.xml`, property này sẽ hiển thị SQL:
```xml
<property name="hibernate.show_sql" value="true"/>
<property name="hibernate.format_sql" value="true"/>
```

## Tài liệu tham khảo

- [Jakarta Persistence (JPA) Documentation](https://jakarta.ee/specifications/persistence/)
- [Hibernate ORM Documentation](https://hibernate.org/orm/documentation/)
- [JPQL Reference](https://docs.oracle.com/javaee/7/tutorial/persistence-querylanguage.htm)

## Notes

- File `UserDB.java` (JDBC version) vẫn còn trong project nhưng không được sử dụng nữa
- Có thể xóa `UserDB.java` nếu muốn, hoặc giữ lại để tham khảo
- `ConnectionPool.java` và `DBUtil.java` cũng không cần thiết nữa với JPA
