package services;

import interfaces.IBTOApplicationService;
import models.*;
import enumeration.*;
import stores.AuthStore;
import stores.DataStore;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing BTO applications
 */
public class BTOApplicationService implements IBTOApplicationService {
    private static BTOApplicationService instance;
    private DataStore dataStore;
    private AuthStore authStore;

    private BTOApplicationService() {
        this.dataStore = DataStore.getInstance();
        this.authStore = AuthStore.getInstance();
    }

    /**
     * Gets the singleton instance of BTOApplicationService
     * @return BTOApplicationService instance
     */
    public static synchronized BTOApplicationService getInstance() {
        if (instance == null) {
            instance = new BTOApplicationService();
        }
        return instance;
    }

    @Override
    public BTOApplication createApplication(Applicant applicant, BTOProject project) {
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
        BTOApplication application = new BTOApplication(
            BTOApplication.generateUniqueId(),
            applicant,
            project,
            flatType
        );
        
        // Set initial properties
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
    public boolean approveApplication(BTOApplication application, HDBManager manager) {
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
    public boolean rejectApplication(BTOApplication application, HDBManager manager) {
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
    public boolean processWithdrawal(BTOApplication application, HDBManager manager, boolean approve) {
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
            // Set application status to WITHDRAWN to indicate withdrawal
            application.setStatus(ApplicationStatus.WITHDRAWN);
            
            // If already booked, update unit count
            if (application.getStatus() == ApplicationStatus.BOOKED) {
                // Implementation would handle updating unit availability if needed
            }
        } else {
            // Reset withdrawal request flag
            application.resetWithdrawalRequest();
        }

        // Save updated application
        dataStore.updateApplication(application);

        return true;
    }

    @Override
    public boolean bookFlat(BTOApplication application, HDBOfficer officer, FlatType flatType) {
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

        // Generate unit number (simplified implementation)
        String unitNumber = generateUnitNumber(flatType);

        // Update application
        application.setStatus(ApplicationStatus.BOOKED);
        application.setFlatType(flatType);
        application.setBookedUnit(unitNumber);

        // Generate booking receipt
        String receipt = generateBookingReceipt(application);
        application.setBookingReceipt(receipt);

        // Add to officer's processed applications
        officer.addProcessedApplication(application);

        // Save updated application
        dataStore.updateApplication(application);

        return true;
    }

    @Override
    public List<BTOApplication> getApplicationsByProject(BTOProject project) {
        if (project == null) {
            return new ArrayList<>();
        }
        
        return dataStore.getApplicationsByProject(project.getProjectId());
    }

    @Override
    public List<BTOApplication> getApplicationsByStatus(BTOProject project, ApplicationStatus status) {
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
        Optional<BTOApplication> bookedApp = applicant.getApplications().stream()
            .filter(app -> app.getStatus() == ApplicationStatus.BOOKED)
            .findFirst();
        
        if (!bookedApp.isPresent()) {
            return null;
        }
        
        BTOApplication application = bookedApp.get();
        BTOProject project = application.getProject();
        
        // Generate receipt
        return generateBookingReceipt(application);
    }
    
    /**
     * Generates a booking receipt for an application
     * @param application Application to generate receipt for
     * @return Booking receipt as a string
     */
    private String generateBookingReceipt(BTOApplication application) {
        if (application == null || application.getStatus() != ApplicationStatus.BOOKED) {
            return null;
        }
        
        Applicant applicant = application.getApplicant();
        BTOProject project = application.getProject();
        
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
            "Flat Type: %s\n" +
            "Unit Number: %s\n\n" +
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
            application.getFlatType(),
            application.getBookedUnit()
        );
    }

    /**
     * Gets all applications
     * @return List of all applications
     */
    public List<BTOApplication> getAllApplications() {
        return dataStore.getAllApplications();
    }

    /**
     * Gets an application by its ID
     * @param applicationId Application ID to look up
     * @return Application if found, null otherwise
     */
    public BTOApplication getApplicationById(String applicationId) {
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
    public List<BTOApplication> getApplicationsByApplicant(Applicant applicant) {
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
    public List<BTOApplication> getApplicationsByApplicantNric(String nric) {
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
     * Generates a unit number for a booked flat
     * @param flatType Flat type being booked
     * @return Generated unit number
     */
    private String generateUnitNumber(FlatType flatType) {
        // Simple unit number generator (should be more sophisticated in a real system)
        String typePrefix = flatType == FlatType.TWO_ROOM ? "2R-" : "3R-";
        return typePrefix + (100 + new Random().nextInt(900));
    }

    /**
     * Updates an existing application
     * @param application Application to update
     * @return true if update was successful
     */
    public boolean updateApplication(BTOApplication application) {
        if (application == null) {
            return false;
        }
        
        return dataStore.updateApplication(application);
    }
}