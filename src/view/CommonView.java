package view;

import java.util.Scanner;

// In CommonView.java
public class CommonView {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Prints the splash screen for the BTO Management System.
     */
    public static void printSplashScreen() {
        System.out.println("========================================");
        System.out.println("  BTO Management System (BTOMS)");
        System.out.println("  Nanyang Technological University");
        System.out.println("========================================");
        System.out.println("Welcome to the Build-To-Order (BTO) Management System");
        System.out.println("----------------------------------------------------");
    }

    /**
     * Displays the main login options menu.
     * @return The user's selection
     */
    public static int displayLoginOptions() {
        System.out.println("\n===== WELCOME =====");
        System.out.println("1. Login");
        System.out.println("2. Register as Applicant");
        System.out.println("3. Exit");
        System.out.print("\nPlease select an option: ");
        
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1; // Invalid input
        }
    }

    /**
     * Displays a generic header for menu screens.
     * 
     * @param title Title of the current screen
     */
    public static void printHeader(String title) {
        System.out.println("\n========================================");
        System.out.println("  " + title);
        System.out.println("========================================");
    }

    /**
     * Prompts user for input with a specific message.
     * 
     * @param prompt Message to display
     * @return User input as a string
     */
    public static String getInput(String prompt) {
        System.out.print(prompt + ": ");
        return scanner.nextLine().trim();
    }

    /**
     * Displays an error message.
     * 
     * @param message Error message to display
     */
    public static void showError(String message) {
        System.err.println("Error: " + message);
    }

    /**
     * Displays a success message.
     * 
     * @param message Success message to display
     */
    public static void showSuccess(String message) {
        System.out.println("Success: " + message);
    }

    /**
     * Prints a separator line.
     */
    public static void printSeparator() {
        System.out.println("----------------------------------------------------");
    }

    /**
     * Closes the scanner to prevent resource leaks.
     */
    public static void closeScanner() {
        scanner.close();
    }
    
    /**
     * Waits for the user to press Enter to continue.
     */
    public static void pressEnterToContinue() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
}