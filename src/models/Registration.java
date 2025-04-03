package models;

import java.time.LocalDate;


public class Registration {
    private User officer;
    private BTOProject project;
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

    public Registration(User officer, BTOProject project) {
        this.officer = officer;
        this.project = project;
        this.status = RegistrationStatus.PENDING;
        this.registrationDate = LocalDate.now();
        generateRegistrationId();
    }

    // Getters and Setters
    public User getOfficer() {
        return officer;
    }

    public void setOfficer(User officer) {
        this.officer = officer;
    }

    public BTOProject getProject() {
        return project;
    }

    public void setProject(BTOProject project) {
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
    private boolean isOverlappingPeriod(BTOProject otherProject) {
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
   
 
private String registrationId;

/**
 * Gets the registration ID
 * @return Registration ID
 */
public String getRegistrationId() {
    return registrationId;
}

/**
 * Sets the registration ID
 * @param registrationId Registration ID
 */
public void setRegistrationId(String registrationId) {
    this.registrationId = registrationId;
}

/**
 * Generates a unique registration ID
 */
private void generateRegistrationId() {
    this.registrationId = "REG-" + System.currentTimeMillis() + "-" + 
                          officer.getNric().substring(1, 5);
}

public boolean isEligible() {
    // If Registration ID alrea dy exists, it's already registered
    if (registrationId != null && !registrationId.isEmpty()) {
        return false;
    }
    
    // Check if officer is valid
    if (officer == null || !(officer instanceof HDBOfficer)) {
        return false;
    }
    
    // Check if project is valid
    if (project == null) {
        return false;
    }

    // Check if officer has already applied for this project
    if (hasAppliedForProject()) {
        return false;
    }

    // Check if officer is already registered for another project in the same period
    if (isRegisteredForAnotherProject()) {
        return false;
    }

    // Check if project has available officer slots
    return project.getAvailableHDBOfficerSlots() > 0;
}


}