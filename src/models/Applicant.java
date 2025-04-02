package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Applicant extends User {
    private Project bookedProject;
    private FlatType bookedFlatType;

    // Constructor
    public Applicant(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus) {
        super(nric, name, dateOfBirth, maritalStatus);
        this.setStatus(UserStatus.APPLICANT);
    }

    // Getter and setter for booked project
    public Project getBookedProject() {
        return bookedProject;
    }

    public void setBookedProject(Project bookedProject) {
        this.bookedProject = bookedProject;
    }

    // Getter and setter for booked flat type
    public FlatType getBookedFlatType() {
        return bookedFlatType;
    }

    public void setBookedFlatType(FlatType bookedFlatType) {
        this.bookedFlatType = bookedFlatType;
    }

    // Method to check if applicant is eligible for a specific flat type
    public boolean isEligibleForFlatType(FlatType flatType) {
        int age = calculateAge();
        
        switch (flatType) {
            case TWO_ROOM:
                return (this.getMaritalStatus() == MaritalStatus.SINGLE && age >= 35) ||
                       (this.getMaritalStatus() == MaritalStatus.MARRIED && age >= 21);
            case THREE_ROOM:
                return this.getMaritalStatus() == MaritalStatus.MARRIED && age >= 21;
            default:
                return false;
        }
    }

    // Method to check if applicant has an active application
    public boolean hasActiveApplication() {
        return this.getApplications().stream()
            .anyMatch(app -> app.getStatus() != ApplicationStatus.BOOKED && 
                              app.getStatus() != ApplicationStatus.UNSUCCESSFUL);
    }

    // Override toString to include applicant-specific information
    @Override
    public String toString() {
        return "Applicant{" +
                "name='" + getName() + '\'' +
                ", nric='" + getNric() + '\'' +
                ", age=" + calculateAge() +
                ", maritalStatus=" + getMaritalStatus() +
                ", bookedProject=" + (bookedProject != null ? bookedProject.getProjectName() : "None") +
                ", bookedFlatType=" + bookedFlatType +
                '}';
    }

    // Method to apply for a project
    public Application applyForProject(Project project) {
        // Check if already has an active application
        if (hasActiveApplication()) {
            throw new IllegalStateException("Applicant already has an active application");
        }

        // Validate project eligibility
        if (!project.isEligibleForApplicant(this)) {
            throw new IllegalArgumentException("Applicant is not eligible for this project");
        }

        // Create and return a new application
        Application application = new Application(this, project, determineFlatType(project));
        this.getApplications().add(application);
        return application;
    }

    // Determine flat type based on project and applicant's eligibility
    private FlatType determineFlatType(Project project) {
        // Logic to determine the appropriate flat type
        if (project.getFlatTypes().contains(FlatType.TWO_ROOM) && 
            isEligibleForFlatType(FlatType.TWO_ROOM)) {
            return FlatType.TWO_ROOM;
        } else if (project.getFlatTypes().contains(FlatType.THREE_ROOM) && 
                   isEligibleForFlatType(FlatType.THREE_ROOM)) {
            return FlatType.THREE_ROOM;
        }
        
        throw new IllegalArgumentException("No eligible flat type found");
    }
}