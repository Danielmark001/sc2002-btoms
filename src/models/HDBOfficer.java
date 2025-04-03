package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import enumeration.MaritalStatus;
import enumeration.UserStatus;
import enumeration.FlatType;
import models.BTOProject;
import enumeration.UserType;
import enumeration.ApplicationStatus;

public class HDBOfficer extends User {
    private BTOProject handlingProject;
    private List<Application> processedApplications;

    // Constructor
    public HDBOfficer(String nric, String name, LocalDate dateOfBirth) {
        super(nric, name, dateOfBirth, MaritalStatus.SINGLE); // Officers typically not applicants

        this.processedApplications = new ArrayList<>();
    }

    // Getter and setter for handling project
    public BTOProject getHandlingProject() {
        return handlingProject;
    }

    public void setHandlingProject(BTOProject project) {
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

    // Constructor for HDBOfficer
    public HDBOfficer(String nric, String name, LocalDate dateOfBirth, MaritalStatus maritalStatus) {
        super(nric, name, dateOfBirth, maritalStatus);
        this.processedApplications = new ArrayList<>();
    }


}