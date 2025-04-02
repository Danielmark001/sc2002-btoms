// File: bto_management_system/controller/ProjectController.java (continued)
package bto_management_system.controller;

import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.User;
import bto_management_system.model.enumeration.FlatType;
import bto_management_system.model.manager.ProjectManager;
import bto_management_system.model.manager.UserManager;
import bto_management_system.view.ProjectView;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling project-related operations
 */
public class ProjectController {
    private ProjectView projectView;
    private ProjectManager projectManager;
    private UserManager userManager;
    
    /**
     * Constructor for ProjectController
     * 
     * @param projectView View for project operations
     */
    public ProjectController(ProjectView projectView) {
        this.projectView = projectView;
        this.projectManager = ProjectManager.getInstance();
        this.userManager = UserManager.getInstance();
    }
    
    /**
     * Creates a new BTO project
     * 
     * @param projectName Name of the project
     * @param neighborhood Neighborhood of the project
     * @param twoRoomCount Number of 2-room flats
     * @param threeRoomCount Number of 3-room flats
     * @param openingDate Application opening date
     * @param closingDate Application closing date
     * @param officerSlots Number of available officer slots
     * @return true if creation succeeds
     */
    public boolean createProject(String projectName, String neighborhood, 
            int twoRoomCount, int threeRoomCount, 
            LocalDate openingDate, LocalDate closingDate, int officerSlots) {
        
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        Map<String, Integer> unitCounts = new HashMap<>();
        unitCounts.put(FlatType.TWO_ROOM.name(), twoRoomCount);
        unitCounts.put(FlatType.THREE_ROOM.name(), threeRoomCount);
        
        BTOProject project = projectManager.createProject(
                (HDBManager) currentUser, 
                projectName, 
                neighborhood, 
                unitCounts, 
                openingDate, 
                closingDate, 
                officerSlots
        );
        
        return project != null;
    }
    
    /**
     * Edits an existing BTO project
     * 
     * @param project Project to edit
     * @param projectName New project name
     * @param neighborhood New neighborhood
     * @param twoRoomCount New number of 2-room flats
     * @param threeRoomCount New number of 3-room flats
     * @param openingDate New application opening date
     * @param closingDate New application closing date
     * @param officerSlots New number of available officer slots
     * @return true if edit succeeds
     */
    public boolean editProject(BTOProject project, String projectName, String neighborhood, 
            int twoRoomCount, int threeRoomCount, 
            LocalDate openingDate, LocalDate closingDate, int officerSlots) {
        
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        if (!project.getManager().equals(manager)) {
            return false;
        }
        
        Map<FlatType, Integer> unitCounts = new HashMap<>();
        unitCounts.put(FlatType.TWO_ROOM, twoRoomCount);
        unitCounts.put(FlatType.THREE_ROOM, threeRoomCount);
        
        return manager.editProject(
                project, 
                projectName, 
                neighborhood, 
                unitCounts, 
                openingDate, 
                closingDate, 
                officerSlots
        );
    }
    
    /**
     * Deletes a BTO project
     * 
     * @param project Project to delete
     * @return true if deletion succeeds
     */
    public boolean deleteProject(BTOProject project) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        if (!project.getManager().equals(manager)) {
            return false;
        }
        
        return manager.deleteProject(project);
    }
    
    /**
     * Toggles a project's visibility
     * 
     * @param project Project to toggle
     * @param visible New visibility status
     * @return true if toggle succeeds
     */
    public boolean toggleProjectVisibility(BTOProject project, boolean visible) {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        if (!project.getManager().equals(manager)) {
            return false;
        }
        
        manager.toggleProjectVisibility(project, visible);
        projectManager.saveProjects();
        return true;
    }
    
    /**
     * Gets all BTO projects
     * 
     * @return List of all projects
     */
    public List<BTOProject> getAllProjects() {
        return projectManager.getAllProjects();
    }
    
    /**
     * Gets projects visible to the current user
     * 
     * @return List of visible projects
     */
    public List<BTOProject> getVisibleProjects() {
        User currentUser = userManager.getCurrentUser();
        return projectManager.getVisibleProjects(currentUser);
    }
    
    /**
     * Gets projects created by the current manager
     * 
     * @return List of projects created by the current manager
     */
    public List<BTOProject> getProjectsByCurrentManager() {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return List.of();
        }
        
        return projectManager.getProjectsByManager((HDBManager) currentUser);
    }
    
    /**
     * Gets a project by name
     * 
     * @param projectName Name of the project
     * @return Project if found, null otherwise
     */
    public BTOProject getProjectByName(String projectName) {
        return projectManager.getProjectByName(projectName);
    }
}

// File: bto_management_system/controller/ApplicationController.java
package bto_management_system.controller;

import bto_management_system.model.entity.Applicant;
import bto_management_system.model.entity.BTOApplication;
import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.HDBOfficer;
import bto_management_system.model.entity.User;
import bto_management_system.model.enumeration.ApplicationStatus;
import bto_management_system.model.enumeration.FlatType;
import bto_management_system.model.manager.ApplicationManager;
import bto_management_system.model.manager.UserManager;
import bto_management_system.view.ApplicationView;

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

// File: bto_management_system/controller/OfficerRegistrationController.java
package bto_management_system.controller;

import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.HDBOfficer;
import bto_management_system.model.entity.OfficerRegistration;
import bto_management_system.model.entity.User;
import bto_management_system.model.enumeration.ApplicationStatus;
import bto_management_system.model.manager.RegistrationManager;
import bto_management_system.model.manager.UserManager;
import bto_management_system.view.RegistrationView;

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

// File: bto_management_system/controller/EnquiryController.java
package bto_management_system.controller;

import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.Enquiry;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.User;
import bto_management_system.model.manager.EnquiryManager;
import bto_management_system.model.manager.UserManager;
import bto_management_system.view.EnquiryView;

import java.util.List;

/**
 * Controller for handling enquiry-related operations
 */
public class EnquiryController {
    private EnquiryView enquiryView;
    private EnquiryManager enquiryManager;
    private UserManager userManager;
    
    /**
     * Constructor for EnquiryController
     * 
     * @param enquiryView View for enquiry operations
     */
    public EnquiryController(EnquiryView enquiryView) {
        this.enquiryView = enquiryView;
        this.enquiryManager = EnquiryManager.getInstance();
        this.userManager = UserManager.getInstance();
    }
    
    /**
     * Creates a new enquiry
     * 
     * @param project Project the enquiry is about
     * @param content Content of the enquiry
     * @return true if creation succeeds
     */
    public boolean createEnquiry(BTOProject project, String content) {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        Enquiry enquiry = enquiryManager.createEnquiry(currentUser, project, content);
        return enquiry != null;
    }
    
    /**
     * Edits an existing enquiry
     * 
     * @param enquiry Enquiry to edit
     * @param newContent New content for the enquiry
     * @return true if edit succeeds
     */
    public boolean editEnquiry(Enquiry enquiry, String newContent) {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return enquiryManager.editEnquiry(enquiry, currentUser, newContent);
    }
    
    /**
     * Deletes an enquiry
     * 
     * @param enquiry Enquiry to delete
     * @return true if deletion succeeds
     */
    public boolean deleteEnquiry(Enquiry enquiry) {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return enquiryManager.deleteEnquiry(enquiry, currentUser);
    }
    
    /**
     * Replies to an enquiry
     * 
     * @param enquiry Enquiry to reply to
     * @param replyContent Content of the reply
     * @return true if reply succeeds
     */
    public boolean replyToEnquiry(Enquiry enquiry, String replyContent) {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return enquiryManager.replyToEnquiry(enquiry, currentUser, replyContent);
    }
    
    /**
     * Gets enquiries for a specific project
     * 
     * @param project Project to get enquiries for
     * @return List of enquiries
     */
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        return enquiryManager.getEnquiriesByProject(project);
    }
    
    /**
     * Gets enquiries created by the current user
     * 
     * @return List of enquiries
     */
    public List<Enquiry> getCurrentUserEnquiries() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        
        return enquiryManager.getEnquiriesByUser(currentUser);
    }
    
    /**
     * Gets all enquiries (for HDB Manager)
     * 
     * @return List of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return List.of();
        }
        
        return enquiryManager.getAllEnquiries();
    }
    
    /**
     * Gets all unanswered enquiries
     * 
     * @return List of unanswered enquiries
     */
    public List<Enquiry> getUnansweredEnquiries() {
        return enquiryManager.getUnansweredEnquiries();
    }
}

// File: bto_management_system/controller/UserController.java
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