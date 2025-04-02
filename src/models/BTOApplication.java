package models;

import java.time.LocalDate;

public class BTOApplication {
    private User applicant;
    private Project project;
    private Project.FlatType flatType;
    private ApplicationStatus status;
    private LocalDate applicationDate;

    // Enum for application status
    public enum ApplicationStatus {
        PENDING,       // Initial status upon application
        SUCCESSFUL,    // Invited to make flat booking
        UNSUCCESSFUL,  // Cannot make flat booking
        BOOKED         // Secured a unit after successful booking
    }

    // Constructors
    public BTOApplication() {}

    public BTOApplication(User applicant, BTOProject project, Project.FlatType flatType) {
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = LocalDate.now();
    }

    // Getters and Setters
    public User getApplicant() {
        return applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project.FlatType getFlatType() {
        return flatType;
    }

    public void setFlatType(Project.FlatType flatType) {
        this.flatType = flatType;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    // Method to check if application is eligible for booking
    public boolean isEligibleForBooking() {
        return this.status == ApplicationStatus.SUCCESSFUL;
    }

    // Method to withdraw application
    public boolean canWithdraw() {
        return this.status == ApplicationStatus.PENDING || 
               this.status == ApplicationStatus.SUCCESSFUL;
    }

    @Override
    public String toString() {
        return "Application{" +
                "applicant=" + applicant.getNric() +
                ", project=" + project.getProjectName() +
                ", flatType=" + flatType +
                ", status=" + status +
                ", applicationDate=" + applicationDate +
                '}';
    }
}