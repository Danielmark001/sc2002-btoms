package models.manager;

import java.util.List;

import interfaces.IUserService;
import models.entity.User;
import models.enumeration.MaritalStatus;
import models.enumeration.UserType;
import services.UserService;
import stores.AuthStore;
import stores.DataStore;
import util.NRICValidator;

/**
 * Manager class for handling user-related operations
 * Acts as a controller between the UI and the data layer
 */
public class UserManager {
    private final AuthStore authStore;
    private final DataStore dataStore;
    private final IUserService userService;
    
    /**
     * Constructor
     */
    public UserManager() {
        this.authStore = AuthStore.getInstance();
        this.dataStore = DataStore.getInstance();
        this.userService = UserService.getInstance();
    }
    
    /**
     * Authenticates a user
     * @param nric User's NRIC
     * @param password User's password
     * @return true if authentication successful, false otherwise
     */
    public boolean login(String nric, String password) {
        // Validate NRIC format
        if (!NRICValidator.isValidNRIC(nric)) {
            return false;
        }
        
        return authStore.login(nric, password);
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        authStore.logout();
    }
    
    /**
     * Gets the currently logged-in user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return authStore.getCurrentUser();
    }
    
    /**
     * Changes the password for the current user
     * @param oldPassword Current password for verification
     * @param newPassword New password to set
     * @return true if password changed successfully, false otherwise
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        return authStore.changePassword(oldPassword, newPassword);
    }
    
    /**
     * Checks if the current user is an Applicant
     * @return true if user is an Applicant, false otherwise
     */
    public boolean isApplicant() {
        return authStore.isApplicant();
    }
    
    /**
     * Checks if the current user is an HDB Officer
     * @return true if user is an HDB Officer, false otherwise
     */
    public boolean isHdbOfficer() {
        return authStore.isHdbOfficer();
    }
    
    /**
     * Checks if the current user is an HDB Manager
     * @return true if user is an HDB Manager, false otherwise
     */
    public boolean isHdbManager() {
        return authStore.isHdbManager();
    }
    
    /**
     * Gets a user by NRIC
     * @param nric User's NRIC
     * @return User object if found, null otherwise
     */
    public User getUserByNRIC(String nric) {
        return dataStore.getUserByNRIC(nric);
    }
    
    /**
     * Gets all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return dataStore.getAllUsers();
    }
    
    /**
     * Gets users by type
     * @param userType User type to filter by
     * @return List of users of the specified type
     */
    public List<User> getUsersByType(UserType userType) {
        return dataStore.getUsersByType(userType);
    }
    
    /**
     * Creates a new user
     * @param nric User's NRIC
     * @param password User's password
     * @param age User's age
     * @param maritalStatus User's marital status
     * @param userType User's type
     * @return true if created successfully, false otherwise
     */
    public boolean createUser(String nric, String password, int age, MaritalStatus maritalStatus, UserType userType) {
        // Validate NRIC format
        if (!NRICValidator.isValidNRIC(nric)) {
            return false;
        }
        
        // Check if user already exists
        if (dataStore.getUserByNRIC(nric) != null) {
            return false;
        }
        
        // Create new user
        User user = new User(nric, password, age, maritalStatus, userType);
        return dataStore.addUser(user);
    }
    
    /**
     * Updates an existing user
     * @param user User to update
     * @return true if updated successfully, false otherwise
     */
    public boolean updateUser(User user) {
        return dataStore.updateUser(user);
    }
    
    /**
     * Loads users from a CSV file
     * @param filepath Path to the CSV file
     * @return Number of users loaded
     */
    public int loadUsersFromCsv(String filepath) {
        return dataStore.loadUsersFromCsv(filepath);
    }
    
    /**
     * Validates if a given string is in the correct NRIC format
     * @param nric NRIC to validate
     * @return true if valid, false otherwise
     */
    public boolean validateNRIC(String nric) {
        return NRICValidator.isValidNRIC(nric);
    }
}