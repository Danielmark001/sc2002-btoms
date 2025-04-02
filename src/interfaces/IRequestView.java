package interfaces;

/**
 * Interface for view classes in the BTO Management System
 * Defines methods for displaying menus and handling user interaction
 */
public interface IRequestView {
    
    /**
     * Displays the view's menu options
     */
    void displayMenu();
    
    /**
     * Handles user input and processes user choices
     * @return true if the user wants to continue using this view, false to go back
     */
    boolean handleRequest();
    
    /**
     * Main method that runs the view's workflow
     * @return true if the user wants to continue using the application, false to exit
     */
    boolean run();
}	