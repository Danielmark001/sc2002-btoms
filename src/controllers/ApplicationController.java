// File: bto_management_system/controller/ApplicationController.java
package controller;

import models.entity.Applicant;
import models.entity.BTOApplication;
import models.entity.BTOProject;
import models.entity.HDBManager;
import models.entity.HDBOfficer;
import models.entity.User;
import models.enumeration.ApplicationStatus;
import models.enumeration.FlatType;
import models.manager.ApplicationManager;
import models.manager.UserManager;
import view.ApplicationView;

import java.util.List;

/**
 * Controller for handling application-related operations
 */
public class ApplicationController {
    private ApplicationView applicationView;
    private ApplicationManager applicationManager;
    private UserManager userManager;
    
    /**
     * Constructor for ApplicationController
     * 
     * @param applicationView View for application operations
     */
    public ApplicationController(ApplicationView applicationView) {
        this.applicationView = applicationView;
        this.applicationManager = ApplicationManager.getInstance();
        this.userManager = UserManager.getInstance();
    }
    
    /**
     * Applies for a BTO project
     * 
     * @param project Project to apply for
     * @return true if application succeeds
     */
    public boolean applyForProject(BTOProject project) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof Applicant)) {
            return false;
        }
        
        Applicant applicant = (Applicant) currentUser;
        
        // Check if already applied for a project
        if (applicant.getCurrentApplication() != null) {
            return false;
        }
        
        // Create the application
        BTOApplication application = applicationManager.createApplication(applicant, project);
        return application != null;
    }
    
    /**
     * Requests withdrawal of an application
     * 
     * @return true if request succeeds
     */
    public boolean requestWithdrawal() {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof Applicant)) {
            return false;
        }
        
        Applicant applicant = (Applicant) currentUser;
        return applicant.requestWithdrawal();
    }
    
    /**
     * Approves an application
     * 
     * @param application Application to approve
     * @return true if approval succeeds
     */
    public boolean approveApplication(BTOApplication application) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        return applicationManager.approveApplication(application, manager);
    }
    
    /**
     * Rejects an application
     * 
     * @param application Application to reject
     * @return true if rejection succeeds
     */
    public boolean rejectApplication(BTOApplication application) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        return applicationManager.rejectApplication(application, manager);
    }
    
    /**
     * Processes an application withdrawal request
     * 
     * @param application Application to withdraw
     * @param approve true to approve, false to reject
     * @return true if processing succeeds
     */
    public boolean processWithdrawal(BTOApplication application, boolean approve) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        return applicationManager.processWithdrawal(application, manager, approve);
    }
    
    /**
     * Books a flat for a successful application
     * 
     * @param application Application to book for
     * @param flatType Type of flat to book
     * @return true if booking succeeds
     */
    public boolean bookFlat(BTOApplication application, FlatType flatType) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            return false;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        return applicationManager.bookFlat(application, officer, flatType);
    }
    
    /**
     * Gets applications for a specific project
     * 
     * @param project Project to get applications for
     * @return List of applications
     */
    public List<BTOApplication> getApplicationsByProject(BTOProject project) {
        return applicationManager.getApplicationsByProject(project);
    }
    
    /**
     * Gets applications with a specific status for a project
     * 
     * @param project Project to get applications for
     * @param status Status to filter by
     * @return List of matching applications
     */
    public List<BTOApplication> getApplicationsByStatus(BTOProject project, ApplicationStatus status) {
        return applicationManager.getApplicationsByStatus(project, status);
    }
    
    /**
     * Gets the current user's application
     * 
     * @return Current application or null if none
     */
    public BTOApplication getCurrentApplication() {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof Applicant)) {
            return null;
        }
        
        Applicant applicant = (Applicant) currentUser;
        return applicant.getCurrentApplication();
    }
    
    /**
     * Retrieves an application by NRIC
     * 
     * @param nric NRIC to look up
     * @return Application if found, null otherwise
     */
    public BTOApplication getApplicationByNRIC(String nric) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            return null;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        if (officer.getHandlingProject() == null) {
            return null;
        }
        
        return officer.retrieveApplication(nric);
    }
    
    /**
     * Generates a receipt for a booked flat
     * 
     * @param application Application with booking details
     * @return Formatted receipt string
     */
    public String generateReceipt(BTOApplication application) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            return null;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        return officer.generateReceipt(application);
    }
}