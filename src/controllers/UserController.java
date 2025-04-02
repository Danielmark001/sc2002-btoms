package controllers;

import models.User;
import models.Applicant;
import enumeration.UserStatus;
import enumeration.MaritalStatus;
import services.UserService;
import view.UserView;

import java.time.LocalDate;
import java.util.List;

public class UserController {
    private UserService userService;
    private UserView userView;

    public UserController(UserView userView) {
        this.userService = UserService.getInstance();
        this.userView = userView;
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
            User newUser = userService.createUser(
                nric, 
                name, 
                dateOfBirth, 
                maritalStatus, 
                UserStatus.APPLICANT
            );
            userView.displaySuccess("Applicant created successfully");
            return newUser;
        } catch (IllegalArgumentException e) {
            userView.displayError("Failed to create applicant: " + e.getMessage());
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
            userView.displaySuccess("Profile updated successfully");
            return updatedUser;
        } catch (Exception e) {
            userView.displayError("Failed to update profile: " + e.getMessage());
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
            return userService.getUserByNRIC(nric)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        } catch (Exception e) {
            userView.displayError("Failed to retrieve user details: " + e.getMessage());
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
            userService.deleteUser(nric);
            userView.displaySuccess("User deleted successfully");
            return true;
        } catch (Exception e) {
            userView.displayError("Failed to delete user: " + e.getMessage());
            return false;
        }
        

    }

    public void logout() {
        // Implement logout logic here
        userView.displayMessage("User logged out successfully");
    }

    
}