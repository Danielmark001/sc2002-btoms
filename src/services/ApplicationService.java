package services;

import interfaces.IApplicationService;
import models.*;
import enumeration.*;
import stores.AuthStore;
import stores.DataStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import services.UserService;

/**
 * Service for managing BTO applications
 */
public class ApplicationService implements IApplicationService {
    private static ApplicationService instance;
    private DataStore dataStore;
    private AuthStore authStore;

    private ApplicationService() {
        this.dataStore = DataStore.getInstance();
        this.authStore = AuthStore.getInstance();
    }

    /**
     * Gets the singleton instance of ApplicationService
     * @return ApplicationService instance
     */
    public static synchronized ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
        }
        return instance;
    }

    @Override
    public Application createApplication(Applicant applicant, BTOProject project) {
        // Validate inputs
        if (applicant == null || project == null) {
            throw new IllegalArgumentException("Applicant and project cannot be null");
        }

        // Validate applicant eligibility
        if (!project.isEligibleForApplicant(applicant)) {
            throw new IllegalStateException("Applicant is not eligible for this project");
        }

        // Check if project is open for applications
        if (!project.isOpenForApplications()) {
            throw new IllegalStateException("Project is not open for applications");
        }

        // Check if applicant already has an active application
        if (hasActiveApplication(applicant)) {
            throw new IllegalStateException("Applicant already has an active application");
        }

        // Determine flat type based on applicant's eligibility
        FlatType flatType = determineFlatType(applicant, project);

        // Create application with generated ID
        Application application = new Application(
            generateUniqueApplicationId(),
            applicant,
            project
        );
        
        // Set initial properties
        application.setFlatType(flatType);
        application.setStatus(ApplicationStatus.PENDING);
        application.setApplicationDate(LocalDateTime.now());

        // Add application to applicant's list of applications
        applicant.addApplication(application);
        
        // Add application to project's list of applications
        project.addApplication(application);

        // Save to data store
        dataStore.addApplication(application);

        return application;
    }

    @Override
    public boolean approveApplication(Application application, HDBManager manager) {
        // Validate inputs
        if (application == null || manager == null) {
            return false;
        }

        // Verify the manager is managing this project
        if (!application.getProject().getHdbManager().equals(manager)) {
            return false;
        }

        // Check application status
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }

        // Check if withdrawal has been requested
        if (application.isWithdrawalRequested()) {
            return false;
        }

        // Check flat availability
        BTOProject project = application.getProject();
        FlatType flatType = application.getFlatType();
        
        if (project.getAvailableUnits(flatType) <= 0) {
            return false;
        }

        // Update application status
        application.setStatus(ApplicationStatus.SUCCESSFUL);

        // Save updated application
        dataStore.updateApplication(application);

        return true;
    }

    @Override
    public boolean rejectApplication(Application application, HDBManager manager) {
        // Validate inputs
        if (application == null || manager == null) {
            return false;
        }

        // Verify the manager is managing this project
        if (!application.getProject().getHdbManager().equals(manager)) {
            return false;
        }

        // Check application status
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }

        // Update application status
        application.setStatus(ApplicationStatus.UNSUCCESSFUL);

        // Save updated application
        dataStore.updateApplication(application);

        return true;
    }

    @Override
    public boolean processWithdrawal(Application application, HDBManager manager, boolean approve) {
        // Validate inputs
        if (application == null || manager == null) {
            return false;
        }

        // Verify the manager is managing this project
        if (!application.getProject().getHdbManager().equals(manager)) {
            return false;
        }

        // Check withdrawal request status
        if (!application.isWithdrawalRequested()) {
            return false;
        }

        // Process based on approval decision
        if (approve) {
            // Set application status to UNSUCCESSFUL to indicate withdrawal
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            
            // If already booked, update unit count
            if (application.getStatus() == ApplicationStatus.BOOKED) {
                // Implementation would handle updating unit availability if needed
            }
        } else {
            // Reset withdrawal request flag
            // This requires adding a method to reset withdrawal in the Application class
            resetWithdrawalRequest(application);
        }

        // Save updated application
        dataStore.updateApplication(application);

        return true;
    }

    @Override
    public boolean bookFlat(Application application, HDBOfficer officer, FlatType flatType) {
        // Validate inputs
        if (application == null || officer == null || flatType == null) {
            return false;
        }

        // Verify the officer is handling this project
        if (officer.getHandlingProject() == null || 
            !officer.getHandlingProject().equals(application.getProject())) {
            return false;
        }

        // Check application status
        if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return false;
        }

        // Check if withdrawal has been requested
        if (application.isWithdrawalRequested()) {
            return false;
        }

        // Check flat availability
        BTOProject project = application.getProject();
        if (project.getAvailableUnits(flatType) <= 0) {
            return false;
        }

        // Update application
        application.setStatus(ApplicationStatus.BOOKED);
        application.setFlatType(flatType);

        // Generate booking receipt
        String receipt = generateBookingReceipt(application.getApplicant().getNric());
        application.setBookingReceipt(receipt);

        // Add to officer's processed applications
        officer.addProcessedApplication(application);

        // Save updated application
        dataStore.updateApplication(application);

        return true;
    }

    @Override
    public List<Application> getApplicationsByProject(BTOProject project) {
        if (project == null) {
            return new ArrayList<>();
        }
        
        return dataStore.getApplicationsByProject(project.getProjectId());
    }

    @Override
    public List<Application> getApplicationsByStatus(BTOProject project, ApplicationStatus status) {
        if (project == null || status == null) {
            return new ArrayList<>();
        }
        
        return getApplicationsByProject(project).stream()
            .filter(app -> app.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public String generateBookingReceipt(String applicantNric) {
        // Validate input
        if (applicantNric == null) {
            return null;
        }
        
        // Find applicant's applications
        User user = dataStore.getUserByNRIC(applicantNric);
        if (!(user instanceof Applicant)) {
            return null;
        }
        
        Applicant applicant = (Applicant) user;
        
        // Find booked application
        Optional<Application> bookedApp = applicant.getApplications().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.BOOKED)
            .findFirst();
        
        if (!bookedApp.isPresent()) {
            return null;
        }
        
        Application application = bookedApp.get();
        BTOProject project = application.getProject();
        
        // Generate receipt
        return String.format(
            "BOOKING RECEIPT\n" +
            "----------------\n" +
            "Receipt ID: %s\n" +
            "Date: %s\n\n" +
            "APPLICANT DETAILS\n" +
            "Name: %s\n" +
            "NRIC: %s\n" +
            "Age: %d\n" +
            "Marital Status: %s\n\n" +
            "PROJECT DETAILS\n" +
            "Project Name: %s\n" +
            "Neighborhood: %s\n" +
            "Flat Type: %s\n\n" +
            "This receipt confirms your successful booking of a BTO flat.\n" +
            "Please retain this receipt for your records.",
            "RCPT-" + System.currentTimeMillis(),
            LocalDate.now(),
            applicant.getName(),
            applicant.getNric(),
            applicant.calculateAge(),
            applicant.getMaritalStatus(),
            project.getProjectName(),
            project.getNeighborhood(),
            application.getFlatType()
        );
    }

    /**
     * Gets all applications
     * @return List of all applications
     */
    public List<Application> getAllApplications() {
        return dataStore.getAllApplications();
    }

    /**
     * Gets an application by its ID
     * @param applicationId Application ID to look up
     * @return Application if found, null otherwise
     */
    public Application getApplicationById(String applicationId) {
        if (applicationId == null) {
            return null;
        }
        
        return getAllApplications().stream()
            .filter(app -> app.getApplicationId().equals(applicationId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets applications by applicant
     * @param applicant Applicant to find applications for
     * @return List of applications for the applicant
     */
    public List<Application> getApplicationsByApplicant(Applicant applicant) {
        if (applicant == null) {
            return new ArrayList<>();
        }
        
        return getAllApplications().stream()
            .filter(app -> app.getApplicant().equals(applicant))
            .collect(Collectors.toList());
    }

    /**
     * Gets applications by applicant NRIC
     * @param nric NRIC of the applicant
     * @return List of applications for the applicant
     */
    public List<Application> getApplicationsByApplicantNric(String nric) {
        if (nric == null) {
            return new ArrayList<>();
        }
        
        return getAllApplications().stream()
            .filter(app -> app.getApplicant().getNric().equals(nric))
            .collect(Collectors.toList());
    }

    /**
     * Checks if an applicant has an active application
     * @param applicant Applicant to check
     * @return true if applicant has an active application
     */
    private boolean hasActiveApplication(Applicant applicant) {
        if (applicant == null) {
            return false;
        }
        
        return getApplicationsByApplicant(applicant).stream()
            .anyMatch(app -> 
                app.getStatus() == ApplicationStatus.PENDING || 
                app.getStatus() == ApplicationStatus.SUCCESSFUL ||
                app.getStatus() == ApplicationStatus.BOOKED
            );
    }

    /**
     * Determines the appropriate flat type for an applicant
     * @param applicant Applicant to determine flat type for
     * @param project Project to check availability in
     * @return Appropriate flat type
     * @throws IllegalStateException if no eligible flat type with available units
     */
    private FlatType determineFlatType(Applicant applicant, BTOProject project) {
        // For married applicants, check 3-Room first (assuming it's preferred)
        if (applicant.getMaritalStatus() == MaritalStatus.MARRIED) {
            if (applicant.isEligibleForFlatType(FlatType.THREE_ROOM) && 
                project.getAvailableUnits(FlatType.THREE_ROOM) > 0) {
                return FlatType.THREE_ROOM;
            }
        }
        
        // Check for 2-Room eligibility
        if (applicant.isEligibleForFlatType(FlatType.TWO_ROOM) && 
            project.getAvailableUnits(FlatType.TWO_ROOM) > 0) {
            return FlatType.TWO_ROOM;
        }
        
        throw new IllegalStateException("No eligible flat type found with available units");
    }

    /**
     * Generates a unique application ID
     * @return Unique application ID
     */
    private String generateUniqueApplicationId() {
        return "APP-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Resets withdrawal request flag for an application
     * @param application Application to reset withdrawal for
     */
    private void resetWithdrawalRequest(Application application) {
        // This requires adding these methods to the Application class
        if (application != null) {
            // Set withdrawal requested to false
            // Implementation depends on the Application class having this method
            if (application instanceof Applicant) {
                ((Applicant) application).setWithdrawalRequest(false);
            }
        }
    }

    public boolean updateApplication(Application application) {
    if (application == null) {
        return false;
    }
    
    // Save to data store
    return dataStore.updateApplication(application);
}

/**
 * Missing method to add setWithdrawalRequest to Applicant class
 */
public void setWithdrawalRequest(boolean requested) {
    // Find the current application
    Application currentApp = getCurrentApplication();
    if (currentApp != null) {
        // Update the withdrawal request status
        // This assumes the Application class has a method to update the withdrawal status directly
        // If not, we would need to call requestWithdrawal() or reset the flag
        if (requested) {
            currentApp.requestWithdrawal();
        } else {
            // Reset the withdrawal request flag - requires adding this method to Application
            if (currentApp instanceof Application) {
                // Cast and access the field directly, or call a method if available
                Field withdrawalRequestedField;
                try {
                    withdrawalRequestedField = Application.class.getDeclaredField("withdrawalRequested");
                    withdrawalRequestedField.setAccessible(true);
                    withdrawalRequestedField.set(currentApp, false);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    // Log error but continue
                    System.err.println("Error resetting withdrawal request: " + e.getMessage());
                }
            }
        }
    }
}

/**
 * Missing method to get the current project for HDBOfficer class
 */
public BTOProject getHandlingProject() {
    return handlingProject;
}

/**
 * Missing method to check if a user is an HDB Manager in User class
 */
public boolean isHdbManager() {
    return this instanceof HDBManager;
}

/**
 * Missing method in ProjectController to get enquiries by project
 */
public List<Enquiry> getEnquiriesByProject(BTOProject project) {
    EnquiryService enquiryService = EnquiryService.getInstance();
    return enquiryService.getEnquiriesByProject(project);
}

/**
 * Missing method for getEnquiryData in DataStore class
 */
public List<String[]> getEnquiryData() {
    lock.readLock().lock();
    try {
        return new ArrayList<>(allData.getOrDefault("enquiries", new ArrayList<>()));
    } finally {
        lock.readLock().unlock();
    }
}

/**
 * Missing getOfficerIds method in BTOProject class
 */
public List<String> getOfficerIds() {
    List<String> officerIds = new ArrayList<>();
    for (Registration reg : registrations) {
        if (reg.getStatus() == RegistrationStatus.APPROVED) {
            officerIds.add(reg.getOfficer().getNric());
        }
    }
    return officerIds;
}

/**
 * Missing utility method in UserController to generate reports
 */
public String generateReport(String filter, String value) {
    // Create the report service
    ReportService reportService = new ReportService();
    
    // Generate the appropriate report
    switch (filter.toLowerCase()) {
        case "project":
            return reportService.generateProjectReport(value);
        case "flat-type":
            return reportService.generateFlatTypeReport(value);
        case "marital-status":
            return reportService.generateMaritalStatusReport(value);
        case "application-status":
            return reportService.generateApplicationStatusReport();
        case "project-application":
            return reportService.generateProjectApplicationReport();
        default:
            return "Invalid filter criteria. Please use project, flat-type, marital-status, application-status, or project-application.";
    }
}

/**
 * Missing utility method to change password in UserController
 */
public boolean changePassword(String oldPassword, String newPassword) {
    // Validate input
    if (oldPassword == null || newPassword == null || 
        oldPassword.trim().isEmpty() || newPassword.trim().isEmpty()) {
        return false;
    }
    
    // Validate new password complexity
    if (newPassword.length() < 8) {
        return false;
    }
    
    // Get current user
    User currentUser = userService.getCurrentUser();
    if (currentUser == null) {
        return false;
    }
    
    // Change password
    return userService.changePassword(oldPassword, newPassword);
}

/**
 * Missing method to get applications for a project in ProjectController
 */
public List<Application> getApplicationsForProject(BTOProject project) {
    if (project == null) {
        return new ArrayList<>();
    }
    
    ApplicationService applicationService = ApplicationService.getInstance();
    return applicationService.getApplicationsByProject(project);
}
}