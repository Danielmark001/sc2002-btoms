// src/stores/AuthStore.java
package stores;

import models.User;
import security.AuthenticationErrorHandler;
import services.UserService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import enumeration.UserType;
import exceptions.BTOSystemException;
import util.NRICValidator;

/**
 * Responsible for authentication operations
 * Follows the Singleton pattern
 */
public class AuthStore {
    private static User currentUser;
    private static AuthStore instance;
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
     * Logs out the current user
     */
    public void logout() {
        setCurrentUser(null);
    }
    
    /**
     * Gets the currently logged-in user
     * @return Current user or null if not logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Sets the current user
     * @param user User to set as current
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * Checks if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Changes the password for the current user
     * @param oldPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser != null && currentUser.authenticate(oldPassword)) {
            currentUser.setPassword(oldPassword, newPassword);
            dataStore.updateUser(currentUser);
            return true;
        }
        return false;
    }
    
    /**
     * Checks if the current user is an Applicant
     * @return true if user is an Applicant, false otherwise
     */
    public static boolean isApplicant() {
        return currentUser != null && currentUser.getUserType() == UserType.APPLICANT;
    }
    
    /**
     * Checks if the current user is an HDB Officer
     * @return true if user is an HDB Officer, false otherwise
     */
    public static boolean isHdbOfficer() {
        return currentUser != null && currentUser.getUserType() == UserType.OFFICER;
    }
    
    /**
     * Checks if the current user is an HDB Manager
     * @return true if user is an HDB Manager, false otherwise
     */
    public static boolean isHdbManager() {
        return currentUser != null && currentUser.getUserType() == UserType.MANAGER;
    }

public boolean login(String nric, String password) {
    // Validate NRIC format
    if (!NRICValidator.isValidNRIC(nric)) {
        return false;
    }
    
    // Get user from data store
    User user = dataStore.getUserByNRIC(nric);
    
    if (user == null) {
        // Use security handler to track failed login attempt with unknown user
        // Using a dummy IP for demonstration - in a real system, this would be from the request
        String ipAddress = "127.0.0.1";
        AuthenticationErrorHandler.handleLoginFailure(nric, ipAddress);
        
        // Return generic error to prevent user enumeration
        return false;
    }
    
    // Check if user account is locked
    try {
        // Using a dummy IP for demonstration
        String ipAddress = "127.0.0.1";
        
        // Check if password matches
        UserService userService = UserService.getInstance();
        boolean authenticated = userService.authenticate(user, password);
        
        if (authenticated) {
            // Handle successful login
            AuthenticationErrorHandler.handleLoginSuccess(nric, ipAddress);
            setCurrentUser(user);
            return true;
        } else {
            // Handle failed login
            AuthenticationErrorHandler.handleLoginFailure(nric, ipAddress);
            return false;
        }
    } catch (BTOSystemException e) {
        // If the handler throws an exception (e.g., for account locked)
        // Return false but don't increment failed attempts again
        return false;
    }
}
// Add a field to track login attempts
private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();
}