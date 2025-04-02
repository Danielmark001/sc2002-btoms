package models.entity;

import java.time.LocalDate;

public class Registration {
    private User officer;
    private Project project;
    private RegistrationStatus status;
    private LocalDate registrationDate;

    // Enum for registration status
    public enum RegistrationStatus {
        PENDING,    // Initial status of registration
        APPROVED,   // Registration approved by HDB Manager
        REJECTED    // Registration rejected by HDB Manager
    }

    // Constructors
    public Registration() {}

    public Registration(User officer, Project project) {
        this.officer = officer;
        this.project = project;
        this.status = RegistrationStatus.PENDING;
        this.registrationDate = LocalDate.now();
    }

    // Getters and Setters
    public User getOfficer() {
        return officer;
    }

    public void setOfficer(User officer) {
        this.officer = officer;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    // Method to check registration eligibility
    public boolean isEligibleToRegister() {
        // Check if officer is not already an applicant for this project
        // Check if officer is not registered for another project in the same period
        return officer.getStatus() == User.UserStatus.OFFICER &&
               !isRegisteredForAnotherProject() &&
               !hasAppliedForProject();
    }

    // Helper method to check if officer is registered for another project
    private boolean isRegisteredForAnotherProject() {
        return officer.getRegistrations().stream()
                .anyMatch(reg -> reg.getStatus() == RegistrationStatus.APPROVED &&
                        !reg.getProject().equals(this.project) &&
                        isOverlappingPeriod(reg.getProject()));
    }

    // Helper method to check if officer has applied for the project
    private boolean hasAppliedForProject() {
        return officer.getApplications().stream()
                .anyMatch(app -> app.getProject().equals(this.project));
    }

    // Helper method to check overlapping application periods
    private boolean isOverlappingPeriod(Project otherProject) {
        LocalDate currentProjectStart = project.getApplicationOpeningDate();
        LocalDate currentProjectEnd = project.getApplicationClosingDate();
        LocalDate otherProjectStart = otherProject.getApplicationOpeningDate();
        LocalDate otherProjectEnd = otherProject.getApplicationClosingDate();

        return !(currentProjectEnd.isBefore(otherProjectStart) || 
                 currentProjectStart.isAfter(otherProjectEnd));
    }

    @Override
    public String toString() {
        return "Registration{" +
                "officer=" + officer.getNric() +
                ", project=" + project.getProjectName() +
                ", status=" + status +
                ", registrationDate=" + registrationDate +
                '}';
    }
}