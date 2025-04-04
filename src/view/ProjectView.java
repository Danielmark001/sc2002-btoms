package view;

import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

import controllers.ProjectController;
import controllers.ApplicationController;

import models.BTOProject;
import models.Registration;
import models.User;
import enumeration.FlatType;
import enumeration.MaritalStatus;


/**
 * View for displaying and handling project-related operations for applicants
 */
public class ProjectView extends BaseView {
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    
    /**
     * Constructor
     * @param scanner Scanner object for user input
     */
    public ProjectView(Scanner scanner) {
        super(scanner);
        this.projectController = new ProjectController(this);
        this.applicationController = new ApplicationController(null);
    }
    
    /**
     * Display error message
     * @param message Error message to display
     */
    public void displayError(String message) {
        System.out.println("ERROR: " + message);
    }
    
    /**
     * Display success message
     * @param message Success message to display
     */
    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }
    
    /**
     * Display information message
     * @param message Information message to display
     */
    public void displayInfo(String message) {
        System.out.println("INFO: " + message);
    }
    
    /**
     * Display warning message
     * @param message Warning message to display
     */
    public void displayWarning(String message) {
        System.out.println("WARNING: " + message);
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
        List<BTOProject> eligibleProjects = projectController.getEligibleProjects();
        
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
        for (BTOProject project : eligibleProjects) {
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
    private void displayProjectDetails(BTOProject project) {
        System.out.println("\n===== PROJECT DETAILS =====");
        System.out.println("Project ID: " + project.getProjectId());
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("2-Room Units: " + project.getTotalUnits(FlatType.TWO_ROOM) + 
                         " (Available: " + project.getAvailableUnits(FlatType.TWO_ROOM) + ")");
        System.out.println("3-Room Units: " + project.getTotalUnits(FlatType.THREE_ROOM) + 
                         " (Available: " + project.getAvailableUnits(FlatType.THREE_ROOM) + ")");
        System.out.println("Application Period: " + project.getApplicationOpeningDate() + " to " + project.getApplicationClosingDate());
        
        // Additional information about project status
        if (project.isOpenForApplications()) {
            System.out.println("Status: Open for applications");
        } else if (project.isInApplicationPeriod()) {
            System.out.println("Status: In application period but not visible");
        } else if (project.getApplicationOpeningDate().isAfter(java.time.LocalDate.now())) {
            System.out.println("Status: Opening in the future");
        } else {
            System.out.println("Status: Application period closed");
        }
        
        System.out.println("Manager in Charge: " + 
            (project.getHdbManager() != null ? project.getHdbManager().getName() : "Not assigned"));
        
        System.out.println("\n");
        scanner.nextLine();
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
        
        List<BTOProject> eligibleProjects = projectController.getEligibleProjects();
        
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
        for (BTOProject project : eligibleProjects) {
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
                BTOProject selectedProject = eligibleProjects.get(selection - 1);
                
                // Check user eligibility for flat types
                boolean canApplyForTwoRoom = false;
                boolean canApplyForThreeRoom = false;
                
                int age = currentUser.calculateAge();
                MaritalStatus maritalStatus = currentUser.getMaritalStatus();
                
                if (maritalStatus == MaritalStatus.SINGLE && age >= 35) {
                    canApplyForTwoRoom = selectedProject.getAvailableUnits(FlatType.TWO_ROOM) > 0;
                } else if (maritalStatus == MaritalStatus.MARRIED && age >= 21) {
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
                System.out.println("\nYou are about to apply for a " + selectedFlatType.toString() + 
                                " flat in " + selectedProject.getProjectName() + ".");
                System.out.print("Confirm application? (Y/N): ");
                
                String confirm = scanner.nextLine().trim().toUpperCase();
                if (confirm.equals("Y")) {
                    boolean success = applicationController.applyForProject(selectedProject);
                    
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
        List<BTOProject> availableProjects = projectController.getAllProjects();
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
        
        List<BTOProject> eligibleProjects = new ArrayList<>();
        int index = 1;
        
        for (BTOProject project : availableProjects) {
            // Skip projects user has applied for
            if (currentUser.getAppliedProjectId() != null && 
                currentUser.getAppliedProjectId().equals(project.getProjectId())) {
                continue;
            }
            
            // Skip projects where officer is already registered
            boolean alreadyRegistered = false;
            for (Registration reg : currentUser.getRegistrations()) {
                if (reg.getProject().equals(project)) {
                    alreadyRegistered = true;
                    break;
                }
            }
            
            if (alreadyRegistered) {
                continue;
            }
            
            int availableSlots = project.getAvailableHDBOfficerSlots();
            
            if (availableSlots > 0) {
                eligibleProjects.add(project);
                System.out.printf("%-5d | %-20s | %-15s | %-13d | %-15d\n", 
                                index++, 
                                project.getProjectName(), 
                                project.getNeighborhood(),
                                project.getOfficerSlots(),
                                availableSlots);
            }
        }
        System.out.println("------------------------------------------------------------");
        
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects found for registration.");
            return;
        }
        
        // Get user selection
        System.out.print("\nEnter project number to register as an officer (or 0 to cancel): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= eligibleProjects.size()) {
                BTOProject selectedProject = eligibleProjects.get(selection - 1);
                
                // Confirm registration
                System.out.println("\nYou are about to register as an HDB Officer for " + 
                                 selectedProject.getProjectName() + ".");
                System.out.println("Note: Registration is subject to approval by the HDB Manager in charge.");
                System.out.print("Confirm registration? (Y/N): ");
                
                String confirm = scanner.nextLine().trim().toUpperCase();
                if (confirm.equals("Y")) {
                    boolean success = projectController.registerAsOfficer(selectedProject.getProjectId());
                    
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
        List<Registration> registrations = projectController.getOfficerRegistrationsByUser(currentUser.getNric());
        
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
        for (Registration reg : registrations) {
            System.out.printf("%-5d | %-20s | %-15s | %-10s\n", 
                            index++, 
                            reg.getProject().getProjectName(), 
                            reg.getProject().getNeighborhood(),
                            reg.getStatus());
        }
        System.out.println("------------------------------------------------------------");
        
        System.out.println("\n");
        scanner.nextLine();
    }
    
    /**
     * Filters projects by neighborhood
     * @param projects List of projects to filter
     * @param neighborhood Neighborhood to filter by
     * @return Filtered list of projects
     */
    public List<BTOProject> filterProjectsByNeighborhood(List<BTOProject> projects, String neighborhood) {
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            return projects;
        }
        
        List<BTOProject> filteredProjects = new ArrayList<>();
        for (BTOProject project : projects) {
            if (project.getNeighborhood().equalsIgnoreCase(neighborhood)) {
                filteredProjects.add(project);
            }
        }
        
        return filteredProjects;
    }
    
    /**
     * Filters projects by flat type
     * @param projects List of projects to filter
     * @param flatType Flat type to filter by
     * @return Filtered list of projects
     */
    public List<BTOProject> filterProjectsByFlatType(List<BTOProject> projects, FlatType flatType) {
        if (flatType == null) {
            return projects;
        }
        
        List<BTOProject> filteredProjects = new ArrayList<>();
        for (BTOProject project : projects) {
            if (project.getAvailableUnits(flatType) > 0) {
                filteredProjects.add(project);
            }
        }
        
        return filteredProjects;
    }
    
    /**
     * Sorts projects by flat availability (descending)
     * @param projects List of projects to sort
     * @param flatType Flat type to sort by
     * @return Sorted list of projects
     */
    public List<BTOProject> sortProjectsByAvailability(List<BTOProject> projects, FlatType flatType) {
        if (flatType == null) {
            return projects;
        }
        
        List<BTOProject> sortedProjects = new ArrayList<>(projects);
        sortedProjects.sort((p1, p2) -> Integer.compare(
            p2.getAvailableUnits(flatType), 
            p1.getAvailableUnits(flatType)
        ));
        
        return sortedProjects;
    }
    
    /**
     * Display formatted data for a list of projects
     * @param projects List of projects to display
     * @param title Title for the display
     */
    public void displayProjects(List<BTOProject> projects, String title) {
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }
        
        System.out.println("\n===== " + title + " =====");
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-10s | %-10s\n", 
                        "No.", "Project Name", "Neighborhood", "2-Room", "3-Room");
        System.out.println("------------------------------------------------------------");
        
        int index = 1;
        for (BTOProject project : projects) {
            System.out.printf("%-5d | %-20s | %-15s | %-10d | %-10d\n", 
                            index++, 
                            project.getProjectName(), 
                            project.getNeighborhood(),
                            project.getAvailableUnits(FlatType.TWO_ROOM),
                            project.getAvailableUnits(FlatType.THREE_ROOM));
        }
        System.out.println("------------------------------------------------------------");
    }
    
    /**
     * Gets a list of unique neighborhoods from the available projects
     * @return List of neighborhood names
     */
    public List<String> getAvailableNeighborhoods() {
        List<BTOProject> projects = projectController.getAllProjects();
        List<String> neighborhoods = new ArrayList<>();
        
        for (BTOProject project : projects) {
            String neighborhood = project.getNeighborhood();
            if (!neighborhoods.contains(neighborhood)) {
                neighborhoods.add(neighborhood);
            }
        }
        
        return neighborhoods;
    }
    
    /**
     * Implementation of run method from BaseView
     * Displays menu and handles user input until user chooses to exit
     * @return true to continue program execution, false to exit
     */
    @Override
    public boolean run() {
        displayMenu();
        boolean continueRunning = true;
        
        while (continueRunning) {
            continueRunning = handleRequest();
            
            if (continueRunning) {
                System.out.print("\nDo you want to perform another operation? (Y/N): ");
                String choice = scanner.nextLine().trim().toUpperCase();
                if (!choice.equals("Y")) {
                    continueRunning = false;
                } else {
                    displayMenu();
                }
            }
        }
        
        return true;
    }
}