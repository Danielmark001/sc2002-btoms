package controllers;

import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.User;  
import enumeration.MaritalStatus;
import enumeration.UserType;
import services.UserService;
import view.LoginView;

/**
 * Controller for handling login-related operations
 */
public class LoginController {
    private LoginView loginView;
    private UserService userService;
    private static LoginController instance;
    
    /**
     * Constructor for LoginController
     * 
     * @param loginView View for login operations  
     */
    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        this.userService = UserService.getInstance();
    }
    
    /**
     * Private constructor for singleton pattern
     */
    private LoginController() {
        this.userService = UserService.getInstance();
    }
    
    /**
     * Gets singleton instance of LoginController
     * 
     * @return LoginController instance
     */
    public static synchronized LoginController getInstance() {
        if (instance == null) {
            instance = new LoginController();
        }
        return instance;
    }
    
    /**
     * Attempts to log in a user
     * 
     * @param nric NRIC of the user
     * @param password Password of the user
     * @return User object if login succeeds, null otherwise
     */
    public User login(String nric, String password) {
        if (!userService.login(nric, password)) {
            return null;
        }
        return userService.getCurrentUser();
    }
    
    /**
     * Determines user type and returns appropriate string 
     * 
     * @param user User to check
     * @return String representing user type
     */
    public String getUserType(User user) {
        if (user == null) {
            return "UNKNOWN";
        }
        
        return user.getUserType().toString();
    }
    
    /**
     * Registers a new applicant
     * 
     * @param nric NRIC of the applicant
     * @param password Password for the account
     * @param age Age of the applicant 
     * @param maritalStatus Marital status of the applicant
     * @return true if registration succeeds
     */
    public boolean registerApplicant(String nric, String password, int age, MaritalStatus maritalStatus) {
        return userService.registerApplicant(nric, password, age, maritalStatus);
    }
    
    /**
     * Changes a user's password
     * 
     * @param oldPassword Old password to verify
     * @param newPassword New password to set
     * @return true if password change succeeds
     */  
    public boolean changePassword(String oldPassword, String newPassword) {
        return userService.changePassword(oldPassword, newPassword);
    }
    
    /**
     * Logs out the current user  
     */
    public void logout() {
        userService.logout();
    }

    /**
     * Gets the current logged-in user
     * 
     * @return Current user object
     */
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }
    
    /**
     * Checks if the current user is an Applicant
     * @return true if user is an Applicant, false otherwise
     */
    public boolean isApplicant() {
        return userService.isApplicant();
    }
    
    /**
     * Checks if the current user is an HDB Officer
     * @return true if user is an HDB Officer, false otherwise
     */
    public boolean isHdbOfficer() {
        return userService.isHdbOfficer();
    }
    
    /**
     * Checks if the current user is an HDB Manager
     * @return true if user is an HDB Manager, false otherwise
     */
    public boolean isHdbManager() {
        return userService.isHdbManager();
    }
}