package models;

import enumeration.ApplicationStatus;
import enumeration.FlatType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class BTOApplication {
    // Unique identifier for the application
    private final String applicationId;

    // Core application details
    private final User applicant;
    private final BTOProject project;
    private FlatType flatType;

    // Application status and tracking
    private ApplicationStatus status;
    private LocalDateTime applicationDate;
    private LocalDateTime statusUpdateDate;

    // Withdrawal tracking
    private boolean withdrawalRequested;
    private LocalDateTime withdrawalRequestDate;

    // Constructors
    public BTOApplication(User applicant, BTOProject project, FlatType flatType) {
        // Validate inputs
        validateInputs(applicant, project, flatType);

        // Generate unique application ID
        this.applicationId = generateUniqueApplicationId();

        // Set core details
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;

        // Initialize application status
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDateTime.now();
        this.statusUpdateDate = LocalDateTime.now();
    }

    // Input validation method
    private void validateInputs(User applicant, BTOProject project, FlatType flatType) {
        if (applicant == null) {
            throw new IllegalArgumentException("Applicant cannot be null");
        }
        
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }
        
        if (flatType == null) {
            throw new IllegalArgumentException("Flat type cannot be null");
        }

        // Validate applicant's eligibility for the project and flat type
        if (!project.isEligibleForApplicant(applicant)) {
            throw new IllegalStateException("Applicant is not eligible for this project");
        }
    }

    // Generate unique application ID
    private String generateUniqueApplicationId() {
        return "APP-" + System.currentTimeMillis() + 
               "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // Status transition method with validation
    public void updateStatus(ApplicationStatus newStatus) {
        validateStatusTransition(this.status, newStatus);
        
        this.status = newStatus;
        this.statusUpdateDate = LocalDateTime.now();
    }

    // Validate status transitions
    private void validateStatusTransition(ApplicationStatus currentStatus, ApplicationStatus newStatus) {
        switch (currentStatus) {
            case PENDING:
                if (!(newStatus == ApplicationStatus.SUCCESSFUL || 
                      newStatus == ApplicationStatus.UNSUCCESSFUL)) {
                    throw new IllegalStateException("Invalid status transition from PENDING");
                }
                break;
            case SUCCESSFUL:
                if (!(newStatus == ApplicationStatus.BOOKED || 
                      newStatus == ApplicationStatus.UNSUCCESSFUL)) {
                    throw new IllegalStateException("Invalid status transition from SUCCESSFUL");
                }
                break;
            case BOOKED:
            case UNSUCCESSFUL:
                throw new IllegalStateException("Cannot change status of completed application");
        }
    }

    // Withdrawal request method
    public void requestWithdrawal() {
        // Can only request withdrawal for pending or successful applications
        if (status != ApplicationStatus.PENDING && status != ApplicationStatus.SUCCESSFUL) {
            throw new IllegalStateException("Cannot withdraw application in current status");
        }
        
        this.withdrawalRequested = true;
        this.withdrawalRequestDate = LocalDateTime.now();
    }

    // Getters
    public String getApplicationId() {
        return applicationId;
    }

    public User getApplicant() {
        return applicant;
    }

    public BTOProject getProject() {
        return project;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public LocalDateTime getStatusUpdateDate() {
        return statusUpdateDate;
    }

    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }

    public LocalDateTime getWithdrawalRequestDate() {
        return withdrawalRequestDate;
    }

    // Business logic methods
    public boolean isEligibleForBooking() {
        return this.status == ApplicationStatus.SUCCESSFUL;
    }

    public boolean canWithdraw() {
        return this.status == ApplicationStatus.PENDING || 
               this.status == ApplicationStatus.SUCCESSFUL;
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BTOApplication that = (BTOApplication) o;
        return Objects.equals(applicationId, that.applicationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(applicationId);
    }

    // ToString
    @Override
    public String toString() {
        return "BTOApplication{" +
                "applicationId='" + applicationId + '\'' +
                ", applicant=" + applicant.getNric() +
                ", project=" + project.getProjectName() +
                ", flatType=" + flatType +
                ", status=" + status +
                ", applicationDate=" + applicationDate +
                '}';
    }
}