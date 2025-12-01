# Hướng dẫn Test JPA

## 📋 Tổng quan

File `TestJPA.java` được tạo để kiểm tra tất cả chức năng JPA trong project. Bạn có thể chạy nó như một Java Application để verify JPA hoạt động đúng.

## 🚀 Cách chạy Test trong NetBeans

### Bước 1: Build project
1. Chuột phải vào project **SQLGatewayApp**
2. Chọn **"Clean and Build"** (Shift + F11)
3. Đợi build hoàn thành (xem console)

### Bước 2: Chạy TestJPA
1. Mở file `src/main/java/murach/test/TestJPA.java`
2. Chuột phải vào file
3. Chọn **"Run File"** (Shift + F6)
4. Hoặc click vào nút ▶️ **Run** trên toolbar

### Bước 3: Xem kết quả
Kết quả sẽ hiển thị trong **Output** window của NetBeans.

## ✅ Các Test Cases

### Test 1: Kiểm tra kết nối JPA
**Mục đích**: Verify EntityManager có thể được tạo thành công

**Kết quả mong đợi**:
```
--- Test 1: Kiểm tra kết nối JPA ---
✅ Kết nối JPA thành công!
   EntityManager đã được tạo
```

**Nếu thất bại**:
- Kiểm tra file `persistence.xml` có đúng vị trí không
- Kiểm tra thông tin database connection
- Xem log chi tiết để biết lỗi gì

---

### Test 2: Test emailExists()
**Mục đích**: Kiểm tra method kiểm tra email tồn tại

**Kết quả mong đợi**:
```
--- Test 2: Test emailExists() ---
Email: jsmith@gmail.com
Tồn tại: ✅ Có

Email: notexist@test.com
Tồn tại: ✅ Không (đúng)
```

**Test này kiểm tra**:
- JPQL query hoạt động đúng
- Parameter binding
- Return đúng kết quả

---

### Test 3: Test selectUser()
**Mục đích**: Kiểm tra method tìm user theo email

**Kết quả mong đợi**:
```
--- Test 3: Test selectUser() ---
✅ Tìm thấy user:
   User ID: 1
   Email: jsmith@gmail.com
   First Name: John
   Last Name: Smith
```

**Test này kiểm tra**:
- JPQL SELECT query
- Entity mapping
- Getter methods hoạt động đúng

---

### Test 4: Test insert() (Optional)
**⚠️ CẢNH BÁO**: Test này sẽ thêm dữ liệu vào database!

**Cách bật**: Uncomment dòng `testInsert();` trong method `main()`

**Kết quả mong đợi**:
```
--- Test 4: Test insert() ---
✅ Insert thành công!
   Email: test_1701234567890@test.com
✅ Verify: User đã được lưu vào database
   User ID: 5
```

**Test này kiểm tra**:
- EntityManager.persist()
- Transaction commit
- Auto-generated ID

---

### Test 5: Test update() (Optional)
**⚠️ CẢNH BÁO**: Test này sẽ sửa dữ liệu trong database!

**Cách bật**: Uncomment dòng `testUpdate();` trong method `main()`

**Kết quả mong đợi**:
```
--- Test 5: Test update() ---
Trước khi update:
   First Name: John
   Last Name: Smith

✅ Update thành công!
Sau khi update:
   First Name: Updated
   Last Name: Name

✅ Đã restore lại giá trị cũ
```

**Test này kiểm tra**:
- EntityManager.merge()
- Update transaction
- Restore lại data sau test

---

### Test 6: Test delete() (Optional)
**⚠️ CẢNH BÁO**: Test này sẽ XÓA dữ liệu trong database!

**Cách bật**: Uncomment dòng `testDelete();` trong method `main()`

**Kết quả mong đợi**:
```
--- Test 6: Test delete() ---
⚠️  CẢNH BÁO: Test này sẽ XÓA dữ liệu!
Đã tạo user test: delete_test_1701234567890@test.com
✅ User tồn tại, ID: 6
✅ Delete thành công!
✅ Verify: User đã bị xóa khỏi database
```

**Test này kiểm tra**:
- EntityManager.remove()
- Delete transaction
- Tạo test data trước khi xóa

---

## 🔍 Troubleshooting

### Lỗi: "EntityManagerFactory is not initialized"

**Nguyên nhân**: Không tìm thấy file `persistence.xml` hoặc cấu hình sai

**Giải pháp**:
1. Kiểm tra file `src/main/resources/META-INF/persistence.xml` tồn tại
2. Rebuild project: Clean and Build
3. Kiểm tra persistence unit name = "MurachPU"

---

### Lỗi: "No Persistence provider for EntityManager"

**Nguyên nhân**: Thiếu Hibernate dependency

**Giải pháp**:
1. Kiểm tra `pom.xml` có dependency `hibernate-core`
2. Clean and Build lại project
3. Kiểm tra trong Dependencies có `hibernate-core-6.2.7.Final.jar`

---

### Lỗi kết nối database

**Nguyên nhân**: Không kết nối được PostgreSQL

**Giải pháp**:
1. Kiểm tra database URL trong `persistence.xml`
2. Kiểm tra username/password
3. Kiểm tra PostgreSQL server đang chạy
4. Kiểm tra firewall/network

**Xem log chi tiết**:
```
hibernate.show_sql = true
hibernate.format_sql = true
```

---

### Lỗi: "Table 'user' doesn't exist"

**Nguyên nhân**: Database chưa có bảng user

**Giải pháp**:
1. Chạy script `database_postgresql.sql` để tạo bảng
2. Hoặc thay đổi `hibernate.hbm2ddl.auto` thành `create` (cẩn thận!)

---

## 📊 Xem SQL Queries

Để xem SQL queries mà Hibernate generate, kiểm tra console output. Bạn sẽ thấy:

```sql
Hibernate: 
    select
        count(u1_0.userid) 
    from
        "user" u1_0 
    where
        u1_0.email=?

Hibernate: 
    select
        u1_0.userid,
        u1_0.email,
        u1_0.firstname,
        u1_0.lastname 
    from
        "user" u1_0 
    where
        u1_0.email=?
```

## 🎯 Test trong môi trường Production

**KHÔNG nên chạy test này trên production database!**

Nếu muốn test trên production:
1. Tạo database riêng cho testing
2. Thay đổi connection trong `persistence.xml`
3. Hoặc tạo `persistence-test.xml` riêng

## 📝 Tùy chỉnh Test

### Thêm test case mới:

```java
private static void testCustomQuery() {
    System.out.println("--- Test Custom Query ---");
    
    EntityManager em = JPAUtil.getEntityManager();
    try {
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.firstName LIKE :name", 
            User.class
        );
        query.setParameter("name", "J%");
        
        List<User> users = query.getResultList();
        System.out.println("Tìm thấy " + users.size() + " users");
        
        for (User u : users) {
            System.out.println("  - " + u.getFirstName() + " " + u.getLastName());
        }
    } finally {
        JPAUtil.closeEntityManager(em);
    }
}
```

### Test với transaction:

```java
private static void testTransaction() {
    EntityManager em = JPAUtil.getEntityManager();
    EntityTransaction tx = em.getTransaction();
    
    try {
        tx.begin();
        
        // Thực hiện nhiều operations
        User user1 = new User("Test1", "User1", "test1@test.com");
        User user2 = new User("Test2", "User2", "test2@test.com");
        
        em.persist(user1);
        em.persist(user2);
        
        tx.commit();
        System.out.println("✅ Transaction committed");
        
    } catch (Exception e) {
        if (tx.isActive()) {
            tx.rollback();
            System.out.println("❌ Transaction rolled back");
        }
        e.printStackTrace();
    } finally {
        JPAUtil.closeEntityManager(em);
    }
}
```

## 🔗 Tài liệu liên quan

- [JPA_GUIDE.md](JPA_GUIDE.md) - Hướng dẫn sử dụng JPA
- [Hibernate Documentation](https://hibernate.org/orm/documentation/)
- [JPQL Reference](https://docs.oracle.com/javaee/7/tutorial/persistence-querylanguage.htm)

## ✨ Tips

1. **Luôn test sau khi thay đổi Entity**: Mỗi khi sửa `@Entity` class, chạy test để verify
2. **Kiểm tra SQL output**: Xem SQL để hiểu Hibernate đang làm gì
3. **Test trên local trước**: Đừng test trực tiếp trên production
4. **Backup database**: Trước khi chạy test insert/update/delete
5. **Sử dụng transaction**: Luôn wrap operations trong transaction

## 🎓 Học thêm về JPA

Sau khi test cơ bản hoạt động, bạn có thể học:
- Named Queries
- Criteria API
- Entity Relationships (OneToMany, ManyToOne, etc.)
- Lazy vs Eager loading
- Caching strategies
- Performance optimization
