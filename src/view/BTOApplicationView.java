package view;

import models.BTOApplication;
import utils.TextDecorationUtils;

/**
 * View class for displaying BTO application information
 */
public class BTOApplicationView {
    
    /**
     * Displays information about a BTO application
     * @param application The BTO application to display
     */
    public void displayApplicationInfo(BTOApplication application) {
        System.out.println(TextDecorationUtils.boldText("Application ID: " + application.getApplicationId()));
        System.out.println("Project: " + application.getProject().getProjectName());
        System.out.println("Flat Type: " + (application.getFlatType() != null ? application.getFlatType().getDisplayName() : "Not Selected"));
        System.out.println("Status: " + application.getStatus().getDisplayName());
        System.out.println("----------------------------------------");
    }
} 