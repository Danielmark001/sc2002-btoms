package views;

import java.util.List;
import java.util.Scanner;

import models.Project;
import models.User;
import models.enumeration.FlatType;
import models.manager.ApplicationManager;
import models.manager.ProjectManager;
import models.manager.UserManager;

/**
 * View for displaying and handling project-related operations for applicants
 */
public class ProjectView extends BaseView {
    private final ProjectManager projectManager;
    private final ApplicationManager applicationManager;
    private final UserManager userManager;
    
    /**
     * Constructor
     * @param scanner Scanner object for user input
     */
    public ProjectView(Scanner scanner) {
        super(scanner);
        this.projectManager = new ProjectManager();
        this.applicationManager = new ApplicationManager();
        this.userManager = new UserManager();
    }
    
    @Override
    public void displayMenu() {
        System.out.println("\n===== PROJECTS =====");
        System.out.println("1. View All Eligible Projects");
        System.out.println("2. Apply for a Project");
        System.out.println("3. Register as an HDB Officer for a Project (for HDB Officers only)");
        System.out.println("4. View Registration Status (for HDB Officers only)");
        System.out.println("5. Back to Main Menu");
    }
    
    @Override
    public boolean handleRequest() {
        System.out.print("\nEnter your choice: ");
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                viewEligibleProjects();
                return true;
            case "2":
                applyForProject();
                return true;
            case "3":
                if (loginController.isHdbOfficer()) {
                    registerAsOfficer();
                } else {
                    System.out.println("This option is only available for HDB Officers.");
                }
                return true;
            case "4":
                if (loginController.isHdbOfficer()) {
                    viewRegistrationStatus();
                } else {
                    System.out.println("This option is only available for HDB Officers.");
                }
                return true;
            case "5":
                return false; // Back to main menu
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }
    
    /**
     * Displays all projects that the current user is eligible to apply for
     */
    private void viewEligibleProjects() {
        List<Project> eligibleProjects = projectManager.getEligibleProjects();
        
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects found.");
            return;
        }
        
        System.out.println("\n===== ELIGIBLE PROJECTS =====");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-10s | %-10s\n", 
                         "No.", "Project Name", "Neighborhood", "2-Room", "3-Room");
        System.out.println("------------------------------------------------------------");
        
        int index = 1;
        for (Project project : eligibleProjects) {
            System.out.printf("%-5d | %-20s | %-15s | %-10d | %-10d\n", 
                            index++, 
                            project.getProjectName(), 
                            project.getNeighborhood(),
                            project.getAvailableUnits(FlatType.TWO_ROOM),
                            project.getAvailableUnits(FlatType.THREE_ROOM));
        }
        System.out.println("------------------------------------------------------------");
        
        // Display project details if user selects a project
        System.out.print("\nEnter project number to view details (or 0 to go back): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= eligibleProjects.size()) {
                displayProjectDetails(eligibleProjects.get(selection - 1));
            } else if (selection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Displays detailed information about a specific project
     * @param project Project to display
     */
    private void displayProjectDetails(Project project) {
        System.out.println("\n===== PROJECT DETAILS =====");
        System.out.println("Project ID: " + project.getProjectId());
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("2-Room Units: " + project.getTotalUnits(FlatType.TWO_ROOM) + 
                         " (Available: " + project.getAvailableUnits(FlatType.TWO_ROOM) + ")");
        System.out.println("3-Room Units: " + project.getTotalUnits(FlatType.THREE_ROOM) + 
                         " (Available: " + project.getAvailableUnits(FlatType.THREE_ROOM) + ")");
        System.out.println("Application Period: " + project.getOpeningDate() + " to " + project.getClosingDate());
        
        pressEnterToContinue("\nPress Enter to continue...");
    }
    
    /**
     * Handles the process of applying for a project
     */
    private void applyForProject() {
        User currentUser = getCurrentUser();
        
        // Check if user already has an application
        if (currentUser.hasApplied()) {
            System.out.println("You already have an active application for project: " + currentUser.getAppliedProjectId());
            return;
        }
        
        List<Project> eligibleProjects = projectManager.getEligibleProjects();
        
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects found.");
            return;
        }
        
        System.out.println("\n===== APPLY FOR PROJECT =====");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-10s | %-10s\n", 
                         "No.", "Project Name", "Neighborhood", "2-Room", "3-Room");
        System.out.println("------------------------------------------------------------");
        
        int index = 1;
        for (Project project : eligibleProjects) {
            System.out.printf("%-5d | %-20s | %-15s | %-10d | %-10d\n", 
                            index++, 
                            project.getProjectName(), 
                            project.getNeighborhood(),
                            project.getAvailableUnits(FlatType.TWO_ROOM),
                            project.getAvailableUnits(FlatType.THREE_ROOM));
        }
        System.out.println("------------------------------------------------------------");
        
        // Get user selection
        System.out.print("\nEnter project number to apply (or 0 to cancel): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= eligibleProjects.size()) {
                Project selectedProject = eligibleProjects.get(selection - 1);
                
                // Check user eligibility for flat types
                boolean canApplyForTwoRoom = false;
                boolean canApplyForThreeRoom = false;
                
                if (currentUser.getMaritalStatus().equals("SINGLE") && currentUser.getAge() >= 35) {
                    canApplyForTwoRoom = selectedProject.getAvailableUnits(FlatType.TWO_ROOM) > 0;
                } else if (currentUser.getMaritalStatus().equals("MARRIED") && currentUser.getAge() >= 21) {
                    canApplyForTwoRoom = selectedProject.getAvailableUnits(FlatType.TWO_ROOM) > 0;
                    canApplyForThreeRoom = selectedProject.getAvailableUnits(FlatType.THREE_ROOM) > 0;
                }
                
                // Get flat type selection
                FlatType selectedFlatType = null;
                if (canApplyForTwoRoom && canApplyForThreeRoom) {
                    System.out.println("\nSelect flat type:");
                    System.out.println("1. 2-Room");
                    System.out.println("2. 3-Room");
                    System.out.print("Enter your choice: ");
                    
                    String flatTypeChoice = scanner.nextLine().trim();
                    if (flatTypeChoice.equals("1")) {
                        selectedFlatType = FlatType.TWO_ROOM;
                    } else if (flatTypeChoice.equals("2")) {
                        selectedFlatType = FlatType.THREE_ROOM;
                    } else {
                        System.out.println("Invalid choice. Application cancelled.");
                        return;
                    }
                } else if (canApplyForTwoRoom) {
                    selectedFlatType = FlatType.TWO_ROOM;
                    System.out.println("\nYou are eligible for 2-Room flats only.");
                } else if (canApplyForThreeRoom) {
                    selectedFlatType = FlatType.THREE_ROOM;
                    System.out.println("\nYou are eligible for 3-Room flats only.");
                } else {
                    System.out.println("\nSorry, you are not eligible for any flat types in this project.");
                    return;
                }
                
                // Confirm application
                System.out.println("\nYou are about to apply for a " + selectedFlatType.getDisplayName() + 
                                " flat in " + selectedProject.getProjectName() + ".");
                System.out.print("Confirm application? (Y/N): ");
                
                String confirm = scanner.nextLine().trim().toUpperCase();
                if (confirm.equals("Y")) {
                    boolean success = applicationManager.createApplication(
                        currentUser.getNric(), selectedProject.getProjectId(), selectedFlatType);
                    
                    if (success) {
                        System.out.println("\nApplication submitted successfully! Your application is now pending approval.");
                    } else {
                        System.out.println("\nFailed to submit application. Please try again later.");
                    }
                } else {
                    System.out.println("\nApplication cancelled.");
                }
            } else if (selection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Handles the process of registering as an HDB Officer for a project
     */
    private void registerAsOfficer() {
        // Get projects that don't have the current user as an officer
        List<Project> availableProjects = projectManager.getAllProjects();
        User currentUser = getCurrentUser();
        
        if (availableProjects.isEmpty()) {
            System.out.println("No projects available for registration.");
            return;
        }
        
        System.out.println("\n===== REGISTER AS HDB OFFICER =====");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-10s | %-10s\n", 
                         "No.", "Project Name", "Neighborhood", "Officer Slots", "Available Slots");
        System.out.println("------------------------------------------------------------");
        
        int index = 1;
        for (Project project : availableProjects) {
            // Skip projects where user is already an officer or has applied
            if (project.getOfficerIds().contains(currentUser.getNric()) ||
                (currentUser.getAppliedProjectId() != null && 
                 currentUser.getAppliedProjectId().equals(project.getProjectId()))) {
                continue;
            }
            
            int availableSlots = project.getOfficerSlots() - project.getOfficerIds().size();
            
            if (availableSlots > 0) {
                System.out.printf("%-5d | %-20s | %-15s | %-13d | %-15d\n", 
                                index++, 
                                project.getProjectName(), 
                                project.getNeighborhood(),
                                project.getOfficerSlots(),
                                availableSlots);
            }
        }
        System.out.println("------------------------------------------------------------");
        
        // Get user selection
        System.out.print("\nEnter project number to register as an officer (or 0 to cancel): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= availableProjects.size()) {
                Project selectedProject = availableProjects.get(selection - 1);
                
                // Confirm registration
                System.out.println("\nYou are about to register as an HDB Officer for " + 
                                 selectedProject.getProjectName() + ".");
                System.out.println("Note: Registration is subject to approval by the HDB Manager in charge.");
                System.out.print("Confirm registration? (Y/N): ");
                
                String confirm = scanner.nextLine().trim().toUpperCase();
                if (confirm.equals("Y")) {
                    boolean success = projectManager.registerAsOfficer(selectedProject.getProjectId());
                    
                    if (success) {
                        System.out.println("\nRegistration submitted successfully! Your registration is now pending approval.");
                    } else {
                        System.out.println("\nFailed to submit registration. Please check if you meet all requirements.");
                    }
                } else {
                    System.out.println("\nRegistration cancelled.");
                }
            } else if (selection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Displays the status of the current user's HDB Officer registrations
     */
    private void viewRegistrationStatus() {
        User currentUser = getCurrentUser();
        List<ProjectRegistrationStatus> registrations = projectManager.getOfficerRegistrationsByUser(currentUser.getNric());
        
        if (registrations.isEmpty()) {
            System.out.println("You have no officer registrations.");
            return;
        }
        
        System.out.println("\n===== OFFICER REGISTRATION STATUS =====");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-10s\n", 
                         "No.", "Project Name", "Neighborhood", "Status");
        System.out.println("------------------------------------------------------------");
        
        int index = 1;
        for (ProjectRegistrationStatus reg : registrations) {
            System.out.printf("%-5d | %-20s | %-15s | %-10s\n", 
                            index++, 
                            reg.getProjectName(), 
                            reg.getNeighborhood(),
                            reg.getStatus());
        }
        System.out.println("------------------------------------------------------------");
        
        pressEnterToContinue("\nPress Enter to continue...");
    }
    
    /**
     * Inner class to hold project registration status information
     */
    private class ProjectRegistrationStatus {
        private String projectName;
        private String neighborhood;
        private String status;
        
        public ProjectRegistrationStatus(String projectName, String neighborhood, String status) {
            this.projectName = projectName;
            this.neighborhood = neighborhood;
            this.status = status;
        }
        
        public String getProjectName() {
            return projectName;
        }
        
        public String getNeighborhood() {
            return neighborhood;
        }
        
        public String getStatus() {
            return status;
        }
    }
}