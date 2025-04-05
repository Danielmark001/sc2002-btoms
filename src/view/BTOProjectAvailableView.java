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

public class BTOProjectAvailableView implements IProjectView {
    private BTOProjectService projectService;

    public BTOProjectAvailableView() {
        this.projectService = new BTOProjectService();
    }

    @Override
    public void displayProjectInfo(BTOProject project) {
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Opening Date: " + project.getApplicationOpeningDate());
        System.out.println("Application Closing Date: " + project.getApplicationClosingDate());
        
        // Get current user
        User user = AuthStore.getCurrentUser();
        
        // Get eligible flat types for this user
        Map<FlatType, FlatTypeDetails> eligibleFlatTypes;
        if (user instanceof Applicant) {
            eligibleFlatTypes = projectService.getEligibleFlatTypes(project, (Applicant) user);
        } else {
            // Default to empty map for other user types
            eligibleFlatTypes = project.getFlatTypes();
        }
        
        System.out.println("\nAvailable Flat Types:");
        for (Map.Entry<FlatType, FlatTypeDetails> entry : eligibleFlatTypes.entrySet()) {
            FlatType flatType = entry.getKey();
            FlatTypeDetails details = entry.getValue();
            
            System.out.println(flatType.getDisplayName() + ":");
            System.out.println("  - Available Units: " + details.getUnits());
            System.out.println("  - Price: $" + details.getPrice());
        }
            
        System.out.println("\nProject Manager: " + project.getHDBManager().getName());
    }
}