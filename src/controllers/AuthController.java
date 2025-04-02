package controllers;

import models.User;
import enumeration.UserStatus;
import services.UserService;
import view.AuthView;

public class AuthController {
    private UserService userService;
    private AuthView authView;

    public AuthController(AuthView authView) {
        this.authView = authView;
        this.userService = UserService.getInstance();
    }

    /**
     * Handles user login process
     * 
     * @param nric User's NRIC
     * @param password User's password
     * @return Logged-in user or null if login fails
     */
    public User login(String nric, String password) {
        // Validate NRIC format
        if (!isValidNRIC(nric)) {
            authView.displayError("Invalid NRIC format");
            return null;
        }

        // Attempt authentication
        User user = userService.authenticateUser(nric, password);
        
        if (user == null) {
            authView.displayError("Invalid credentials");
            return null;
        }

        // Log successful login
        authView.displaySuccess("Login successful");
        return user;
    }

    /**
     * Handles user logout process
     */
    public void logout() {
        userService.setCurrentUser(null);
        authView.displaySuccess("Logged out successfully");
    }

    /**
     * Changes user password
     * 
     * @param nric User's NRIC
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password change successful
     */
    public boolean changePassword(String nric, String oldPassword, String newPassword) {
        // Password complexity check
        if (!isValidPassword(newPassword)) {
            authView.displayError("Password does not meet complexity requirements");
            return false;
        }

        try {
            boolean changed = userService.changePassword(nric, oldPassword, newPassword);
            if (changed) {
                authView.displaySuccess("Password changed successfully");
            } else {
                authView.displayError("Failed to change password");
            }
            return changed;
        } catch (Exception e) {
            authView.displayError("Error changing password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates NRIC format
     * 
     * @param nric NRIC to validate
     * @return true if NRIC is valid
     */
    private boolean isValidNRIC(String nric) {
        return nric != null && nric.matches("^[ST]\\d{7}[A-Z]$");
    }

    /**
     * Validates password complexity
     * 
     * @param password Password to validate
     * @return true if password meets complexity requirements
     */
    private boolean isValidPassword(String password) {
        // Example password complexity rules
        return password != null && 
               password.length() >= 8 && 
               password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }
}