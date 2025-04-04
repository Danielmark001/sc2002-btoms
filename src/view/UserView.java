package view;

import java.util.Scanner;
import controllers.UserController;
import models.User;
import interfaces.IRequestView;

public class UserView implements IRequestView {
    private UserController userController;
    private Scanner scanner;

    public UserView(UserController userController) {
        this.userController = userController;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu() {
        System.out.println("\n===== USER MENU =====");
        System.out.println("1. View Profile");
        System.out.println("2. Back to Main Menu");
    }

    @Override
    public boolean handleRequest() {
        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            displayError("Invalid input. Please enter a number.");
            return true; // Continue in this view
        }
        
        switch (choice) {
            case 1:
                viewProfile();
                return true;
            case 2:
                System.out.println("Going back to main menu...");
                return false; // Return to previous menu
            default:
                displayError("Invalid choice. Please try again.");
                return true;
        }
    }
    
    @Override
    public boolean run() {
        boolean continueRunning = true;
        while (continueRunning) {
            displayMenu();
            continueRunning = handleRequest();
            
            if (continueRunning) {
                System.out.print("\nDo you want to continue in this menu? (Y/N): ");
                String choice = scanner.nextLine().trim().toUpperCase();
                if (!choice.equals("Y")) {
                    continueRunning = false;
                }
            }
        }
        return true;
    }

    private void viewProfile() {
        // Implementation for viewing user profile
        User currentUser = userController.getCurrentUser();
        if (currentUser != null) {
            displayUserDetails(currentUser);
        } else {
            displayError("No user logged in.");
        }
    }

    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public void displayError(String message) {
        System.out.println("ERROR: " + message);
    }
    
    public void displayUserDetails(User user) {
        System.out.println("User Details:");
        System.out.println("NRIC: " + user.getNric());
        System.out.println("Name: " + user.getName());
        System.out.println("Date of Birth: " + user.getDateOfBirth());
        System.out.println("Age: " + user.calculateAge());
        System.out.println("Marital Status: " + user.getMaritalStatus());
        
        if (user.getContactNumber() != null) {
            System.out.println("Contact Number: " + user.getContactNumber());
        }
        
        if (user.getEmail() != null) {
            System.out.println("Email: " + user.getEmail());
        }
    }
}