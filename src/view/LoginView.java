package view;

import controllers.LoginController;
import models.User;
import enumeration.MaritalStatus;

import java.util.Scanner;

public class LoginView {
    private LoginController loginController;
    private Scanner scanner;

    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        this.scanner = new Scanner(System.in);
    }

    public void displayLoginMenu() {
        System.out.println("\n===== LOGIN MENU =====");
        System.out.println("1. Login");
        System.out.println("2. Register as Applicant");
        System.out.println("3. Exit");
    }

    public void handleUserInput() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> login();
            case 2 -> registerApplicant();
            case 3 -> System.out.println("Exiting...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void login() {
        System.out.println("Enter NRIC:");
        String nric = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();

        User user = loginController.login(nric, password);
        if (user != null) {
            System.out.println("Login successful!");
            System.out.println("User type: " + loginController.getUserType(user));
            // Redirect to appropriate menu based on user type
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    private void registerApplicant() {
        System.out.println("Enter NRIC:");
        String nric = scanner.nextLine();
        System.out.println("Enter password:");
        String password = scanner.nextLine();
        System.out.println("Enter age:");
        int age = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.println("Enter marital status (SINGLE/MARRIED):");
        String maritalStatusStr = scanner.nextLine().toUpperCase();
        MaritalStatus maritalStatus = MaritalStatus.valueOf(maritalStatusStr);

        boolean success = true ? loginController.registerApplicant(nric, password, age, maritalStatus) == null : false;
        if (success) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed. Please try again.");
        }
    }
}