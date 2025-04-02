package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HDBOfficer extends User {
    private Project handlingProject;
    private List<Application> processedApplications;

    // Constructor
    public HDBOfficer(String nric, String name, LocalDate dateOfBirth) {
        super(nric, name, dateOfBirth, MaritalStatus.SINGLE); // Officers typically not applicants
        this.setStatus(UserStatus.OFFICER);
        this.processedApplications = new ArrayList<>();
    }

    // Getter and setter for handling project
    public Project getHandlingProject() {
        return handlingProject;
    }

    public void setHandlingProject(Project project) {
        this.handlingProject = project;
    }

    // Methods for managing processed applications
    public void addProcessedApplication(Application application) {
        if (!processedApplications.contains(application)) {
            processedApplications.add(application);
        }
    }

    public List<Application> getProcessedApplications() {
        return new ArrayList<>(processedApplications);
    }

    // Override toString to include officer-specific information
    @Override
    public String toString() {
        return "HDBOfficer{" +
                "name='" + getName() + '\'' +
                ", nric='" + getNric() + '\'' +
                ", handlingProject=" + (handlingProject != null ? handlingProject.getProjectName() : "None") +
                ", processedApplications=" + processedApplications.size() +
                '}';
    }

    // Specific methods for officer roles
    public boolean canProcessApplication() {
        // Logic to determine if officer can process applications
        return this.getStatus() == UserStatus.OFFICER && handlingProject != null;
    }

    public boolean canBookFlat() {
        // Logic to determine if officer can book a flat
        return this.getStatus() == UserStatus.OFFICER && handlingProject != null;
    }
}