package view;

import models.BTOProject;
import models.FlatTypeDetails;
import enumeration.FlatType;
import java.util.Map;

public class BTOProjectView {
    
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

    public void displayProjectListHeader() {
        System.out.println("\n===== BTO Projects =====");
    }

    public void displayProjectListItem(int index, BTOProject project) {
        System.out.println((index + 1) + ". " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
    }

    public void displayNoProjectsMessage() {
        System.out.println("No projects found.");
    }

    public void displayProjectCreationHeader() {
        System.out.println("\n===== Create BTO Project =====");
    }

    public void displayProjectEditHeader() {
        System.out.println("\n===== Edit BTO Project =====");
    }

    public void displayProjectDeleteHeader() {
        System.out.println("\n===== Delete BTO Project =====");
    }

    public void displayProjectVisibilityHeader() {
        System.out.println("\n===== Toggle Project Visibility =====");
    }

    public void displayProjectUpdateSuccess() {
        System.out.println("Project updated successfully!");
    }

    public void displayProjectCreationSuccess() {
        System.out.println("BTO Project created successfully!");
    }

    public void displayProjectDeletionSuccess() {
        System.out.println("Project deleted successfully!");
    }

    public void displayProjectDeletionCancelled() {
        System.out.println("Deletion cancelled.");
    }

    public void displayProjectVisibilityUpdateSuccess() {
        System.out.println("Project visibility updated successfully!");
    }

    public void displayActiveProjectWarning() {
        System.out.println("You are already handling a project within an application period.");
    }

    public void displayApplicationPeriodWarning() {
        System.out.println("Cannot edit/delete a project that is currently in application period.");
    }
} 