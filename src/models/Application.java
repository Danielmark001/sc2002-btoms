package models;

import enumeration.ApplicationStatus;
import enumeration.FlatType;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

/**
 * Represents an application for a BTO project
 */
public class Application {
    private String applicationId;
    private Applicant applicant;
    private BTOProject project;
    private ApplicationStatus status;
    private String bookingReceipt;
    private FlatType flatType;
    private LocalDateTime applicationDate;
    private LocalDateTime statusUpdateDate;
    private boolean withdrawalRequested;
    private LocalDateTime withdrawalRequestDate;

    /**
     * Constructor for Application
     * @param applicationId Unique identifier for the application
     * @param applicant Applicant who submitted the application
     * @param project Project being applied for
     */
    public Application(String applicationId, Applicant applicant, BTOProject project) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.statusUpdateDate = LocalDateTime.now();
        this.withdrawalRequested = false;
    }

    /**
     * Gets the application ID
     * @return Application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Sets the application ID
     * @param applicationId Application ID to set
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * Gets the applicant
     * @return Applicant who submitted the application
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Sets the applicant
     * @param applicant Applicant to set
     */
    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    /**
     * Gets the project
     * @return Project being applied for
     */
    public BTOProject getProject() {
        return project;
    }

    /**
     * Sets the project
     * @param project Project to set
     */
    public void setProject(BTOProject project) {
        this.project = project;
    }

    /**
     * Gets the application status
     * @return Current status of the application
     */
    public ApplicationStatus getStatus() {
        return status;
    }

    /**
     * Sets the application status and updates the status update date
     * @param status Status to set
     */
    public void setStatus(ApplicationStatus status) {
        this.status = status;
        this.statusUpdateDate = LocalDateTime.now();
    }

    /**
     * Gets the booking receipt
     * @return Booking receipt as a string
     */
    public String getBookingReceipt() {
        return bookingReceipt;
    }

    /**
     * Sets the booking receipt
     * @param bookingReceipt Booking receipt to set
     */
    public void setBookingReceipt(String bookingReceipt) {
        this.bookingReceipt = bookingReceipt;
    }

    /**
     * Gets the flat type
     * @return Flat type for this application
     */
    public FlatType getFlatType() {
        return flatType;
    }

    /**
     * Sets the flat type
     * @param flatType Flat type to set
     */
    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    /**
     * Gets the application date
     * @return Date and time when the application was submitted
     */
    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    /**
     * Sets the application date
     * @param applicationDate Application date to set
     */
    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    /**
     * Gets the status update date
     * @return Date and time when the status was last updated
     */
    public LocalDateTime getStatusUpdateDate() {
        return statusUpdateDate;
    }

    /**
     * Sets the status update date
     * @param statusUpdateDate Status update date to set
     */
    public void setStatusUpdateDate(LocalDateTime statusUpdateDate) {
        this.statusUpdateDate = statusUpdateDate;
    }

    /**
     * Checks if withdrawal has been requested
     * @return true if withdrawal has been requested
     */
    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }

    /**
     * Gets the withdrawal request date
     * @return Date and time when withdrawal was requested
     */
    public LocalDateTime getWithdrawalRequestDate() {
        return withdrawalRequestDate;
    }

    /**
     * Requests withdrawal of this application
     */
    public void requestWithdrawal() {
        if (this.status == ApplicationStatus.PENDING || 
            this.status == ApplicationStatus.SUCCESSFUL) {
            this.withdrawalRequested = true;
            this.withdrawalRequestDate = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Withdrawal can only be requested for PENDING or SUCCESSFUL applications");
        }
    }

    /**
     * Resets the withdrawal request flag
     */
    public void resetWithdrawalRequest() {
        this.withdrawalRequested = false;
        this.withdrawalRequestDate = null;
    }

    /**
     * Books a flat for this application
     * @param officer HDB Officer processing the booking
     * @return true if booking is successful
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

        // Check if withdrawal has been requested
        if (this.withdrawalRequested) {
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
        
        return true;
    }

    /**
     * Checks if the application is eligible for booking
     * @return true if eligible for booking
     */
    public boolean isEligibleForBooking() {
        return this.status == ApplicationStatus.SUCCESSFUL && !this.withdrawalRequested;
    }

    /**
     * Checks if the application can be withdrawn
     * @return true if the application can be withdrawn
     */
    public boolean canWithdraw() {
        return (this.status == ApplicationStatus.PENDING || 
                this.status == ApplicationStatus.SUCCESSFUL) && 
               !this.withdrawalRequested;
    }
    
    /**
     * Generates a unique application ID
     * @return Unique application ID
     */
    public static String generateUniqueId() {
        return "APP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Application that = (Application) obj;
        return this.applicationId.equals(that.applicationId);
    }

    @Override
    public int hashCode() {
        return this.applicationId.hashCode();
    }

    @Override
    public String toString() {
        return "Application{" +
                "applicationId='" + applicationId + '\'' +
                ", applicant=" + applicant.getNric() +
                ", project=" + project.getProjectName() +
                ", status=" + status +
                ", flatType=" + flatType +
                ", applicationDate=" + applicationDate +
                ", withdrawalRequested=" + withdrawalRequested +
                '}';
    }
}