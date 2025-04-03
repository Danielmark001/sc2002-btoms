package models;

import enumeration.ApplicationStatus;
import enumeration.FlatType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.List;

public class Application {
    private String applicationId;
    private Applicant applicant;
    private BTOProject project;
    private static List<Application> applications; 
    private ApplicationStatus status;
    private String bookingReceipt;
    private FlatType flatType; // Added flatType variable
    private LocalDateTime applicationDate;
    private LocalDateTime statusUpdateDate; // Added statusUpdateDate field

    public Application(String applicationId, Applicant applicant, BTOProject project) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.status = ApplicationStatus.PENDING; // Default status
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setBookedFlatType(FlatType bookedFlatType) {
        this.flatType = bookedFlatType;
    }

    public FlatType getBookedFlatType() {
        return flatType;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public BTOProject getProject() {
        return project;
    }

    public void setProject(BTOProject project) {
        this.project = project;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getBookingReceipt() {
        return bookingReceipt;
    }

    public void setBookingReceipt(String bookingReceipt) {
        this.bookingReceipt = bookingReceipt;

    }

    public FlatType getFlatType() {
        return flatType;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    /**
 * Books a flat for this application
 * 
 * @param officer HDB Officer processing the booking
 * @return true if booking is successful, false otherwise
 */
    public boolean bookFlat(HDBOfficer officer) {
        // Validate application status
        if (this.status != ApplicationStatus.SUCCESSFUL) {
            return false;
        }

        // Validate officer is assigned to this project
        if (officer.getHandlingProject() == null || !officer.getHandlingProject().equals(this.project)) {
            return false;
        }

        // Check flat availability
        if (project.getAvailableUnits(this.flatType) <= 0) {
            return false;
        }

        // Update application status
        this.status = ApplicationStatus.BOOKED;
        this.statusUpdateDate = LocalDateTime.now();

        // Add reference to the officer who processed the booking
        officer.addProcessedApplication(this);
    }

    /**
    * Gets the list of all applications
    * @return List of applications
    */
    public static List<Application> getApplications() {
        return applications;
    }

    public Application getCurrentApplication() {
        // Check if there are any applications
        if (getApplications().isEmpty()) {
            return null;
        }

        // Look for the most recent active application (not unsuccessful)
        Optional<Application> activeApp = getApplications().stream()
                .filter(app -> app.getStatus() != ApplicationStatus.UNSUCCESSFUL)
                .max(Comparator.comparing(Application::getApplicationDate));

        return activeApp.orElse(null);
    }

    /**
     * Requests withdrawal for the current application
     * @return true if withdrawal request was made, false otherwise
     */
    public boolean requestWithdrawal() {
        Application currentApp = getCurrentApplication();

        if (currentApp == null) {
            return false;
        }

        // Check if application can be withdrawn
        if (currentApp.getStatus() == ApplicationStatus.PENDING ||
                currentApp.getStatus() == ApplicationStatus.SUCCESSFUL) {
            currentApp.requestWithdrawal();
            return true;
        }

        return false;
    }


}
