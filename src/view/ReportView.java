package view;

import controllers.UserController;

import java.util.Scanner;

public class ReportView {
    private UserController userController;
    private Scanner scanner;

    public ReportView(UserController userController) {
        this.userController = userController;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        System.out.println("\n===== REPORT MENU =====");
        System.out.println("1. Booking Report"); 
        System.out.println("2. Application Status Report");
        System.out.println("3. Project Application Report"); 
        System.out.println("4. Back");
    }

    public void handleUserInput() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> generateBookingReport();
            case 2 -> generateApplicationStatusReport();
            case 3 -> generateProjectApplicationReport();
            case 4 -> System.out.println("Going back...");
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