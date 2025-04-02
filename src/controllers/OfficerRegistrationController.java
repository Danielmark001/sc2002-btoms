// File: bto_management_system/controller/OfficerRegistrationController.java
package controller;

import models.entity.BTOProject;
import models.entity.HDBManager;
import models.entity.HDBOfficer;
import models.entity.OfficerRegistration;
import models.entity.User;
import models.enumeration.ApplicationStatus;
import models.manager.RegistrationManager;
import models.manager.UserManager;
import view.RegistrationView;

import java.util.List;

/**
 * Controller for handling officer registration-related operations
 */
public class OfficerRegistrationController {
    private RegistrationView registrationView;
    private RegistrationManager registrationManager;
    private UserManager userManager;
    
    /**
     * Constructor for OfficerRegistrationController
     * 
     * @param registrationView View for registration operations
     */
    public OfficerRegistrationController(RegistrationView registrationView) {
        this.registrationView = registrationView;
        this.registrationManager = RegistrationManager.getInstance();
        this.userManager = UserManager.getInstance();
    }
    
    /**
     * Registers to handle a project
     * 
     * @param project Project to register for
     * @return true if registration succeeds
     */
    public boolean registerForProject(BTOProject project) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            return false;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        OfficerRegistration registration = registrationManager.createRegistration(officer, project);
        return registration != null;
    }
    
    /**
     * Approves an officer registration
     * 
     * @param registration Registration to approve
     * @return true if approval succeeds
     */
    public boolean approveRegistration(OfficerRegistration registration) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        return registrationManager.processRegistration(registration, manager, true);
    }
    
    /**
     * Rejects an officer registration
     * 
     * @param registration Registration to reject
     * @return true if rejection succeeds
     */
    public boolean rejectRegistration(OfficerRegistration registration) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        return registrationManager.processRegistration(registration, manager, false);
    }
    
    /**
     * Gets registrations for a specific project
     * 
     * @param project Project to get registrations for
     * @return List of registrations
     */
    public List<OfficerRegistration> getRegistrationsByProject(BTOProject project) {
        return registrationManager.getRegistrationsByProject(project);
    }
    
    /**
     * Gets registrations with a specific status for a project
     * 
     * @param project Project to get registrations for
     * @param status Status to filter by
     * @return List of matching registrations
     */
    public List<OfficerRegistration> getRegistrationsByStatus(BTOProject project, ApplicationStatus status) {
        return registrationManager.getRegistrationsByStatus(project, status);
    }
    
    /**
     * Gets the current user's registration status
     * 
     * @return Current registration status
     */
    public ApplicationStatus getCurrentRegistrationStatus() {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            return null;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        return officer.getRegistrationStatus();
    }
    
    /**
     * Gets the project the current officer is handling
     * 
     * @return Project being handled
     */
    public BTOProject getHandlingProject() {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            return null;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        return officer.getHandlingProject();
    }
}