package view;

import controllers.OfficerRegistrationController;
import models.BTOProject;
import models.Registration;
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
    public void displayError(string message)
    
    private void displayRegistrations(List<Registration> registrations) {
        for (Registration registration : registrations) {
            System.out.println("ID: " + registration.getRegistrationId());
            System.out.println("Officer: " + registration.getOfficer().getName());
            System.out.println("Project: " + registration.getProject().getProjectName());
            System.out.println("Status: " + registration.getStatus());
            System.out.println("-------------------------");
        }
    }
    
    private BTOProject getProjectByName(String projectName) {
        // This method would normally call the controller
        // For now, returning null as a placeholder
        return null;
    }
    
    private Registration getRegistrationById(String registrationId) {
        // This method would normally call the controller
        // For now, returning null as a placeholder
        return null; 
    }
}