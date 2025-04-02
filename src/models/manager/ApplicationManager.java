// File: bto_management_system/model/manager/ApplicationManager.java (continued)
package bto_management_system.model.manager;

import bto_management_system.model.entity.Applicant;
import bto_management_system.model.entity.BTOApplication;
import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.HDBOfficer;
import bto_management_system.model.enumeration.ApplicationStatus;
import bto_management_system.model.enumeration.FlatType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages BTO application-related operations
 */
public class ApplicationManager {
    private static ApplicationManager instance;
    
    private ApplicationManager() {
    }
    
    /**
     * Gets the singleton instance
     * 
     * @return ApplicationManager instance
     */
    public static ApplicationManager getInstance() {
        if (instance == null) {
            instance = new ApplicationManager();
        }
        return instance;
    }
    
    /**
     * Creates a new BTO application
     * 
     * @param applicant Applicant submitting the application
     * @param project Project being applied for
     * @return Created application if successful, null otherwise
     */
    public BTOApplication createApplication(Applicant applicant, BTOProject project) {
        // Check if applicant already has an application
        if (applicant.getCurrentApplication() != null) {
            return null;
        }
        
        // Check eligibility based on marital status and age
        boolean eligible = UserManager.getInstance().isEligibleForFlatType(
                applicant, 
                applicant.getMaritalStatus().name()
        );
        
        if (!eligible) {
            return null;
        }
        
        // Create new application
        BTOApplication application = new BTOApplication(applicant, project);
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        
        return application;
    }
    
    /**
     * Checks if a user has applied for a specific project
     * 
     * @param applicant Applicant to check
     * @param project Project to check
     * @return true if applicant has applied for the project
     */
    public boolean hasAppliedForProject(Applicant applicant, BTOProject project) {
        BTOApplication currentApp = applicant.getCurrentApplication();
        return currentApp != null && currentApp.getProject().equals(project);
    }
    
    /**
     * Processes a BTO application withdrawal request
     * 
     * @param application Application to withdraw
     * @param manager Manager processing the request
     * @param approve true to approve, false to reject
     * @return true if processing succeeds
     */
    public boolean processWithdrawal(BTOApplication application, HDBManager manager, boolean approve) {
        if (!application.getProject().getManager().equals(manager)) {
            return false;
        }
        
        if (approve) {
            if (application.getStatus() == ApplicationStatus.BOOKED) {
                // Return the flat to inventory
                FlatType bookedType = application.getApplicant().getBookedFlatType();
                if (bookedType != null) {
                    application.getProject().incrementUnitCount(bookedType);
                    application.getApplicant().setBookedFlatType(null);
                    application.getApplicant().setBookedProject(null);
                }
            }
            
            // Update application status
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            application.getApplicant().setCurrentApplication(null);
            
            // Save changes
            ProjectManager.getInstance().saveProjects();
            UserManager.getInstance().saveUsers();
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Approves a BTO application
     * 
     * @param application Application to approve
     * @param manager Manager approving the application
     * @return true if approval succeeds
     */
    public boolean approveApplication(BTOApplication application, HDBManager manager) {
        if (!application.getProject().getManager().equals(manager)) {
            return false;
        }
        
        application.setStatus(ApplicationStatus.SUCCESSFUL);
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        
        return true;
    }
    
    /**
     * Rejects a BTO application
     * 
     * @param application Application to reject
     * @param manager Manager rejecting the application
     * @return true if rejection succeeds
     */
    public boolean rejectApplication(BTOApplication application, HDBManager manager) {
        if (!application.getProject().getManager().equals(manager)) {
            return false;
        }
        
        application.setStatus(ApplicationStatus.UNSUCCESSFUL);
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        
        return true;
    }
    
    /**
     * Books a flat for a successful application
     * 
     * @param application Application to book for
     * @param officer Officer processing the booking
     * @param flatType Type of flat to book
     * @return true if booking succeeds
     */
    public boolean bookFlat(BTOApplication application, HDBOfficer officer, FlatType flatType) {
        if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return false;
        }
        
        if (!application.getProject().equals(officer.getHandlingProject())) {
            return false;
        }
        
        // Check if the flat type is available
        if (!application.getProject().hasFlatType(flatType)) {
            return false;
        }
        
        // Update application status
        application.setStatus(ApplicationStatus.BOOKED);
        
        // Update applicant's profile
        Applicant applicant = application.getApplicant();
        applicant.setBookedFlatType(flatType);
        applicant.setBookedProject(application.getProject());
        
        // Decrement available units
        application.getProject().decrementUnitCount(flatType);
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        UserManager.getInstance().saveUsers();
        
        return true;
    }
    
    /**
     * Gets all applications for a specific project
     * 
     * @param project Project to get applications for
     * @return List of applications
     */
    public List<BTOApplication> getApplicationsByProject(BTOProject project) {
        return project.getApplications();
    }
    
    /**
     * Gets applications with a specific status for a project
     * 
     * @param project Project to get applications for
     * @param status Status to filter by
     * @return List of matching applications
     */
    public List<BTOApplication> getApplicationsByStatus(BTOProject project, ApplicationStatus status) {
        return project.getApplications().stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }
}




