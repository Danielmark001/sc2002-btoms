package stores;

import java.util.HashMap;
import java.util.Map;
import models.User;
import models.enumeration.UserType;
import util.NRICValidator;

/**
 * Responsible for authentication operations
 * Follows the Singleton pattern
 */
public class AuthStore {
    private static AuthStore instance;
    private User currentUser;
    private final DataStore dataStore;
    
    /**
     * Private constructor for Singleton pattern
     */
    private AuthStore() {
        dataStore = DataStore.getInstance();
    }
    
    /**
     * Gets the singleton instance of AuthStore
     * @return AuthStore instance
     */
    public static AuthStore getInstance() {
        if (instance == null) {
            instance = new AuthStore();
        }
        return instance;
    }
    
    /**
     * Authenticates a user with NRIC and password
     * @param nric User's NRIC
     * @param password User's password
     * @return true if authentication successful, false otherwise
     */
    public boolean login(String nric, String password) {
        // Validate NRIC format
        if (!NRICValidator.isValidNRIC(nric)) {
            return false;
        }
        
        // Get user from data store
        User user = dataStore.getUserByNRIC(nric);
        
        // Check if user exists and password matches
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        
        return false;
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        currentUser = null;
    }
    
    /**
     * Gets the currently logged-in user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Changes the password for the current user
     * @param oldPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser != null && currentUser.getPassword().equals(oldPassword)) {
            currentUser.setPassword(newPassword);
            dataStore.updateUser(currentUser);
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the current user is an Applicant
     * @return true if user is an Applicant, false otherwise
     */
    public boolean isApplicant() {
        return currentUser != null && currentUser.getUserType() == UserType.APPLICANT;
    }
    
    /**
     * Checks if the current user is an HDB Officer
     * @return true if user is an HDB Officer, false otherwise
     */
    public boolean isHdbOfficer() {
        return currentUser != null && currentUser.getUserType() == UserType.HDB_OFFICER;
    }
    
    /**
     * Checks if the current user is an HDB Manager
     * @return true if user is an HDB Manager, false otherwise
     */
    public boolean isHdbManager() {
        return currentUser != null && currentUser.getUserType() == UserType.HDB_MANAGER;
    }
}