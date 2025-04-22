package view;

/**
 * Provides common view components and utilities shared across the application.
 * 
 * This class contains static methods for displaying UI elements that are used
 * in multiple parts of the application, such as the splash screen that appears
 * when the application starts.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class CommonView {
    /**
     * Prints the splash screen for the BTO Management System.
     */
    public static void printSplashScreen() {
        System.out.println("========================================");
        System.out.println("  BTO Management System (BTOMS)");
        System.out.println("  Nanyang Technological University");
        System.out.println("========================================");
    }
}