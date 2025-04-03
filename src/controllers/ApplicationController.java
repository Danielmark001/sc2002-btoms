package controllers;

import models.*;
import enumeration.*;
import services.*;
import view.ApplicationView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import services.BTOApplicationService;

/**
 * Controller for handling application-related operations
 */
public class ApplicationController {
    private ApplicationView applicationView;
    private BTOApplicationService applicationService;
    private UserService userService;
    private ProjectService projectService;
    
    /**
     * Constructor for ApplicationController
     * 
     * @param applicationView View for application operations
     */
    public ApplicationController(ApplicationView applicationView) {
        this.applicationView = applicationView;
        this.applicationService = ApplicationService.getInstance();
        this.userService = UserService.getInstance();
        this.projectService = ProjectService.getInstance();
    }
    
    /**
     * Default constructor
     */
    public ApplicationController() {
        this.applicationService = ApplicationService.getInstance();
        this.userService = UserService.getInstance();
        this.projectService = ProjectService.getInstance();
    }
    
    /**
     * Applies for a BTO project
     * 
     * @param project Project to apply for  
     * @return true if application succeeds
     */
    public boolean applyForProject(BTOProject project) {
        try {
            // Validate project
            if (project == null) {
                if (applicationView != null) {
                    applicationView.displayError("Project cannot be null");
                }
                return false;
            }
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (applicationView != null) {
                    applicationView.displayError("You must be logged in to apply for a project");
                }
                return false;
            }
            
            // Check if user is an applicant
            if (!(currentUser instanceof Applicant)) {
                if (applicationView != null) {
                    applicationView.displayError("Only applicants can apply for projects");
                }
                return false;
            }
            
            Applicant applicant = (Applicant) currentUser;
            
            // Check if already applied for a project
            if (applicant.hasActiveApplication()) {
                if (applicationView != null) {
                    applicationView.displayError("You already have an active application");
                }
                return false;
            }
            
            // Validate eligibility
            if (!project.isEligibleForApplicant(applicant)) {
                if (applicationView != null) {
                    applicationView.displayError("You are not eligible for this project");
                }
                return false;
            }
            
            // Check if project is open for applications
            if (!project.isOpenForApplications()) {
                if (applicationView != null) {
                    applicationView.displayError("This project is not open for applications");
                }
                return false;
            }
            
            // Create the application
            BTOApplication application = applicationService.createApplication(applicant, project);
            
            if (applicationView != null) {
                applicationView.displaySuccess("Application submitted successfully");
            }
            
            return true;
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error applying for project: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Requests withdrawal of an application
     * 
     * @param application Application to withdraw
     * @return true if request succeeds
     */
    public boolean requestWithdrawal(BTOApplication application) {
        try {
            // Validate application
            if (application == null) {
                if (applicationView != null) {
                    applicationView.displayError("Application cannot be null");
                }
                return false;
            }
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (applicationView != null) {
                    applicationView.displayError("You must be logged in to request withdrawal");
                }
                return false;
            }
            
            // Check if user is the applicant
            if (!application.getApplicant().getNric().equals(currentUser.getNric())) {
                if (applicationView != null) {
                    applicationView.displayError("You can only withdraw your own applications");
                }
                return false;
            }
            
            // Check if application can be withdrawn
            if (application.getStatus() != ApplicationStatus.PENDING && 
                application.getStatus() != ApplicationStatus.SUCCESSFUL) {
                if (applicationView != null) {
                    applicationView.displayError("This application cannot be withdrawn");
                }
                return false;
            }
            
            // Request withdrawal
            application.requestWithdrawal();
            
            // Save the application
            boolean success = applicationService.updateApplication(application);
            
            if (success) {
                if (applicationView != null) {
                    applicationView.displaySuccess("Withdrawal request submitted successfully");
                }
            } else {
                if (applicationView != null) {
                    applicationView.displayError("Failed to submit withdrawal request");
                }
            }
            
            return success;
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error requesting withdrawal: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Processes an application booking
     * 
     * @param application Application to process
     * @return true if processing succeeds
     */
    public boolean processBooking(BTOApplication application) {
        try {
            // Validate application
            if (application == null) {
                if (applicationView != null) {
                    applicationView.displayError("Application cannot be null");
                }
                return false;
            }
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (applicationView != null) {
                    applicationView.displayError("You must be logged in to process bookings");
                }
                return false;
            }
            
            // Check if user is an HDB Officer
            if (!(currentUser instanceof HDBOfficer)) {
                if (applicationView != null) {
                    applicationView.displayError("Only HDB Officers can process bookings");
                }
                return false;
            }
            
            HDBOfficer officer = (HDBOfficer) currentUser;
            
            // Check if officer is handling this project
            if (officer.getHandlingProject() == null || 
                !officer.getHandlingProject().equals(application.getProject())) {
                if (applicationView != null) {
                    applicationView.displayError("You can only process bookings for projects you are handling");
                }
                return false;
            }
            
            // Process booking
            boolean success = applicationService.bookFlat(
                application, officer, application.getFlatType());
            
            if (success) {
                if (applicationView != null) {
                    applicationView.displaySuccess("Booking processed successfully");
                }
            } else {
                if (applicationView != null) {
                    applicationView.displayError("Failed to process booking");
                }
            }
            
            return success;
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error processing booking: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Approves an application
     * 
     * @param application Application to approve
     * @return true if approval succeeds
     */
    public boolean approveApplication(BTOApplication application) {
        try {
            // Validate application
            if (application == null) {
                if (applicationView != null) {
                    applicationView.displayError("Application cannot be null");
                }
                return false;
            }
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (applicationView != null) {
                    applicationView.displayError("You must be logged in to approve applications");
                }
                return false;
            }
            
            // Check if user is an HDB Manager
            if (!(currentUser instanceof HDBManager)) {
                if (applicationView != null) {
                    applicationView.displayError("Only HDB Managers can approve applications");
                }
                return false;
            }
            
            HDBManager manager = (HDBManager) currentUser;
            
            // Check if manager is managing this project
            if (application.getProject().getHdbManager() == null || 
                !application.getProject().getHdbManager().equals(manager)) {
                if (applicationView != null) {
                    applicationView.displayError("You can only approve applications for projects you manage");
                }
                return false;
            }
            
            // Approve application
            boolean success = applicationService.approveApplication(application, manager);
            
            if (success) {
                if (applicationView != null) {
                    applicationView.displaySuccess("Application approved successfully");
                }
            } else {
                if (applicationView != null) {
                    applicationView.displayError("Failed to approve application");
                }
            }
            
            return success;
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error approving application: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Rejects an application
     * 
     * @param application Application to reject
     * @return true if rejection succeeds
     */
    public boolean rejectApplication(BTOApplication application) {
        try {
            // Validate application
            if (application == null) {
                if (applicationView != null) {
                    applicationView.displayError("Application cannot be null");
                }
                return false;
            }
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (applicationView != null) {
                    applicationView.displayError("You must be logged in to reject applications");
                }
                return false;
            }
            
            // Check if user is an HDB Manager
            if (!(currentUser instanceof HDBManager)) {
                if (applicationView != null) {
                    applicationView.displayError("Only HDB Managers can reject applications");
                }
                return false;
            }
            
            HDBManager manager = (HDBManager) currentUser;
            
            // Check if manager is managing this project
            if (application.getProject().getHdbManager() == null || 
                !application.getProject().getHdbManager().equals(manager)) {
                if (applicationView != null) {
                    applicationView.displayError("You can only reject applications for projects you manage");
                }
                return false;
            }
            
            // Reject application
            boolean success = applicationService.rejectApplication(application, manager);
            
            if (success) {
                if (applicationView != null) {
                    applicationView.displaySuccess("Application rejected successfully");
                }
            } else {
                if (applicationView != null) {
                    applicationView.displayError("Failed to reject application");
                }
            }
            
            return success;
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error rejecting application: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Processes a withdrawal request
     * 
     * @param application Application with withdrawal request
     * @param approve Whether to approve or reject the withdrawal
     * @return true if processing succeeds
     */
    public boolean processWithdrawal(BTOApplication application, boolean approve) {
        try {
            // Validate application
            if (application == null) {
                if (applicationView != null) {
                    applicationView.displayError("Application cannot be null");
                }
                return false;
            }
            
            // Check if withdrawal was requested
            if (!application.isWithdrawalRequested()) {
                if (applicationView != null) {
                    applicationView.displayError("No withdrawal request for this application");
                }
                return false;
            }
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (applicationView != null) {
                    applicationView.displayError("You must be logged in to process withdrawals");
                }
                return false;
            }
            
            // Check if user is an HDB Manager
            if (!(currentUser instanceof HDBManager)) {
                if (applicationView != null) {
                    applicationView.displayError("Only HDB Managers can process withdrawals");
                }
                return false;
            }
            
            HDBManager manager = (HDBManager) currentUser;
            
            // Process withdrawal
            boolean success = applicationService.processWithdrawal(application, manager, approve);
            
            if (success) {
                if (applicationView != null) {
                    String action = approve ? "approved" : "rejected";
                    applicationView.displaySuccess("Withdrawal request " + action + " successfully");
                }
            } else {
                if (applicationView != null) {
                    applicationView.displayError("Failed to process withdrawal request");
                }
            }
            
            return success;
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error processing withdrawal: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Gets applications for a specific project
     * 
     * @param project Project to get applications for
     * @return List of applications
     */
    public List<BTOApplication> getApplicationsByProject(BTOProject project) {
        try {
            if (project == null) {
                return new ArrayList<>();
            }
            
            return applicationService.getApplicationsByProject(project);
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error retrieving applications: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets applications by user
     * 
     * @param nric NRIC of the user
     * @return List of applications by the user
     */
    public List<BTOApplication> getApplicationsByUser(String nric) {
        try {
            if (nric == null || nric.trim().isEmpty()) {
                return new ArrayList<>();
            }
            
            return applicationService.getApplicationsByApplicantNric(nric);
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error retrieving applications: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets applications by project and status
     * 
     * @param projectId ID of the project
     * @param status Status to filter by
     * @return List of applications with the specified project and status
     */
    public List<BTOApplication> getApplicationsByProjectAndStatus(String projectId, ApplicationStatus status) {
        try {
            if (projectId == null || projectId.trim().isEmpty() || status == null) {
                return new ArrayList<>();
            }
            
            BTOProject project = projectService.getProjectById(projectId);
            if (project == null) {
                return new ArrayList<>();
            }
            
            List<BTOApplication> allApplications = applicationService.getApplicationsByProject(project);
            List<BTOApplication> filteredApplications = new ArrayList<>();
            
            for (BTOApplication app : allApplications) {
                if (app.getStatus() == status) {
                    filteredApplications.add(app);
                }
            }
            
            return filteredApplications;
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error retrieving applications: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    /**
     * Generates a booking receipt for an applicant
     * 
     * @param applicantNric NRIC of the applicant
     * @return Booking receipt as a string
     */
    public String generateBookingReceipt(String applicantNric) {
        try {
            if (applicantNric == null || applicantNric.trim().isEmpty()) {
                return null;
            }
            
            return applicationService.generateBookingReceipt(applicantNric);
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error generating receipt: " + e.getMessage());
            }
            return null;
        }
    }
    
    /**
     * Saves a receipt to a file
     * 
     * @param receipt Receipt content
     * @param filename Filename to save to
     * @return true if save succeeds
     */
    public boolean saveReceiptToFile(String receipt, String filename) {
        try {
            if (receipt == null || receipt.trim().isEmpty()) {
                return false;
            }
            
            if (filename == null || filename.trim().isEmpty()) {
                filename = "receipt.txt";
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write(receipt);
            }
            
            if (applicationView != null) {
                applicationView.displaySuccess("Receipt saved to " + filename + " successfully");
            }
            
            return true;
        } catch (IOException e) {
            if (applicationView != null) {
                applicationView.displayError("Error saving receipt: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Gets the current user's application
     * 
     * @return Current application or null if none
     */
    public BTOApplication getCurrentApplication() {
        try {
            User currentUser = userService.getCurrentUser();
            if (!(currentUser instanceof Applicant)) {
                return null;
            }
            
            Applicant applicant = (Applicant) currentUser;
            return applicant.getCurrentApplication();
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error retrieving current application: " + e.getMessage());
            }
            return null;
        }
    }
    
    /**
     * Gets all applications in the system
     * 
     * @return List of all applications
     */
    public List<BTOApplication> getAllApplications() {
        try {
            return applicationService.getAllApplications();
        } catch (Exception e) {
            if (applicationView != null) {
                applicationView.displayError("Error retrieving applications: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
}