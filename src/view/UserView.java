package view;

import java.util.Scanner;
import controllers.UserController;

public class UserView {
    private UserController userController;

    public UserView(UserController userController) {
        this.userController = userController;
    }

    public void displayMenu() {
        System.out.println("\n===== USER MENU =====");
        System.out.println("1. Create Applicant");
        System.out.println("2. Update Profile");
        System.out.println("3. View Profile");
        System.out.println("4. Back to Main Menu");
    }
    Scanner scanner = new Scanner(System.in);

    public void handleUserInput() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> createApplicant();
            case 2 -> updateProfile();
            case 3 -> viewProfile();
            case 4 -> System.out.println("Going back to main menu...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void createApplicant() {
        // Implementation for creating an applicant
    }

    private void updateProfile() {
        // Implementation for updating user profile
    }

    private void viewProfile() {
        // Implementation for viewing user profile
    }


    
    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public void displayError(String message) {
        System.out.println("ERROR: " + message);
    }

    public void displayInfo(String message) {
        System.out.println("INFO: " + message);
    }

    public void displayWarning(String message) {
        System.out.println("WARNING: " + message);
    }
    public void displayMessage(String message) {
        System.out.println(message);
    }
    public void displayUserDetails(User user) {
        System.out.println("User Details:");
        System.out.println("NRIC: " + user.getNric());
        System.out.println("Name: " + user.getName());
        System.out.println("Date of Birth: " + user.getDateOfBirth());
        System.out.println("Marital Status: " + user.getMaritalStatus());
        System.out.println("Contact Number: " + user.getContactNumber());
        System.out.println("Email: " + user.getEmail());
    }
}
