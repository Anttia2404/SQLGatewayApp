package murach.test;

import murach.business.User;
import murach.data.UserDAO;
import murach.data.JPAUtil;

/**
 * Test class để kiểm tra JPA functionality
 * Chạy class này như một Java Application để test JPA
 */
public class TestJPA {
    
    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU TEST JPA ===\n");
        
        try {
            // Test 1: Kiểm tra kết nối JPA
            testConnection();
            
            // Test 2: Test emailExists
            testEmailExists();
            
            // Test 3: Test selectUser
            testSelectUser();
            
            // Test 4: Test insert (nếu muốn)
            // testInsert();
            
            // Test 5: Test update (nếu muốn)
            // testUpdate();
            
            // Test 6: Test delete (nếu muốn)
            // testDelete();
            
            System.out.println("\n=== TẤT CẢ TEST HOÀN THÀNH ===");
            
        } catch (Exception e) {
            System.err.println("\n❌ LỖI TRONG QUÁ TRÌNH TEST:");
            e.printStackTrace();
        } finally {
            // Đóng EntityManagerFactory
            JPAUtil.close();
            System.out.println("\nĐã đóng EntityManagerFactory");
        }
    }
    
    /**
     * Test 1: Kiểm tra kết nối JPA
     */
    private static void testConnection() {
        System.out.println("--- Test 1: Kiểm tra kết nối JPA ---");
        try {
            // Thử tạo EntityManager
            var em = JPAUtil.getEntityManager();
            if (em != null && em.isOpen()) {
                System.out.println("✅ Kết nối JPA thành công!");
                System.out.println("   EntityManager đã được tạo");
                JPAUtil.closeEntityManager(em);
            } else {
                System.out.println("❌ Không thể tạo EntityManager");
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi kết nối JPA:");
            e.printStackTrace();
        }
        System.out.println();
    }
    
    /**
     * Test 2: Test emailExists method
     */
    private static void testEmailExists() {
        System.out.println("--- Test 2: Test emailExists() ---");
        
        // Test với email tồn tại
        String existingEmail = "jsmith@gmail.com";
        boolean exists = UserDAO.emailExists(existingEmail);
        System.out.println("Email: " + existingEmail);
        System.out.println("Tồn tại: " + (exists ? "✅ Có" : "❌ Không"));
        
        // Test với email không tồn tại
        String nonExistingEmail = "notexist@test.com";
        boolean notExists = UserDAO.emailExists(nonExistingEmail);
        System.out.println("\nEmail: " + nonExistingEmail);
        System.out.println("Tồn tại: " + (notExists ? "❌ Có (không đúng)" : "✅ Không (đúng)"));
        System.out.println();
    }
    
    /**
     * Test 3: Test selectUser method
     */
    private static void testSelectUser() {
        System.out.println("--- Test 3: Test selectUser() ---");
        
        String email = "jsmith@gmail.com";
        User user = UserDAO.selectUser(email);
        
        if (user != null) {
            System.out.println("✅ Tìm thấy user:");
            System.out.println("   User ID: " + user.getUserId());
            System.out.println("   Email: " + user.getEmail());
            System.out.println("   First Name: " + user.getFirstName());
            System.out.println("   Last Name: " + user.getLastName());
        } else {
            System.out.println("❌ Không tìm thấy user với email: " + email);
        }
        System.out.println();
    }
    
    /**
     * Test 4: Test insert method
     * CẢNH BÁO: Test này sẽ thêm dữ liệu vào database
     * Uncomment để chạy
     */
    private static void testInsert() {
        System.out.println("--- Test 4: Test insert() ---");
        
        // Tạo user mới với email unique
        String testEmail = "test_" + System.currentTimeMillis() + "@test.com";
        User newUser = new User("Test", "User", testEmail);
        
        int result = UserDAO.insert(newUser);
        
        if (result > 0) {
            System.out.println("✅ Insert thành công!");
            System.out.println("   Email: " + testEmail);
            
            // Verify bằng cách select lại
            User verifyUser = UserDAO.selectUser(testEmail);
            if (verifyUser != null) {
                System.out.println("✅ Verify: User đã được lưu vào database");
                System.out.println("   User ID: " + verifyUser.getUserId());
            }
        } else {
            System.out.println("❌ Insert thất bại");
        }
        System.out.println();
    }
    
    /**
     * Test 5: Test update method
     * CẢNH BÁO: Test này sẽ sửa dữ liệu trong database
     * Uncomment để chạy
     */
    private static void testUpdate() {
        System.out.println("--- Test 5: Test update() ---");
        
        String email = "jsmith@gmail.com";
        User user = UserDAO.selectUser(email);
        
        if (user != null) {
            System.out.println("Trước khi update:");
            System.out.println("   First Name: " + user.getFirstName());
            System.out.println("   Last Name: " + user.getLastName());
            
            // Update
            String oldFirstName = user.getFirstName();
            user.setFirstName("Updated");
            user.setLastName("Name");
            
            int result = UserDAO.update(user);
            
            if (result > 0) {
                System.out.println("\n✅ Update thành công!");
                
                // Verify
                User updatedUser = UserDAO.selectUser(email);
                System.out.println("Sau khi update:");
                System.out.println("   First Name: " + updatedUser.getFirstName());
                System.out.println("   Last Name: " + updatedUser.getLastName());
                
                // Restore lại giá trị cũ
                updatedUser.setFirstName(oldFirstName);
                updatedUser.setLastName("Smith");
                UserDAO.update(updatedUser);
                System.out.println("\n✅ Đã restore lại giá trị cũ");
            } else {
                System.out.println("❌ Update thất bại");
            }
        }
        System.out.println();
    }
    
    /**
     * Test 6: Test delete method
     * CẢNH BÁO: Test này sẽ XÓA dữ liệu trong database
     * Uncomment để chạy và cẩn thận!
     */
    private static void testDelete() {
        System.out.println("--- Test 6: Test delete() ---");
        System.out.println("⚠️  CẢNH BÁO: Test này sẽ XÓA dữ liệu!");
        
        // Tạo user mới để test delete
        String testEmail = "delete_test_" + System.currentTimeMillis() + "@test.com";
        User newUser = new User("Delete", "Test", testEmail);
        
        // Insert trước
        UserDAO.insert(newUser);
        System.out.println("Đã tạo user test: " + testEmail);
        
        // Verify user tồn tại
        User verifyUser = UserDAO.selectUser(testEmail);
        if (verifyUser != null) {
            System.out.println("✅ User tồn tại, ID: " + verifyUser.getUserId());
            
            // Delete
            int result = UserDAO.delete(verifyUser);
            
            if (result > 0) {
                System.out.println("✅ Delete thành công!");
                
                // Verify đã bị xóa
                User deletedUser = UserDAO.selectUser(testEmail);
                if (deletedUser == null) {
                    System.out.println("✅ Verify: User đã bị xóa khỏi database");
                } else {
                    System.out.println("❌ User vẫn còn trong database");
                }
            } else {
                System.out.println("❌ Delete thất bại");
            }
        }
        System.out.println();
    }
}
