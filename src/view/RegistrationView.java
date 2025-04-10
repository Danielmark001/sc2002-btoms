package view;

import controllers.OfficerRegistrationController;
import models.BTOProject;
import models.Registration;
import services.ProjectService;
import enumeration.RegistrationStatus;

import java.util.List;
import java.util.Scanner;

public class RegistrationView {
    private OfficerRegistrationController registrationController;
    private Scanner scanner;

    public RegistrationView(OfficerRegistrationController registrationController) {
        this.registrationController = registrationController;
        this.scanner = new Scanner(System.in);  
    }

    public void displayMenu() {
        System.out.println("\n===== REGISTRATION MENU =====");
        System.out.println("1. Register for Project");
        System.out.println("2. View Registration Status"); 
        System.out.println("3. Manage Registrations");
        System.out.println("4. Back");
    }

    public void handleUserInput() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> registerForProject();
            case 2 -> viewRegistrationStatus();
            case 3 -> manageRegistrations();
            case 4 -> System.out.println("Going back...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void registerForProject() {
        System.out.println("Enter project name:");
        String projectName = scanner.nextLine();
        BTOProject project = getProjectByName(projectName);

        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        boolean success = registrationController.registerForProject(project); 
        if (success) {
            System.out.println("Registration submitted successfully!"); 
        } else {
            System.out.println("Failed to submit registration.");
        }
    }

    private void viewRegistrationStatus() {
        RegistrationStatus status = registrationController.getCurrentRegistrationStatus();
        if (status != null) {
            System.out.println("Current registration status: " + status);
        } else {
            System.out.println("You have no current registrations.");
        }
    }

    private void manageRegistrations() {
        System.out.println("Enter project name:");
        String projectName = scanner.nextLine();
        BTOProject project = getProjectByName(projectName);

        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        List<Registration> pendingRegistrations = registrationController
                .getRegistrationsByStatus(project, RegistrationStatus.PENDING);

        if (pendingRegistrations.isEmpty()) {
            System.out.println("No pending registrations for this project.");
            return;
        }

        displayRegistrations(pendingRegistrations);

        System.out.println("Enter registration ID to process:");
        String registrationId = scanner.nextLine();
        Registration registration = getRegistrationById(registrationId);

        if (registration == null) {
            System.out.println("Registration not found.");
            return;
        }

        System.out.println("Enter action (approve/reject):");
        String action = scanner.nextLine().toLowerCase();

        boolean success;
        if (action.equals("approve")) {
            success = registrationController.approveRegistration(registration);
        } else if (action.equals("reject")) {
            success = registrationController.rejectRegistration(registration);
        } else {
            System.out.println("Invalid action.");
            return;
        }

        if (success) {
            System.out.println("Registration " + action + "d successfully!");
        } else {
            System.out.println("Failed to " + action + " registration.");
        }
    }

    public void displayError(String message) {
        System.out.println("Error: " + message);
    }
    public void displaySuccess(String message) {
        System.out.println("Success: " + message);
    }
    public void displayMessage(String message) {
        System.out.println(message);
    }
    
    private void displayRegistrations(List<Registration> registrations) {
        for (Registration registration : registrations) {
            System.out.println("ID: " + registration.getRegistrationId());
            System.out.println("Officer: " + registration.getOfficer().getName());
            System.out.println("Project: " + registration.getProject().getProjectName());
            System.out.println("Status: " + registration.getStatus());
            System.out.println("-------------------------");
        }
    }
/**
 * Gets a project by name
 * 
 * @param projectName Name of the project to retrieve
 * @return Project with the given name, or null if not found
 */
private BTOProject getProjectByName(String projectName) {
    if (projectName == null || projectName.trim().isEmpty()) {
        return null;
    }
    
    // Get all projects from the registration controller
    ProjectService projectService = ProjectService.getInstance();
    
    // Find project with matching name (case-insensitive)
    List<BTOProject> allProjects = projectService.getAllProjects();
    
    for (BTOProject project : allProjects) {
        if (project.getProjectName().equalsIgnoreCase(projectName)) {
            return project;
        }
    }
    
    return null;
}

/**
 * Gets a registration by ID
 * 
 * @param registrationId ID of the registration to retrieve
 * @return Registration with the given ID, or null if not found
 */
private Registration getRegistrationById(String registrationId) {
    if (registrationId == null || registrationId.trim().isEmpty()) {
        return null;
    }
    
    // Get all projects
    ProjectService projectService = ProjectService.getInstance();
    List<BTOProject> allProjects = projectService.getAllProjects();
    
    // Go through all projects to find the registration
    for (BTOProject project : allProjects) {
        List<Registration> registrations = project.getRegistrations();
        
        for (Registration registration : registrations) {
            if (registration.getRegistrationId().equals(registrationId)) {
                return registration;
            }
        }
    }
    
    return null;
}
}