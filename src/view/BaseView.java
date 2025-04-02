package views;

import java.util.Scanner;

import controllers.LoginController;
import interfaces.IRequestView;
import models.entity.User;

/**
 * Abstract base class for all views in the BTO Management System
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
     * Displays a message and waits for user to press Enter
     * @param message Message to display
     */
    protected void pressEnterToContinue(String message) {
        System.out.println(message);
        scanner.nextLine();
    }
    
    /**
     * Standard method to ask the user if they want to go back
     * @return true if the user wants to continue, false to go back
     */
    protected boolean continueOrGoBack() {
        System.out.print("\nDo you want to continue with this menu? (Y/N): ");
        String choice = scanner.nextLine().trim().toUpperCase();
        return choice.equals("Y");
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
                continueRunning = continueOrGoBack();
            }
        }
        return true;
    }
}