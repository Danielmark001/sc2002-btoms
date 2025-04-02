package models.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HDBManager extends User {
    private List<Project> managedProjects;

    // Constructor
    public HDBManager(String nric, String name, LocalDate dateOfBirth) {
        super(nric, name, dateOfBirth, MaritalStatus.SINGLE); // Managers typically not applicants
        this.setStatus(UserStatus.MANAGER);
        this.managedProjects = new ArrayList<>();
    }

    // Method to add a managed project
    public void addManagedProject(Project project) {
        if (!managedProjects.contains(project)) {
            managedProjects.add(project);
        }
    }

    // Method to remove a managed project
    public void removeManagedProject(Project project) {
        managedProjects.remove(project);
    }

    // Get managed projects
    public List<Project> getManagedProjects() {
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

    // Specific methods for manager roles can be added here
    public boolean canCreateProject() {
        // Logic to determine if manager can create a new project
        return this.getStatus() == UserStatus.MANAGER;
    }

    public boolean canApproveApplication() {
        // Logic to determine if manager can approve applications
        return this.getStatus() == UserStatus.MANAGER;
    }
}