// File: bto_management_system/controller/LoginController.java
package controllers;

import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.User;  
import enumeration.MaritalStatus;
import services.UserService;
import view.LoginView;

/**
 * Controller for handling login-related operations
 */
public class LoginController {
    private LoginView loginView;
    private UserService userService;
    
    /**
     * Constructor for LoginController
     * 
     * @param loginView View for login operations  
     */
    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        this.userService = new UserService();
    }
    
    /**
     * Attempts to log in a user
     * 
     * @param nric NRIC of the user
     * @param password Password of the user
     * @return User object if login succeeds, null otherwise
     */
    public User login(String nric, String password) {
        return userService.authenticateUser(nric, password);
    }
    
    /**
     * Determines user type and returns appropriate string 
     * 
     * @param user User to check
     * @return String representing user type
     */
    public String getUserType(User user) {
        if (user instanceof Applicant) {
            return "APPLICANT";
        } else if (user instanceof HDBOfficer) {
            return "OFFICER";  
        } else if (user instanceof HDBManager) {
            return "MANAGER";
        }
        return "UNKNOWN";
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
    
    
    public static LoginController getInstance() {
        return new LoginController(new LoginView());
    }
    
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }
    public boolean isApplicant() {
        return userService.isApplicant();
    }
    public boolean isHdbOfficer() {
        return userService.isHdbOfficer();
    }
    public boolean isHdbManager() {
        return userService.isHdbManager();
    }
}