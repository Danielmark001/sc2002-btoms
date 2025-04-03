package models;

import enumeration.ApplicationStatus;
import enumeration.FlatType;


import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an application for a BTO project
 */
public class BTOApplication {
    // Unique identifier for the application
    private final String applicationId;

    // Core application details
    private final Applicant applicant;
    private final BTOProject project;
    private FlatType flatType;

    // Application status and tracking
    private ApplicationStatus status;
    private LocalDateTime applicationDate;
    private LocalDateTime statusUpdateDate;

    // Withdrawal tracking
    private boolean withdrawalRequested;
    private LocalDateTime withdrawalRequestDate;
    
    // Booking details
    private String bookingReceipt;
    private String bookedUnit;

    /**
     * Constructor for Application
     * @param applicationId Unique identifier for the application
     * @param applicant Applicant who submitted the application
     * @param project Project being applied for
     */
    public BTOApplication(String applicationId, Applicant applicant, BTOProject project) {
        // Validate inputs
        validateInputs(applicant, project);
        
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.statusUpdateDate = LocalDateTime.now();
        this.withdrawalRequested = false;
    }
    
    /**
     * Constructor with flat type
     * @param applicationId Unique identifier for the application
     * @param applicant Applicant who submitted the application
     * @param project Project being applied for
     * @param flatType Flat type for the application
     */
    public BTOApplication(String applicationId, Applicant applicant, BTOProject project, FlatType flatType) {
        this(applicationId, applicant, project);
        this.flatType = flatType;
    }

    // Input validation method
    private void validateInputs(Applicant applicant, BTOProject project) {
        if (applicant == null) {
            throw new IllegalArgumentException("Applicant cannot be null");
        }
        
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }

        // Validate applicant's eligibility for the project
        if (!project.isEligibleForApplicant(applicant)) {
            throw new IllegalStateException("Applicant is not eligible for this project");
        }
    }

    /**
     * Generates a unique application ID
     * @return Unique application ID
     */
    public static String generateUniqueId() {
        return "APP-" + System.currentTimeMillis() + 
               "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Updates the application status with validation
     * @param newStatus New status to set
     * @throws IllegalStateException if the status transition is invalid
     */
    public void updateStatus(ApplicationStatus newStatus) {
        validateStatusTransition(this.status, newStatus);
        
        this.status = newStatus;
        this.statusUpdateDate = LocalDateTime.now();
    }

    /**
     * Validate status transitions to ensure proper application flow
     * @param currentStatus Current status
     * @param newStatus New status
     * @throws IllegalStateException if the status transition is invalid
     */
    private void validateStatusTransition(ApplicationStatus currentStatus, ApplicationStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                if (!(newStatus == ApplicationStatus.SUCCESSFUL || 
                      newStatus == ApplicationStatus.UNSUCCESSFUL ||
                      newStatus == ApplicationStatus.WITHDRAWN)) {
                    throw new IllegalStateException("Invalid status transition from PENDING");
                }
                break;
            case SUCCESSFUL:
                if (!(newStatus == ApplicationStatus.BOOKED || 
                      newStatus == ApplicationStatus.UNSUCCESSFUL ||
                      newStatus == ApplicationStatus.WITHDRAWN)) {
                    throw new IllegalStateException("Invalid status transition from SUCCESSFUL");
                }
                break;
            case BOOKED:
                if (!(newStatus == ApplicationStatus.UNSUCCESSFUL || 
                     newStatus == ApplicationStatus.WITHDRAWN)) {
                    throw new IllegalStateException("Invalid status transition from BOOKED");
                }
                break;
            case UNSUCCESSFUL:
            case WITHDRAWN:
                throw new IllegalStateException("Cannot change status of completed application");
        }
    }

    /**
     * Requests withdrawal of this application
     * @throws IllegalStateException if application cannot be withdrawn
     */
    public void requestWithdrawal() {
        // Can only request withdrawal for pending or successful applications
        if (status != ApplicationStatus.PENDING && status != ApplicationStatus.SUCCESSFUL) {
            throw new IllegalStateException("Cannot withdraw application in current status");
        }
        
        this.withdrawalRequested = true;
        this.withdrawalRequestDate = LocalDateTime.now();
    }

    /**
     * Resets the withdrawal request
     */
    public void resetWithdrawalRequest() {
        this.withdrawalRequested = false;
        this.withdrawalRequestDate = null;
    }

    /**
     * Books a flat for this application
     * @param officer HDB Officer processing the booking
     * @param unitNumber Unit number being booked
     * @return true if booking is successful
     */
    public boolean bookFlat(HDBOfficer officer, String unitNumber) {
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
        this.bookedUnit = unitNumber;

        // Add reference to the officer who processed the booking
        officer.addProcessedApplication(this);
        
        return true;
    }

    /**
     * Gets the application ID
     * @return Application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Gets the applicant
     * @return Applicant who submitted the application
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Gets the project
     * @return Project being applied for
     */
    public BTOProject getProject() {
        return project;
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
     * Gets the application status
     * @return Current status of the application
     */
    public ApplicationStatus getStatus() {
        return status;
    }

    /**
     * Sets the application status
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
     * Gets the booked unit number
     * @return Booked unit number
     */
    public String getBookedUnit() {
        return bookedUnit;
    }
    
    /**
     * Sets the booked unit number
     * @param bookedUnit Unit number to set
     */
    public void setBookedUnit(String bookedUnit) {
        this.bookedUnit = bookedUnit;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BTOApplication that = (BTOApplication) o;
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