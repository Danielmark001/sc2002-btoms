package view;

import java.util.Scanner;

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
}