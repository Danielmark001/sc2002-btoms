package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import enumeration.MaritalStatus;
import enumeration.UserStatus;
import enumeration.UserType;
import models.BTOProject;

public class HDBManager extends User {
    private List<BTOProject> managedProjects;

    // Constructor
    public HDBManager(String nric, String name, LocalDate dateOfBirth) {
        super(nric, name, dateOfBirth, MaritalStatus.SINGLE); // Managers typically not applicants
        this.managedProjects = new ArrayList<>();
    }

    public HDBManager(String nric, String password, int age, MaritalStatus maritalStatus) {
        super(nric, password, age, maritalStatus);
        this.managedProjects = new ArrayList<>();
    };

    // Method to add a managed project
    public void addManagedProject(BTOProject project) {
        if (!managedProjects.contains(project)) {
            managedProjects.add(project);
        }
    }

    // Method to remove a managed project
    public void removeManagedProject(BTOProject project) {
        managedProjects.remove(project);
    }

    // Get managed projects
    public List<BTOProject> getManagedProjects() {
        return new ArrayList<>(managedProjects);
    }

    // Override toString to include manager-specific information
    @Override
    public String toString() {
        return "HDBManager{" +
                "name='" + getName() + '\'' +
                ", nric='" + getNric() + '\'' +
                ", managedProjects=" + managedProjects.size() +
                '}';
    }

    

    @Override
    public MaritalStatus getMaritalStatus() {
        // TODO Auto-generated method stub
        return super.getMaritalStatus();
    }
    
}