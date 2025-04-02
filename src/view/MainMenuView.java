package view;

import controller.UserController;
import models.User;

import java.util.Scanner;

public class MainMenuView {
    private UserController userController;
    private Scanner scanner;

    public MainMenuView(UserController userController) {
        this.userController = userController;
        this.scanner = new Scanner(System.in);
    }

    public void displayMainMenu() {
        User currentUser = userController.getCurrentUser();
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("Welcome, " + currentUser.getName() + "!");
        System.out.println("1. View Projects");
        System.out.println("2. Manage Applications");
        System.out.println("3. Manage Enquiries");
        System.out.println("4. Change Password");
        System.out.println("5. Logout");

        if (currentUser.isHdbManager()) {
            System.out.println("6. Generate Reports");
        }
    }

    public void handleUserInput() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> viewProjects();
            case 2 -> manageApplications();
            case 3 -> manageEnquiries();
            case 4 -> changePassword();
            case 5 -> logout();
            case 6 -> {
                if (userController.getCurrentUser().isHdbManager()) {
                    generateReports();
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            }
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void viewProjects() {
        // Navigate to ProjectView
    }

    private void manageApplications() {
        // Navigate to ApplicationView
    }

    private void manageEnquiries() {
        // Navigate to EnquiryView
    }

    private void changePassword() {
        System.out.println("Enter current password:");
        String oldPassword = scanner.nextLine();
        System.out.println("Enter new password:");
        String newPassword = scanner.nextLine();

        boolean success = userController.changePassword(oldPassword, newPassword);
        if (success) {
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password. Please try again.");
        }
    }

    private void logout() {
        userController.logout();
        System.out.println("Logged out successfully.");
        // Navigate back to LoginView
    }

    private void generateReports() {
        System.out.println("Select report type:");
        System.out.println("1. Booking Report");
        System.out.println("2. Application Status Report");
        System.out.println("3. Project Application Report");

        int reportChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (reportChoice) {
            case 1 -> generateBookingReport();
            case 2 -> generateApplicationStatusReport();
            case 3 -> generateProjectApplicationReport();
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void generateBookingReport() {
        System.out.println("Enter filter criteria (project/flat-type/marital-status):");
        String filter = scanner.nextLine();
        System.out.println("Enter filter value:");
        String value = scanner.nextLine();

        String report = userController.generateReport(filter, value);
        System.out.println(report);
    }

    private void generateApplicationStatusReport() {
        String report = userController.generateReport("application-status", "");
        System.out.println(report);
    }

    private void generateProjectApplicationReport() {
        String report = userController.generateReport("project-application", "");
        System.out.println(report);
    }
}   