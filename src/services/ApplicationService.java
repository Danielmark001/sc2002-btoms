package services;

import models.*;
import enumeration.*;
import stores.AuthStore;
import stores.DataStore;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicationService implements interfaces.IApplicationService {
    private static ApplicationService instance;
    private DataStore dataStore;
    private AuthStore authStore;

    private ApplicationService() {
        this.dataStore = DataStore.getInstance();
        this.authStore = AuthStore.getInstance();
    }

    public static synchronized ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
        }
        return instance;
    }

    @Override
    public Application createApplication(Applicant applicant, BTOProject project) {
        // Validate applicant eligibility
        if (!project.isEligibleForApplicant(applicant)) {
            throw new IllegalStateException("Applicant is not eligible for this project");
        }

        // Check if applicant already has an active application
        if (hasActiveApplication(applicant)) {
            throw new IllegalStateException("Applicant already has an active application");
        }

        // Determine flat type based on applicant's eligibility
        FlatType flatType = determineFlatType(applicant, project);

        // Create and save application
        Application application = new Application(
            generateUniqueApplicationId(), 
            applicant, 
            project
        );
        application.setStatus(ApplicationStatus.PENDING);
        application.setFlatType(flatType);

        // Persist application
        saveApplication(application);

        return application;
    }

    @Override
    public boolean approveApplication(Application application, HDBManager manager) {
        // Validate user is HDB Manager
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            throw new SecurityException("Only HDB Managers can approve applications");
        }

        // Check application status
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }

        // Check flat availability
        BTOProject project = application.getProject();
        if (project.getAvailableUnits(application.getFlatType()) > 0) {
            application.setStatus(ApplicationStatus.SUCCESSFUL);
            saveApplication(application);
            return true;
        }

        return false;
    }

    @Override
    public boolean rejectApplication(Application application, HDBManager manager) {
        // Validate user is HDB Manager
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            throw new SecurityException("Only HDB Managers can reject applications");
        }

        // Check application status
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return false;
        }

        application.setStatus(ApplicationStatus.UNSUCCESSFUL);
        saveApplication(application);
        return true;
    }

    @Override
    public boolean processWithdrawal(Application application, HDBManager manager, boolean approve) {
        // Validate user is HDB Manager
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            throw new SecurityException("Only HDB Managers can process withdrawals");
        }


        if (approve) {
            application.setStatus(ApplicationStatus.WITHDRAWN);
        }

        saveApplication(application);
        return true;
    }

    @Override
    public boolean bookFlat(Application application, HDBOfficer officer, FlatType flatType) {
        // Validate user is HDB Officer
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            throw new SecurityException("Only HDB Officers can book flats");
        }

        // Check application status
        if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return false;
        }

        // Check flat availability
        BTOProject project = application.getProject();
        if (project.getAvailableUnits(flatType) <= 0) {
            return false;
        }

        // Update application
        application.setStatus(ApplicationStatus.BOOKED);
        

        // Reduce available units
        

        // Generate booking receipt
        String receipt = generateBookingReceipt(application);
        application.setBookingReceipt(receipt);

        saveApplication(application);
        return true;
    }

    @Override
    public List<Application> getApplicationsByProject(BTOProject project) {
        return getAllApplications().stream()
            .filter(app -> app.getProject().equals(project))
            .collect(Collectors.toList());
    }

    @Override
    public List<Application> getApplicationsByStatus(BTOProject project, ApplicationStatus status) {
        return getAllApplications().stream()
            .filter(app -> app.getProject().equals(project) && app.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public String generateBookingReceipt(String applicantNric) {
        Application application = getApplicationByApplicantNric(applicantNric);
        if (application == null || application.getStatus() != ApplicationStatus.BOOKED) {
            return null;
        }

        return generateBookingReceipt(application);
    }

    // Helper methods
    private String generateUniqueApplicationId() {
        return "APP-" + System.currentTimeMillis();
    }

    private boolean hasActiveApplication(Applicant applicant) {
        return getAllApplications().stream()
            .filter(app -> app.getApplicant().equals(applicant))
            .anyMatch(app -> 
                app.getStatus() == ApplicationStatus.PENDING || 
                app.getStatus() == ApplicationStatus.SUCCESSFUL
            );
    }

    private FlatType determineFlatType(Applicant applicant, BTOProject project) {
        // Logic to determine flat type based on applicant's eligibility
        if (applicant.isEligibleForFlatType(FlatType.TWO_ROOM)) {
            return FlatType.TWO_ROOM;
        } else if (applicant.isEligibleForFlatType(FlatType.THREE_ROOM)) {
            return FlatType.THREE_ROOM;
        }
        throw new IllegalStateException("No eligible flat type found");
    }

    private void saveApplication(Application application) {
        // In a real implementation, this would interact with DataStore
        DataStore.saveData();
    }

    private List<Application> getAllApplications() {
        // In a real implementation, this would come from DataStore
        return new ArrayList<>();
    }

    private Application getApplicationByApplicantNric(String nric) {
        return getAllApplications().stream()
            .filter(app -> app.getApplicant().getNric().equals(nric))
            .findFirst()
            .orElse(null);
    }

    private String generateBookingReceipt(Application application) {
        Applicant applicant = application.getApplicant();
        return String.format(
            "BOOKING RECEIPT\n" +
            "Applicant: %s\n" +
            "NRIC: %s\n" +
            "Project: %s\n" +
            "Flat Type: %s\n" +
            "Booking Date: %s",
            applicant.getName(),
            applicant.getNric(),
            application.getProject().getProjectName(),
            LocalDate.now()
        );
    }
}