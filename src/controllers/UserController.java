package bto_management_system.controller;

import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.User;
import bto_management_system.model.manager.UserManager;
import bto_management_system.view.MainMenuView;

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