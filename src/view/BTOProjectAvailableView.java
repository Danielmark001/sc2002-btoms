package view;

import interfaces.IProjectView;
import models.BTOProject;
import models.Applicant;
import models.FlatTypeDetails;
import models.User;
import enumeration.FlatType;
import stores.AuthStore;
import services.BTOProjectService;
import java.util.Map;

/**
 * View class for managing BTO Projects
 */
public class BTOProjectAvailableView implements IProjectView {
    private BTOProjectService projectService;

    public BTOProjectAvailableView() {
        this.projectService = new BTOProjectService();
    }

    /**
     * Checks if an applicant has already applied for a project
     * 
     * @param applicant The applicant to check
     * @param project The project to check against
     * @return true if the applicant has already applied for the project, false otherwise
     */
    private boolean hasAppliedForProject(Applicant applicant, BTOProject project) {
        return projectService.getApplicationsByApplicant(applicant).stream()
            .anyMatch(app -> app.getProject().equals(project));
    }

    @Override
    public void displayProjectInfo(BTOProject project) {
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Opening Date: " + project.getApplicationOpeningDate());
        System.out.println("Application Closing Date: " + project.getApplicationClosingDate());
        
        // Get current user
        User user = AuthStore.getCurrentUser();
        
        // If user is an applicant who has already applied for this project,
        // show limited information
        if (user instanceof Applicant) {
            Applicant applicant = (Applicant) user;
            if (hasAppliedForProject(applicant, project)) {
                System.out.println("\nYou have already applied for this project.");
                System.out.println("Project Manager: " + project.getHDBManager().getName());
                return; // Exit early without showing flat type details
            }
        }
        
        // Get eligible flat types for this user
        Map<FlatType, FlatTypeDetails> eligibleFlatTypes;
        if (user instanceof Applicant) {
            eligibleFlatTypes = projectService.getEligibleFlatTypes(project, (Applicant) user);
        } else {
            // Default to all flat types for other user types
            eligibleFlatTypes = project.getFlatTypes();
        }
        
        System.out.println("\nAvailable Flat Types:");
        boolean hasAvailableUnits = false;
        
        for (Map.Entry<FlatType, FlatTypeDetails> entry : eligibleFlatTypes.entrySet()) {
            FlatType flatType = entry.getKey();
            FlatTypeDetails details = entry.getValue();
            
            // Show all flat types but indicate if units are available
            System.out.println(flatType.getDisplayName() + ":");
            System.out.println("  - Available Units: " + details.getUnits());
            System.out.println("  - Price: $" + details.getPrice());
            
            if (details.getUnits() > 0) {
                hasAvailableUnits = true;
            }
        }
        
        if (eligibleFlatTypes.isEmpty()) {
            System.out.println("  No eligible flat types for your profile.");
        } else if (!hasAvailableUnits) {
            System.out.println("  No units currently available for eligible flat types.");
        }
            
        System.out.println("\nProject Manager: " + project.getHDBManager().getName());
    }
}