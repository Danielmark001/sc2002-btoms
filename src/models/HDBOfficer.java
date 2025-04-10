package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import enumeration.MaritalStatus;


public class HDBOfficer extends User {
    private BTOProject handlingProject;
    private List<BTOApplication> processedApplications;

    // Constructor
    public HDBOfficer(String nric, String name, LocalDate dateOfBirth) {
        super(nric, name, dateOfBirth, MaritalStatus.SINGLE); // Officers typically not applicants

        this.processedApplications = new ArrayList<>();
    }

    public HDBOfficer(String nric, String password, int age, MaritalStatus maritalStatus) {
        super(nric, password, age, maritalStatus);
        this.processedApplications = new ArrayList<>();
    };

    // Getter and setter for handling project
    public BTOProject getHandlingProject() {
        return handlingProject;
    }

    public void setHandlingProject(BTOProject project) {
        this.handlingProject = project;
    }

    // Methods for managing processed applications
    public void addProcessedApplication(BTOApplication application) {
        if (!processedApplications.contains(application)) {
            processedApplications.add(application);
        }
    }

    public List<BTOApplication> getProcessedApplications() {
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