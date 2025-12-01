package murach.data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import murach.business.User;

public class UserDAO {
    
    /**
     * Insert a new user into the database
     * @param user User object to insert
     * @return 1 if successful, 0 if failed
     */
    public static int insert(User user) {
        EntityManager em = null;
        EntityTransaction transaction = null;
        
        try {
            em = JPAUtil.getEntityManager();
            transaction = em.getTransaction();
            transaction.begin();
            
            em.persist(user);//đánh dấu
            
            transaction.commit();//insert
            return 1;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error inserting user: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            JPAUtil.closeEntityManager(em);
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
     * Check if an email already exists in the database
     * @param email Email to check
     * @return true if email exists, false otherwise
     */
    public static boolean emailExists(String email) {
        EntityManager em = null;
        
        try {
            em = JPAUtil.getEntityManager();
            
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", email);
            
            Long count = query.getSingleResult();
            return count > 0;
        } catch (Exception e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            JPAUtil.closeEntityManager(em);
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
