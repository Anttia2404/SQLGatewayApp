# Entity trong JPA - Giải thích chi tiết

## 🎯 Entity là gì?

**Entity** = **Java class** được đánh dấu bằng annotation `@Entity`

**Mục đích**: Đại diện cho **một bảng trong database**

Trong project này, **Entity** là class **`User.java`**

---

## 📍 Vị trí Entity

**File**: `src/main/java/murach/business/User.java`

---

## 🔍 Phân tích từng phần của Entity

### **Toàn bộ code của Entity User**

```java
package murach.business;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity                              // ← Đây là ENTITY annotation
@Table(name = "\"user\"")            // ← Map với bảng "user"
public class User implements Serializable {
    
    @Id                              // ← Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long userId;
    
    @Column(name = "firstname", nullable = false, length = 50)
    private String firstName;
    
    @Column(name = "lastname", nullable = false, length = 50)
    private String lastName;
    
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;
    
    // Constructors, getters, setters...
}
```

---

## 📊 Mapping: Java Class ↔️ Database Table

### **Java Entity (User.java)**
```java
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @Column(name = "userid")
    private Long userId;
    
    @Column(name = "firstname")
    private String firstName;
    
    @Column(name = "lastname")
    private String lastName;
    
    @Column(name = "email")
    private String email;
}
```

### **↕️ Tương ứng với**

### **Database Table (PostgreSQL)**
```sql
CREATE TABLE "user" (
    userid SERIAL PRIMARY KEY,
    firstname VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE
);
```

---

## 🏷️ Các Annotation trong Entity

### 1. **@Entity** (Dòng 6)
```java
@Entity
public class User { ... }
```

**Ý nghĩa**: Đánh dấu class này là một JPA Entity

**Kết quả**: 
- JPA biết class này map với một bảng trong database
- Có thể dùng trong JPQL queries
- EntityManager có thể quản lý objects của class này

---

### 2. **@Table** (Dòng 7)
```java
@Table(name = "\"user\"")
```

**Ý nghĩa**: Chỉ định tên bảng trong database

**Giải thích**:
- `name = "\"user\""` → Bảng tên `"user"` (có dấu ngoặc kép)
- Dấu `\"` cần thiết vì `user` là reserved keyword trong PostgreSQL
- Nếu không có `@Table`, JPA sẽ dùng tên class (`User`) làm tên bảng

**Ví dụ**:
```java
@Table(name = "\"user\"")  → Bảng: "user"
@Table(name = "users")     → Bảng: users
// Không có @Table       → Bảng: User (tên class)
```

---

### 3. **@Id** (Dòng 10)
```java
@Id
@Column(name = "userid")
private Long userId;
```

**Ý nghĩa**: Đánh dấu field này là **Primary Key**

**Kết quả**:
- JPA biết field này là unique identifier
- Dùng để tìm kiếm: `em.find(User.class, userId)`

---

### 4. **@GeneratedValue** (Dòng 11)
```java
@GeneratedValue(strategy = GenerationType.IDENTITY)
```

**Ý nghĩa**: ID được **tự động generate** bởi database

**Strategies**:
- `IDENTITY` → Database tự tăng (AUTO_INCREMENT, SERIAL)
- `SEQUENCE` → Dùng database sequence
- `AUTO` → JPA tự chọn strategy phù hợp
- `TABLE` → Dùng bảng riêng để generate ID

**Trong PostgreSQL**:
```sql
userid SERIAL  -- Tự động tăng: 1, 2, 3, 4, ...
```

**Kết quả**:
```java
User user = new User("John", "Doe", "john@example.com");
// Trước khi insert
user.getUserId() → null

UserDAO.insert(user);
// Sau khi insert
user.getUserId() → 5  // Database tự generate
```

---

### 5. **@Column** (Dòng 12, 15, 18, 21)
```java
@Column(name = "firstname", nullable = false, length = 50)
private String firstName;
```

**Ý nghĩa**: Map field với cột trong database

**Các thuộc tính**:
- `name` → Tên cột trong database
- `nullable` → Có cho phép NULL không
- `unique` → Giá trị phải unique
- `length` → Độ dài tối đa (cho String)

**Ví dụ**:
```java
@Column(name = "email", nullable = false, unique = true, length = 50)
private String email;
```

Tương đương SQL:
```sql
email VARCHAR(50) NOT NULL UNIQUE
```

---

## 🔄 Lifecycle của Entity Object

### **1. Transient (Tạm thời)**
```java
User user = new User("John", "Doe", "john@example.com");
// Object mới tạo, chưa liên quan gì đến database
// userId = null
```

**Trạng thái**: Chưa được JPA quản lý

---

### **2. Managed (Được quản lý)**
```java
EntityManager em = JPAUtil.getEntityManager();
em.getTransaction().begin();

em.persist(user);  // ← Từ Transient → Managed
// JPA bắt đầu theo dõi object này
// Mọi thay đổi sẽ được sync với database
```

**Trạng thái**: Đang được EntityManager quản lý

---

### **3. Persisted (Đã lưu)**
```java
em.getTransaction().commit();  // ← INSERT vào database
// userId được set = 5 (auto-generated)
// Dữ liệu đã có trong database
```

**Trạng thái**: Đã lưu vào database

---

### **4. Detached (Tách rời)**
```java
em.close();  // ← Từ Managed → Detached
// Object vẫn tồn tại nhưng không còn được quản lý
// Thay đổi object sẽ KHÔNG tự động sync với database
```

**Trạng thái**: Không còn được quản lý

---

## 💡 Ví dụ thực tế

### **Tạo Entity object**
```java
// Tạo object mới (Transient)
User user = new User();
user.setFirstName("John");
user.setLastName("Doe");
user.setEmail("john@example.com");

System.out.println(user.getUserId());  // null
```

### **Lưu vào database**
```java
EntityManager em = JPAUtil.getEntityManager();
EntityTransaction tx = em.getTransaction();

tx.begin();
em.persist(user);  // Managed
tx.commit();       // Persisted

System.out.println(user.getUserId());  // 5 (auto-generated)
```

### **Tìm Entity từ database**
```java
EntityManager em = JPAUtil.getEntityManager();

// Tìm theo ID
User user = em.find(User.class, 5L);
// SELECT * FROM "user" WHERE userid = 5

System.out.println(user.getFirstName());  // "John"
```

### **Update Entity**
```java
EntityManager em = JPAUtil.getEntityManager();
EntityTransaction tx = em.getTransaction();

tx.begin();

User user = em.find(User.class, 5L);  // Managed
user.setFirstName("Jane");  // Thay đổi

tx.commit();  // Tự động UPDATE database
// UPDATE "user" SET firstname = 'Jane' WHERE userid = 5
```

### **Delete Entity**
```java
EntityManager em = JPAUtil.getEntityManager();
EntityTransaction tx = em.getTransaction();

tx.begin();

User user = em.find(User.class, 5L);
em.remove(user);  // Đánh dấu để xóa

tx.commit();  // DELETE khỏi database
// DELETE FROM "user" WHERE userid = 5
```

---

## 📋 So sánh: Entity vs Plain Object

### **Plain Java Object (POJO)**
```java
public class User {
    private String firstName;
    private String lastName;
    private String email;
    
    // Getters, setters...
}
```

**Đặc điểm**:
- Chỉ là Java object bình thường
- Không liên quan gì đến database
- Phải tự viết SQL để lưu/lấy dữ liệu

---

### **JPA Entity**
```java
@Entity
@Table(name = "\"user\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Column(name = "firstname")
    private String firstName;
    
    // ...
}
```

**Đặc điểm**:
- Có annotations `@Entity`, `@Table`, `@Column`
- JPA tự động map với database
- Không cần viết SQL thủ công
- EntityManager quản lý lifecycle

---

## 🎯 Tại sao cần Entity?

### **Trước khi có JPA (dùng JDBC)**
```java
// Phải viết SQL thủ công
String sql = "INSERT INTO \"user\" (email, firstname, lastname) VALUES (?, ?, ?)";
PreparedStatement ps = connection.prepareStatement(sql);
ps.setString(1, user.getEmail());
ps.setString(2, user.getFirstName());
ps.setString(3, user.getLastName());
ps.executeUpdate();

// Phải tự map ResultSet → Object
ResultSet rs = ps.executeQuery("SELECT * FROM \"user\" WHERE userid = 5");
if (rs.next()) {
    User user = new User();
    user.setUserId(rs.getLong("userid"));
    user.setFirstName(rs.getString("firstname"));
    user.setLastName(rs.getString("lastname"));
    user.setEmail(rs.getString("email"));
}
```

**Nhược điểm**:
- Nhiều code boilerplate
- Dễ lỗi (typo trong SQL, tên cột)
- Khó bảo trì

---

### **Sau khi có JPA (dùng Entity)**
```java
// Insert
em.persist(user);

// Select
User user = em.find(User.class, 5L);

// Update
user.setFirstName("Jane");
em.merge(user);

// Delete
em.remove(user);
```

**Ưu điểm**:
- Code ngắn gọn
- Type-safe (compile-time checking)
- Dễ bảo trì
- Tự động generate SQL

---

## 🔍 Entity trong JPQL

### **JPQL Query**
```java
TypedQuery<User> query = em.createQuery(
    "SELECT u FROM User u WHERE u.email = :email",  // ← Dùng class name
    User.class
);
query.setParameter("email", "john@example.com");
User user = query.getSingleResult();
```

**Chú ý**:
- `User` → Tên **class** (Entity), không phải tên bảng
- `u.email` → Tên **field** trong class, không phải tên cột

### **Hibernate tự động chuyển thành SQL**
```sql
SELECT u1_0.userid, u1_0.email, u1_0.firstname, u1_0.lastname
FROM "user" u1_0
WHERE u1_0.email = ?
```

---

## 📝 Quy tắc khi tạo Entity

### ✅ **Bắt buộc**
1. Phải có annotation `@Entity`
2. Phải có `@Id` (primary key)
3. Phải có constructor không tham số (no-arg constructor)
4. Class không được `final`
5. Fields không được `final`

### ✅ **Nên có**
1. Implement `Serializable`
2. Override `toString()`, `equals()`, `hashCode()`
3. Có getters/setters cho tất cả fields

### ❌ **Không nên**
1. Có business logic phức tạp trong Entity
2. Entity phụ thuộc vào các class khác (ngoài JPA)

---

## 🎓 Tóm tắt

| Khái niệm | Giải thích |
|-----------|------------|
| **Entity** | Java class map với database table |
| **@Entity** | Annotation đánh dấu class là Entity |
| **@Table** | Chỉ định tên bảng trong database |
| **@Id** | Đánh dấu primary key |
| **@GeneratedValue** | ID tự động generate |
| **@Column** | Map field với cột trong database |
| **Transient** | Object mới tạo, chưa liên quan DB |
| **Managed** | Object được EntityManager quản lý |
| **Persisted** | Object đã lưu vào database |
| **Detached** | Object không còn được quản lý |

---

## 💡 Ví dụ trong project

Trong project **SQLGatewayApp**, có **1 Entity**:

```
User.java (Entity)
    ↕
"user" table (Database)
```

**Mapping**:
```
User.userId      ↔  userid (SERIAL PRIMARY KEY)
User.firstName   ↔  firstname (VARCHAR(50))
User.lastName    ↔  lastname (VARCHAR(50))
User.email       ↔  email (VARCHAR(50) UNIQUE)
```

**Sử dụng**:
```java
// Tạo
User user = new User("John", "Doe", "john@example.com");
UserDAO.insert(user);

// Đọc
User user = UserDAO.selectUser("john@example.com");

// Cập nhật
user.setFirstName("Jane");
UserDAO.update(user);

// Xóa
UserDAO.delete(user);
```

---

## ❓ Câu hỏi thường gặp

### Q: Một project có bao nhiêu Entity?
**A**: Tùy thuộc vào số bảng trong database. Mỗi bảng = 1 Entity.

### Q: Entity phải ở package nào?
**A**: Thường ở package `model`, `entity`, hoặc `business`.

### Q: Entity có thể có methods không?
**A**: Có, nhưng nên giữ đơn giản (getters/setters, toString, equals).

### Q: Tại sao cần constructor không tham số?
**A**: JPA cần nó để tạo instance khi load từ database.

### Q: Field có thể là `public` không?
**A**: Có thể nhưng không nên. Nên dùng `private` + getters/setters.

---

Bạn đã hiểu rõ về Entity chưa? Có câu hỏi gì thêm không? 😊
