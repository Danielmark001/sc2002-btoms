package view;

import models.BTOProject;
import models.FlatTypeDetails;
import enumeration.FlatType;
import java.util.Map;

/**
 * View class for displaying BTO project information in the console interface.
 * 
 * This class is responsible for rendering BTO project data to users through the
 * console. It provides methods for displaying project details, project lists,
 * and various feedback messages related to project management operations.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class BTOProjectView {
    
    /**
     * Displays detailed information about a specific BTO project.
     * 
     * @param project The BTO project to display
     */
    public void displayProjectDetails(BTOProject project) {
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Opening Date: " + project.getApplicationOpeningDate());
        System.out.println("Application Closing Date: " + project.getApplicationClosingDate());
        System.out.println("HDB Manager: " + project.getHDBManager().getName() + " (" + project.getHDBManager().getNric() + ")");
        System.out.println("HDB Officer Slots: " + project.getHDBOfficerSlots());
        System.out.println("HDB Officers: " + project.getHDBOfficers().size());
        System.out.println("Visible: " + (project.isVisible() ? "Yes" : "No"));
        
        System.out.println("Flat Types:");
        for (Map.Entry<FlatType, FlatTypeDetails> entry : project.getFlatTypes().entrySet()) {
            System.out.println("  " + entry.getKey().getDisplayName() + ": " + entry.getValue().getUnits() + " units, $" + entry.getValue().getPrice());
        }
    }

    /**
     * Displays a header for the BTO projects list view.
     */
    public void displayProjectListHeader() {
        System.out.println("\n===== BTO Projects =====");
    }

    /**
     * Displays a single project item in a list format.
     * 
     * @param index The position of the project in the list (for selection purposes)
     * @param project The BTO project to display
     */
    public void displayProjectListItem(int index, BTOProject project) {
        System.out.println((index + 1) + ". " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
    }

    /**
     * Displays a message indicating that no projects were found.
     */
    public void displayNoProjectsMessage() {
        System.out.println("No projects found.");
    }

    /**
     * Displays a header for the project creation view.
     */
    public void displayProjectCreationHeader() {
        System.out.println("\n===== Create BTO Project =====");
    }

    /**
     * Displays a header for the project edit view.
     */
    public void displayProjectEditHeader() {
        System.out.println("\n===== Edit BTO Project =====");
    }

    /**
     * Displays a header for the project deletion view.
     */
    public void displayProjectDeleteHeader() {
        System.out.println("\n===== Delete BTO Project =====");
    }

    /**
     * Displays a header for the project visibility toggle view.
     */
    public void displayProjectVisibilityHeader() {
        System.out.println("\n===== Toggle Project Visibility =====");
    }

    /**
     * Displays a success message after project update.
     */
    public void displayProjectUpdateSuccess() {
        System.out.println("Project updated successfully!");
    }

    /**
     * Displays a success message after project creation.
     */
    public void displayProjectCreationSuccess() {
        System.out.println("BTO Project created successfully!");
    }

    /**
     * Displays a success message after project deletion.
     */
    public void displayProjectDeletionSuccess() {
        System.out.println("Project deleted successfully!");
    }

    /**
     * Displays a message indicating that project deletion was cancelled.
     */
    public void displayProjectDeletionCancelled() {
        System.out.println("Deletion cancelled.");
    }

    /**
     * Displays a success message after project visibility update.
     */
    public void displayProjectVisibilityUpdateSuccess() {
        System.out.println("Project visibility updated successfully!");
    }

    /**
     * Displays a warning about already handling an active project.
     */
    public void displayActiveProjectWarning() {
        System.out.println("You are already handling a project within an application period.");
    }

    /**
     * Displays a warning about modifying a project within its application period.
     */
    public void displayApplicationPeriodWarning() {
        System.out.println("Cannot edit/delete a project that is currently in application period.");
    }
} 