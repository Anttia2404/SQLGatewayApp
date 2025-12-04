package murach.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import murach.business.User;

public class UserDAO {
    
    /**
     * Insert a new user into the database using JDBC
     * @param user User object to insert
     * @return 1 if successful, 0 if failed
     */
    public static int insert(User user) {
        try {
            Class.forName("org.postgresql.Driver");
            
            String dbURL = "jdbc:postgresql://dpg-d47cvdi4d50c73834gmg-a.oregon-postgres.render.com:5432/murach";
            String username = "my_portfolio_db_vxq1_user";
            String password = "E3XY5g7Z35scTCzeB49CtUZFOAJVUiPG";
            
            java.sql.Connection connection = java.sql.DriverManager.getConnection(dbURL, username, password);
            java.sql.PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO \"user\" (firstname, lastname, email) VALUES (?, ?, ?)"
            );
            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getEmail());
            
            int result = statement.executeUpdate();
            
            statement.close();
            connection.close();
            
            return result > 0 ? 1 : 0;
        } catch (Exception e) {
            System.err.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Update an existing user in the database
     * @param user User object with updated information
     * @return 1 if successful, 0 if failed
     */
    public static int update(User user) {
        EntityManager em = null;
        EntityTransaction transaction = null;
        
        try {
            em = JPAUtil.getEntityManager();
            transaction = em.getTransaction();
            transaction.begin();
            
            // Find user by email
            User existingUser = selectUser(user.getEmail());
            if (existingUser != null) {
                existingUser.setFirstName(user.getFirstName());
                existingUser.setLastName(user.getLastName());
                em.merge(existingUser);
            }
            
            transaction.commit();
            return 1;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Delete a user from the database
     * @param user User object to delete
     * @return 1 if successful, 0 if failed
     */
    public static int delete(User user) {
        EntityManager em = null;
        EntityTransaction transaction = null;
        
        try {
            em = JPAUtil.getEntityManager();
            transaction = em.getTransaction();
            transaction.begin();
            
            // Find and remove user by email
            User userToDelete = selectUser(user.getEmail());
            if (userToDelete != null) {
                User managedUser = em.merge(userToDelete);
                em.remove(managedUser);
            }
            
            transaction.commit();
            return 1;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Delete a user from the database by userId
     * @param userId ID of the user to delete
     * @return 1 if successful, 0 if failed
     */
    public static int delete(int userId) {
        EntityManager em = null;
        EntityTransaction transaction = null;
        
        try {
            em = JPAUtil.getEntityManager();
            transaction = em.getTransaction();
            transaction.begin();
            
            // Find user by ID
            User user = em.find(User.class, userId);
            if (user != null) {
                em.remove(user);
            }
            
            transaction.commit();
            return 1;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error deleting user by ID: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
    
    /**
     * Select all users from the database using JDBC (same as SQL Gateway)
     * @return List of all users
     */
    public static java.util.List<User> selectAll() {
        java.util.List<User> users = new java.util.ArrayList<>();
        
        try {
            // Use JDBC like SQL Gateway does
            Class.forName("org.postgresql.Driver");
            
            String dbURL = "jdbc:postgresql://dpg-d47cvdi4d50c73834gmg-a.oregon-postgres.render.com:5432/murach";
            String username = "my_portfolio_db_vxq1_user";
            String password = "E3XY5g7Z35scTCzeB49CtUZFOAJVUiPG";
            
            java.sql.Connection connection = java.sql.DriverManager.getConnection(dbURL, username, password);
            java.sql.Statement statement = connection.createStatement();
            java.sql.ResultSet resultSet = statement.executeQuery("SELECT \"user\", email, firstname, lastname FROM \"user\" ORDER BY \"user\"");
            
            while (resultSet.next()) {
                User user = new User();
                user.setUserId(resultSet.getLong("user"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName(resultSet.getString("lastname"));
                user.setEmail(resultSet.getString("email"));
                users.add(user);
            }
            
            resultSet.close();
            statement.close();
            connection.close();
            
        } catch (Exception e) {
            System.err.println("Error selecting all users: " + e.getMessage());
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Check if an email already exists in the database using JDBC
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public static boolean emailExists(String email) {
        try {
            Class.forName("org.postgresql.Driver");
            
            String dbURL = "jdbc:postgresql://dpg-d47cvdi4d50c73834gmg-a.oregon-postgres.render.com:5432/murach";
            String username = "my_portfolio_db_vxq1_user";
            String password = "E3XY5g7Z35scTCzeB49CtUZFOAJVUiPG";
            
            java.sql.Connection connection = java.sql.DriverManager.getConnection(dbURL, username, password);
            java.sql.PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM \"user\" WHERE email = ?"
            );
            statement.setString(1, email);
            
            java.sql.ResultSet resultSet = statement.executeQuery();
            boolean exists = false;
            if (resultSet.next()) {
                exists = resultSet.getLong(1) > 0;
            }
            
            resultSet.close();
            statement.close();
            connection.close();
            
            return exists;
        } catch (Exception e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Select a user by email
     * @param email Email of the user to find
     * @return User object if found, null otherwise
     */
    public static User selectUser(String email) {
        EntityManager em = null;
        
        try {
            em = JPAUtil.getEntityManager();
            
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", email);
            
            return query.getSingleResult();
        } catch (NoResultException e) {
            // No user found with this email
            return null;
        } catch (Exception e) {
            System.err.println("Error selecting user: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            JPAUtil.closeEntityManager(em);
        }
    }
}
