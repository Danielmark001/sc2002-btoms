package view;

import java.util.Scanner;
import interfaces.IRequestView;
import controllers.LoginController;
import models.User;

/**
 * Simplified abstract base class for all views in the BTO Management System
 * Provides common functionality for views
 */
public abstract class BaseView implements IRequestView {
    protected Scanner scanner;
    protected LoginController loginController;
    
    /**
     * Constructor
     * @param scanner Scanner object for user input
     */
    public BaseView(Scanner scanner) {
        this.scanner = scanner;
        this.loginController = LoginController.getInstance();
    }
    
    /**
     * Gets the currently logged-in user
     * @return Current user or null if not logged in
     */
    protected User getCurrentUser() {
        return loginController.getCurrentUser();
    }
    
    /**
     * Main method that runs the view's workflow
     * Displays menu and handles user input in a loop until user chooses to go back
     * @return true if the user wants to continue using the application, false to exit
     */
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
    
    /**
     * Displays an error message
     * @param message Error message to display
     */
    public void displayError(String message) {
        System.out.println("ERROR: " + message);
    }
    
    /**
     * Displays a success message
     * @param message Success message to display
     */
    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }
}