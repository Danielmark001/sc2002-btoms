package controllers;

import models.User;
import models.Applicant;
import enumeration.MaritalStatus;
import enumeration.UserType;
import services.UserService;
import services.ReportService;
import util.InputValidator;
import view.UserView;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class UserController {
    private UserService userService;
    private UserView userView;
    private ReportService reportService;

    /**
     * Constructor for UserController
     * 
     * @param userView View for user operations
     */
    public UserController(UserView userView) {
        this.userService = UserService.getInstance();
        this.userView = userView;
        this.reportService = new ReportService();
    }

    /**
     * Default constructor
     */
    public UserController() {
        this.userService = UserService.getInstance();
        this.reportService = new ReportService();
    }

    /**
     * Creates a new applicant user
     * 
     * @param nric User's NRIC
     * @param name User's name
     * @param dateOfBirth User's date of birth
     * @param maritalStatus User's marital status
     * @return Created user
     */
    public User createApplicant(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus) {
        try {
            // Validate NRIC
            if (!InputValidator.isValidNRIC(nric)) {
                throw new IllegalArgumentException("Invalid NRIC format");
            }
            
            // Validate name
            InputValidator.validateNonEmpty(name, "Name cannot be empty");
            InputValidator.validateLength(name, 2, 100, "Name must be between 2 and 100 characters");
            
            // Validate date of birth
            if (dateOfBirth == null) {
                throw new IllegalArgumentException("Date of birth cannot be empty");
            }
            
            if (dateOfBirth.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Date of birth cannot be in the future");
            }
            
            // Calculate age
            int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
            
            // Validate age based on marital status
            if (maritalStatus == MaritalStatus.SINGLE && age < 35) {
                throw new IllegalArgumentException("Single applicants must be at least 35 years old");
            }
            
            if (maritalStatus == MaritalStatus.MARRIED && age < 21) {
                throw new IllegalArgumentException("Married applicants must be at least 21 years old");
            }
            
            // Create user
            User newUser = userService.createUser(nric, name, age, maritalStatus, UserType.APPLICANT);
            
            if (userView != null) {
                userView.displaySuccess("Applicant created successfully");
            }
            
            return newUser;
        } catch (IllegalArgumentException e) {
            if (userView != null) {
                userView.displayError("Failed to create applicant: " + e.getMessage());
            }
            return null;
        } catch (Exception e) {
            if (userView != null) {
                userView.displayError("An unexpected error occurred: " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Updates user profile
     * 
     * @param nric User's NRIC
     * @param name New name (can be null)
     * @param contactNumber New contact number (can be null)
     * @param email New email (can be null)
     * @return Updated user
     */
    public User updateUserProfile(String nric, String name, String contactNumber, String email) {
        try {
            User updatedUser = userService.updateUser(nric, name, contactNumber, email);
            if (userView != null) {
                userView.displaySuccess("Profile updated successfully");
            }
            return updatedUser;
        } catch (Exception e) {
            if (userView != null) {
                userView.displayError("Failed to update profile: " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Retrieves user details
     * 
     * @param nric User's NRIC
     * @return User details
     */
    public User getUserDetails(String nric) {
        try {
            User user = userService.getUserByNRIC(nric);
            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }
            return user;
        } catch (Exception e) {
            if (userView != null) {
                userView.displayError("Failed to retrieve user details: " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Gets all users
     * 
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Checks if a user is eligible for a specific project
     * 
     * @param nric User's NRIC
     * @return true if user is eligible
     */
    public boolean checkUserEligibility(String nric) {
        User user = getUserDetails(nric);
        if (user instanceof Applicant) {
            Applicant applicant = (Applicant) user;
            // Implement eligibility logic based on age and marital status
            int age = applicant.calculateAge();
            return (applicant.getMaritalStatus() == MaritalStatus.SINGLE && age >= 35) ||
                    (applicant.getMaritalStatus() == MaritalStatus.MARRIED && age >= 21);
        }
        return false;
    }
    
    /**
     * Deletes a user
     * 
     * @param nric User's NRIC
     * @return true if deletion succeeds
     */
    public boolean deleteUser(String nric) {
        try {
            boolean success = userService.deleteUser(nric);
            if (success && userView != null) {
                userView.displaySuccess("User deleted successfully");
            }
            return success;
        } catch (Exception e) {
            if (userView != null) {
                userView.displayError("Failed to delete user: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        userService.logout();
        if (userView != null) {
            userView.displayMessage("User logged out successfully");
        }
    }
    
    /**
     * Starts the controller's main operation
     */
    public void start() {
        if (userView != null) {
            userView.displayMessage("UserController started");
        }
    }
    
    /**
     * Gets the current logged-in user
     * 
     * @return Current user
     */
    public User getCurrentUser() {
        return userService.getCurrentUser();
    }
    
    /**
     * Generates a report based on filter and value
     * 
     * @param filter Report filter
     * @param value Value to filter by
     * @return Generated report
     */
    public String generateReport(String filter, String value) {
        // Generate the appropriate report
        switch (filter.toLowerCase()) {
            case "project":
                return reportService.generateProjectReport(value);
            case "flat-type":
                return reportService.generateFlatTypeReport(value);
            case "marital-status":
                return reportService.generateMaritalStatusReport(value);
            case "application-status":
                return reportService.generateApplicationStatusReport();
            case "project-application":
                return reportService.generateProjectApplicationReport();
            default:
                return "Invalid filter criteria. Please use project, flat-type, marital-status, application-status, or project-application.";
        }
    }
    
    /**
     * Changes the current user's password
     * 
     * @param oldPassword Current password
     * @param newPassword New password
     * @return true if password change succeeds
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null ||
                oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
            if (userView != null) {
                userView.displayError("Passwords cannot be empty");
            }
            return false;
        }

        boolean success = userService.changePassword(oldPassword, newPassword);
        if (success) {
            if (userView != null) {
                userView.displaySuccess("Password changed successfully");
            }
        } else {
            if (userView != null) {
                userView.displayError("Failed to change password. Check your current password and try again.");
            }
        }

        return success;
    }
    public User getUserByNRIC(String message) {
        User user = userService.getUserByNRIC(message);
        if (user == null) {
            if (userView != null) {
                userView.displayError("User not found");
            }
        }
        return user;
    }
}