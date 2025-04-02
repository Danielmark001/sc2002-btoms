package bto_management_system.model.manager;

import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.HDBOfficer;
import bto_management_system.model.entity.OfficerRegistration;
import bto_management_system.model.enumeration.ApplicationStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages HDB Officer registration-related operations
 */
public class RegistrationManager {
    private static RegistrationManager instance;
    
    private RegistrationManager() {
    }
    
    /**
     * Gets the singleton instance
     * 
     * @return RegistrationManager instance
     */
    public static RegistrationManager getInstance() {
        if (instance == null) {
            instance = new RegistrationManager();
        }
        return instance;
    }
    
    /**
     * Creates a new officer registration
     * 
     * @param officer Officer submitting the registration
     * @param project Project to register for
     * @return Created registration if successful, null otherwise
     */
    public OfficerRegistration createRegistration(HDBOfficer officer, BTOProject project) {
        // Check if already handling another project in the same period
        if (isHandlingAnyProjectInPeriod(officer, project.getOpeningDate(), project.getClosingDate())) {
            return null;
        }
        
        // Check if officer has applied for this project as an applicant
        if (officer instanceof HDBOfficer) {
            ApplicationManager appManager = ApplicationManager.getInstance();
            if (appManager.hasAppliedForProject((HDBOfficer) officer, project)) {
                return null;
            }
        }
        
        // Check if there are available officer slots
        if (project.getAvailableOfficerSlots() <= 0) {
            return null;
        }
        
        // Create the registration
        OfficerRegistration registration = new OfficerRegistration(officer, project);
        officer.setRegistrationStatus(ApplicationStatus.PENDING);
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        UserManager.getInstance().saveUsers();
        
        return registration;
    }
    
    /**
     * Checks if an officer is handling any project in a specific period
     * 
     * @param officer Officer to check
     * @param startDate Start of the period
     * @param endDate End of the period
     * @return true if officer is handling a project in the period
     */
    public boolean isHandlingAnyProjectInPeriod(HDBOfficer officer, LocalDate startDate, LocalDate endDate) {
        BTOProject handlingProject = officer.getHandlingProject();
        
        if (handlingProject != null) {
            return handlingProject.isOverlapping(startDate, endDate);
        }
        
        return false;
    }
    
    /**
     * Processes an officer registration
     * 
     * @param registration Registration to process
     * @param manager Manager processing the registration
     * @param approve true to approve, false to reject
     * @return true if processing succeeds
     */
    public boolean processRegistration(OfficerRegistration registration, HDBManager manager, boolean approve) {
        if (!registration.getProject().getManager().equals(manager)) {
            return false;
        }
        
        if (approve) {
            // Check if there are still available slots
            if (registration.getProject().getAvailableOfficerSlots() <= 0) {
                return false;
            }
            
            // Update registration status
            registration.setStatus(ApplicationStatus.SUCCESSFUL);
            
            // Update officer's handling project and status
            registration.getOfficer().setHandlingProject(registration.getProject());
            registration.getOfficer().setRegistrationStatus(ApplicationStatus.SUCCESSFUL);
            
            // Decrement available officer slots
            registration.getProject().decrementOfficerSlots();
        } else {
            // Reject the registration
            registration.setStatus(ApplicationStatus.UNSUCCESSFUL);
            registration.getOfficer().setRegistrationStatus(ApplicationStatus.UNSUCCESSFUL);
        }
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        UserManager.getInstance().saveUsers();
        
        return true;
    }
    
    /**
     * Gets all registrations for a specific project
     * 
     * @param project Project to get registrations for
     * @return List of registrations
     */
    public List<OfficerRegistration> getRegistrationsByProject(BTOProject project) {
        return project.getOfficerRegistrations();
    }
    
    /**
     * Gets registrations with a specific status for a project
     * 
     * @param project Project to get registrations for
     * @param status Status to filter by
     * @return List of matching registrations
     */
    public List<OfficerRegistration> getRegistrationsByStatus(BTOProject project, ApplicationStatus status) {
        return project.getOfficerRegistrations().stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets registration for a specific officer
     * 
     * @param officer Officer to get registration for
     * @return Registration if found, null otherwise
     */
    public OfficerRegistration getRegistrationByOfficer(HDBOfficer officer) {
        if (officer.getHandlingProject() != null) {
            return officer.getHandlingProject().getOfficerRegistrations().stream()
                    .filter(r -> r.getOfficer().equals(officer))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}