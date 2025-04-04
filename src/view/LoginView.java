// In LoginView.java
package view;

import controllers.LoginController;
import models.User;
import enumeration.MaritalStatus;
import util.InputValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class LoginView {
    private LoginController loginController;
    private Scanner scanner;

    public LoginView(LoginController loginController) {
        this.loginController = loginController;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Handles the main login menu
     * @return true if the user wants to continue, false to exit
     */
    public boolean handleMainMenu() {
        int choice = CommonView.displayLoginOptions();
        
        switch (choice) {
            case 1:
                login();
                return true;
            case 2:
                registerApplicant();
                return true;
            case 3:
                System.out.println("Thank you for using the BTO Management System. Goodbye!");
                return false;
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }

    /**
     * Processes user login
     * @return true if login succeeds
     */
    private boolean login() {
        CommonView.printHeader("LOGIN");
        
        String nric = CommonView.getInput("Enter NRIC");
        String password = CommonView.getInput("Enter password");

        User user = loginController.login(nric, password);
        if (user != null) {
            CommonView.showSuccess("Login successful!");
            System.out.println("Welcome, " + user.getName() + " (" + loginController.getUserType(user) + ")");
            CommonView.pressEnterToContinue();
            return true;
        } else {
            CommonView.showError("Invalid credentials. Please try again.");
            CommonView.pressEnterToContinue();
            return false;
        }
    }

    /**
     * Handles applicant registration process
     */
    private void registerApplicant() {
        CommonView.printHeader("APPLICANT REGISTRATION");
        
        // Get NRIC
        String nric = null;
        boolean validNRIC = false;
        while (!validNRIC) {
            nric = CommonView.getInput("Enter NRIC (e.g., S1234567A)");
            if (InputValidator.isValidNRIC(nric)) {
                validNRIC = true;
            } else {
                CommonView.showError("Invalid NRIC format. Must start with S or T, followed by 7 digits, and end with a letter.");
            }
        }
        
        // Get name
        String name = CommonView.getInput("Enter full name");
        
        // Get date of birth
        LocalDate dateOfBirth = null;
        while (dateOfBirth == null) {
            String dobInput = CommonView.getInput("Enter date of birth (YYYY-MM-DD)");
            try {
                dateOfBirth = LocalDate.parse(dobInput, DateTimeFormatter.ISO_LOCAL_DATE);
                
                // Validate age based on marital status criteria
                int age = java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
                if (age < 21) {
                    CommonView.showError("You must be at least 21 years old to register.");
                    dateOfBirth = null;
                }
            } catch (DateTimeParseException e) {
                CommonView.showError("Invalid date format. Please use YYYY-MM-DD format.");
            }
        }
        
        // Get marital status
        MaritalStatus maritalStatus = null;
        while (maritalStatus == null) {
            String statusInput = CommonView.getInput("Enter marital status (SINGLE/MARRIED)").toUpperCase();
            try {
                maritalStatus = MaritalStatus.valueOf(statusInput);
                
                // Validate age based on marital status
                int age = java.time.Period.between(dateOfBirth, LocalDate.now()).getYears();
                if (maritalStatus == MaritalStatus.SINGLE && age < 35) {
                    CommonView.showError("Single applicants must be at least 35 years old.");
                    maritalStatus = null;
                }
            } catch (IllegalArgumentException e) {
                CommonView.showError("Invalid marital status. Please enter SINGLE or MARRIED.");
            }
        }
        
        // Get password
        String password = null;
        boolean validPassword = false;
        while (!validPassword) {
            password = CommonView.getInput("Enter password (min 8 characters, must include uppercase & number)");
            if (password.length() >= 8 && password.matches(".*[A-Z].*") && password.matches(".*\\d.*")) {
                validPassword = true;
            } else {
                CommonView.showError("Password must be at least 8 characters and include at least one uppercase letter and one number.");
            }
        }
        
        // Confirm registration
        CommonView.printSeparator();
        System.out.println("Please confirm your registration details:");
        System.out.println("NRIC: " + nric);
        System.out.println("Name: " + name);
        System.out.println("Date of Birth: " + dateOfBirth);
        System.out.println("Marital Status: " + maritalStatus);
        
        String confirmation = CommonView.getInput("Confirm registration (Y/N)");
        if (confirmation.equalsIgnoreCase("Y")) {
            User user = loginController.registerApplicant(nric, password, java.time.Period.between(dateOfBirth, LocalDate.now()).getYears(), maritalStatus);
            if (user != null) {
                CommonView.showSuccess("Registration successful! You can now login with your NRIC and password.");
            } else {
                CommonView.showError("Registration failed. This NRIC may already be registered.");
            }
        } else {
            CommonView.showError("Registration cancelled.");
        }
        
        CommonView.pressEnterToContinue();
    }
}