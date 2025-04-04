package controllers;

import java.util.Scanner;

import exceptions.BTOSystemException;
import models.User;
import services.UserService;
import stores.AuthStore;
import view.AuthView;

/**
 * Controller for handling authentication operations
 */
public class AuthController {
    private static AuthController instance;
    private UserService userService;
    private AuthView authView;

    /**
     * Constructor
     * 
     * @param authView View for authentication operations
     */
    public AuthController(AuthView authView) {
        this.authView = authView;
        this.userService = UserService.getInstance();
    }
    
    /**
     * Private constructor for singleton pattern
     */
    private AuthController() {
        this.userService = UserService.getInstance();
    }
    
    /**
     * Gets singleton instance of AuthController
     * 
     * @return AuthController instance
     */
    public static synchronized AuthController getInstance() {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }
    
    /**
     * Sets the auth view
     * 
     * @param authView View to set
     */
    public void setAuthView(AuthView authView) {
        this.authView = authView;
    }

    /**
     * Handles user login process
     * 
     * @param nric User's NRIC
     * @param password User's password
     * @return Logged-in user or null if login fails
     */
    // In AuthController.java - update the login method
public User login(String nric, String password) {
    // Validate NRIC format
    if (!isValidNRIC(nric)) {
        if (authView != null) {
            authView.displayError("Invalid NRIC format");
        }
        return null;
    }

    try {
        // Attempt authentication with potential security exceptions
        boolean authenticated = userService.login(nric, password);
        
        if (!authenticated) {
            if (authView != null) {
                authView.displayError("Invalid credentials");
            }
            return null;
        }

        // Log successful login
        if (authView != null) {
            authView.displaySuccess("Login successful");
        }
        
        return userService.getCurrentUser();
    } catch (BTOSystemException e) {
        // Handle security-specific exceptions (like account locked)
        if (e.getErrorCode() == BTOSystemException.ErrorCode.INSUFFICIENT_USER_PERMISSIONS) {
            if (authView != null) {
                authView.displayError(e.getMessage());
            }
        } else {
            if (authView != null) {
                authView.displayError("Login failed: " + e.getMessage());
            }
        }
        return null;
    }
}

// Update the static startSession method
public static boolean startSession() {
    // If user is already logged in, just return true
    if (AuthStore.isLoggedIn()) {
        return true;
    }
    
    Scanner scanner = new Scanner(System.in);
    int attempts = 0;
    
    while (true) {
        System.out.println("\n===== LOGIN =====");
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine().trim();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        
        // Get the singleton instance
        AuthController authController = AuthController.getInstance();
        
        try {
            // Attempt to login
            User user = authController.login(nric, password);
            
            if (user != null) {
                System.out.println("Login successful! Welcome, " + user.getName() + ".");
                return true;
            } else {
                System.out.println("Invalid credentials. Please try again.");
            }
        } catch (BTOSystemException e) {
            if (e.getErrorCode() == BTOSystemException.ErrorCode.INSUFFICIENT_USER_PERMISSIONS) {
                // This is for account locked scenario
                System.out.println(e.getMessage());
                scanner.close();
                return false;
            }
        }
    }
}
    /**
     * Handles user logout process
     */
    public void logout() {
        userService.logout();
        if (authView != null) {
            authView.displaySuccess("Logged out successfully");
        }
    }

    /**
     * Changes user password
     * 
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password change successful
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        // Password complexity check
        if (!isValidPassword(newPassword)) {
            if (authView != null) {
                authView.displayError("Password does not meet complexity requirements");
            }
            return false;
        }

        try {
            boolean changed = userService.changePassword(oldPassword, newPassword);
            if (changed) {
                if (authView != null) {
                    authView.displaySuccess("Password changed successfully");
                }
            } else {
                if (authView != null) {
                    authView.displayError("Failed to change password");
                }
            }
            return changed;
        } catch (Exception e) {
            if (authView != null) {
                authView.displayError("Error changing password: " + e.getMessage());
            }
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
        // For now, just check length as a minimal requirement
        return password != null && password.length() >= 8;
    }
    
    /**
     * Gets the currently logged-in user
     * 
     * @return Current user
     */
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }
    


    
    /**
     * Ends the current authentication session
     */
    public static void endSession() {
        AuthStore.getInstance().logout();
    }
}