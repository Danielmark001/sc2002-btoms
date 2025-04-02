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

// File: bto_management_system/model/manager/RegistrationManager.java
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

// File: bto_management_system/model/manager/EnquiryManager.java
package bto_management_system.model.manager;

import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.Enquiry;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.HDBOfficer;
import bto_management_system.model.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages enquiry-related operations
 */
public class EnquiryManager {
    private static EnquiryManager instance;
    
    private EnquiryManager() {
    }
    
    /**
     * Gets the singleton instance
     * 
     * @return EnquiryManager instance
     */
    public static EnquiryManager getInstance() {
        if (instance == null) {
            instance = new EnquiryManager();
        }
        return instance;
    }
    
    /**
     * Creates a new enquiry
     * 
     * @param creator User creating the enquiry
     * @param project Project the enquiry is about
     * @param content Content of the enquiry
     * @return Created enquiry if successful, null otherwise
     */
    public Enquiry createEnquiry(User creator, BTOProject project, String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        
        Enquiry enquiry = creator.createEnquiry(content, project);
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        UserManager.getInstance().saveUsers();
        
        return enquiry;
    }
    
    /**
     * Edits an existing enquiry
     * 
     * @param enquiry Enquiry to edit
     * @param user User editing the enquiry
     * @param newContent New content for the enquiry
     * @return true if edit succeeds
     */
    public boolean editEnquiry(Enquiry enquiry, User user, String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            return false;
        }
        
        if (!enquiry.getCreator().equals(user)) {
            return false;
        }
        
        boolean result = user.editEnquiry(enquiry, newContent);
        
        if (result) {
            // Save changes
            ProjectManager.getInstance().saveProjects();
            UserManager.getInstance().saveUsers();
        }
        
        return result;
    }
    
    /**
     * Deletes an enquiry
     * 
     * @param enquiry Enquiry to delete
     * @param user User deleting the enquiry
     * @return true if deletion succeeds
     */
    public boolean deleteEnquiry(Enquiry enquiry, User user) {
        if (!enquiry.getCreator().equals(user)) {
            return false;
        }
        
        boolean result = user.deleteEnquiry(enquiry);
        
        if (result) {
            // Save changes
            ProjectManager.getInstance().saveProjects();
            UserManager.getInstance().saveUsers();
        }
        
        return result;
    }
    
    /**
     * Replies to an enquiry
     * 
     * @param enquiry Enquiry to reply to
     * @param replier User replying to the enquiry
     * @param replyContent Content of the reply
     * @return true if reply succeeds
     */
    public boolean replyToEnquiry(Enquiry enquiry, User replier, String replyContent) {
        if (replyContent == null || replyContent.trim().isEmpty()) {
            return false;
        }
        
        if (!(replier instanceof HDBOfficer || replier instanceof HDBManager)) {
            return false;
        }
        
        // Check if officer is handling the project
        if (replier instanceof HDBOfficer && 
                !((HDBOfficer) replier).getHandlingProject().equals(enquiry.getProject())) {
            return false;
        }
        
        // Add the reply
        enquiry.setReply(replyContent, replier);
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        
        return true;
    }
    
    /**
     * Gets all enquiries for a specific project
     * 
     * @param project Project to get enquiries for
     * @return List of enquiries
     */
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        return project.getEnquiries();
    }
    
    /**
     * Gets all enquiries created by a specific user
     * 
     * @param user User to get enquiries for
     * @return List of enquiries
     */
    public List<Enquiry> getEnquiriesByUser(User user) {
        return user.getEnquiries();
    }
    
    /**
     * Gets all enquiries for all projects
     * 
     * @return List of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        List<Enquiry> allEnquiries = new ArrayList<>();
        
        for (BTOProject project : ProjectManager.getInstance().getAllProjects()) {
            allEnquiries.addAll(project.getEnquiries());
        }
        
        return allEnquiries;
    }
    
    /**
     * Gets all unanswered enquiries
     * 
     * @return List of unanswered enquiries
     */
    public List<Enquiry> getUnansweredEnquiries() {
        return getAllEnquiries().stream()
                .filter(e -> e.getReply() == null)
                .collect(Collectors.toList());
    }
}