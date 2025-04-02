package interfaces;

import java.util.List;

import models.User;
import models.enumeration.MaritalStatus;
import models.enumeration.UserType;

/**
 * Interface for the User Service
 * Defines methods for user-related operations
 */
public interface IUserService {
    
    /**
     * Gets the singleton instance of UserService
     * @return UserService instance
     */
    public static IUserService getInstance() {
        return null; // Implemented by concrete class
    }
    
    /**
     * Authenticates a user by user ID and password
     * @param nric User's NRIC
     * @param password User's password
     * @return true if authentication successful, false otherwise
     */
    boolean login(String nric, String password);
    
    /**
     * Logs out the current user
     */
    void logout();
    
    /**
     * Gets the currently logged-in user
     * @return Current User object
     */
    User getCurrentUser();
    
    /**
     * Changes the password for the current user
     * @param oldPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password changed successfully, false otherwise
     */
    boolean changePassword(String oldPassword, String newPassword);
    
    /**
     * Checks if the current user is an Applicant
     * @return true if user is an Applicant, false otherwise
     */
    boolean isApplicant();
    
    /**
     * Checks if the current user is an HDB Officer
     * @return true if user is an HDB Officer, false otherwise
     */
    boolean isHdbOfficer();
    
    /**
     * Checks if the current user is an HDB Manager
     * @return true if user is an HDB Manager, false otherwise
     */
    boolean isHdbManager();
    
    /**
     * Gets a user by their ID
     * @param nric User's NRIC
     * @return User object if found, null otherwise
     */
    User getUserByNRIC(String nric);
    
    /**
     * Gets all users
     * @return List of all users
     */
    List<User> getAllUsers();
    
    /**
     * Gets users by type
     * @param userType User type to filter by
     * @return List of filtered users
     */
    List<User> getUsersByType(UserType userType);
    
    /**
     * Creates a new user
     * @param nric User's NRIC
     * @param password User's password
     * @param age User's age
     * @param maritalStatus User's marital status
     * @param userType User's type
     * @return true if created successfully, false otherwise
     */
    boolean createUser(String nric, String password, int age, MaritalStatus maritalStatus, UserType userType);
    
    /**
     * Updates an existing user
     * @param user User to update
     * @return true if updated successfully, false otherwise
     */
    boolean updateUser(User user);
    
    /**
     * Validates if a given string is in the correct NRIC format
     * @param nric NRIC to validate
     * @return true if valid, false otherwise
     */
    boolean validateNRIC(String nric);
}