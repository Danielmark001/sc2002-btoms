package controller;

import models.entity.HDBManager;
import models.entity.User;
import models.manager.UserManager;
import view.MainMenuView;

/**
 * Controller for handling user-related operations
 */
public class UserController {
    private MainMenuView mainMenuView;
    private UserManager userManager;
    
    /**
     * Constructor for UserController
     * 
     * @param mainMenuView View for main menu operations
     */
    public UserController(MainMenuView mainMenuView) {
        this.mainMenuView = mainMenuView;
        this.userManager = UserManager.getInstance();
    }
    
    /**
     * Gets the current logged-in user
     * 
     * @return Current user
     */
    public User getCurrentUser() {
        return userManager.getCurrentUser();
    }
    
    /**
     * Generates a report (for HDB Manager)
     * 
     * @param filter Filter for the report
     * @param value Value for the filter
     * @return Formatted report string
     */
    public String generateReport(String filter, String value) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return null;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        return manager.generateReport(filter, value);
    }
}