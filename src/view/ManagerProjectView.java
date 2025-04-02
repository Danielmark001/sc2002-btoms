package views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

import models.Project;
import models.enumeration.FlatType;
import models.manager.ProjectManager;

/**
 * View for managing projects by HDB Managers
 */
public class ManagerProjectView extends BaseView {
    private final ProjectManager projectManager;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Constructor
     * @param scanner Scanner object for user input
     */
    public ManagerProjectView(Scanner scanner) {
        super(scanner);
        this.projectManager = new ProjectManager();
    }
    
    @Override
    public void displayMenu() {
        System.out.println("\n===== MANAGE PROJECTS =====");
        System.out.println("1. View All Projects");
        System.out.println("2. View My Projects");
        System.out.println("3. Create New Project");
        System.out.println("4. Edit Project");
        System.out.println("5. Toggle Project Visibility");
        System.out.println("6. Delete Project");
        System.out.println("7. Back to Main Menu");
    }
    
    @Override
    public boolean handleRequest() {
        System.out.print("\nEnter your choice: ");
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                viewAllProjects();
                return true;
            case "2":
                viewMyProjects();
                return true;
            case "3":
                createNewProject();
                return true;
            case "4":
                editProject();
                return true;
            case "5":
                toggleProjectVisibility();
                return true;
            case "6":
                deleteProject();
                return true;
            case "7":
                return false; // Back to main menu
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }
    
    /**
     * Displays all projects in the system
     */
    private void viewAllProjects() {
        List<Project> allProjects = projectManager.getAllProjects();
        
        if (allProjects.isEmpty()) {
            System.out.println("No projects found in the system.");
            return;
        }
        
        displayProjectList(allProjects, "ALL PROJECTS");
    }
    
    /**
     * Displays projects created by the current manager
     */
    private void viewMyProjects() {
        List<Project> myProjects = projectManager.getProjectsByCurrentManager();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects.");
            return;
        }
        
        displayProjectList(myProjects, "MY PROJECTS");
    }
    
    /**
     * Helper method to display a list of projects
     * @param projects List of projects to display
     * @param title Title for the display
     */
    private void displayProjectList(List<Project> projects, String title) {
        System.out.println("\n===== " + title + " =====");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-10s | %-10s | %-10s\n", 
                         "No.", "Project Name", "Neighborhood", "2-Room", "3-Room", "Visible");
        System.out.println("---------------------------------------------------------------------------------");
        
        int index = 1;
        for (Project project : projects) {
            System.out.printf("%-5d | %-20s | %-15s | %-10d | %-10d | %-10s\n", 
                            index++, 
                            project.getProjectName(), 
                            project.getNeighborhood(),
                            project.getTotalUnits(FlatType.TWO_ROOM),
                            project.getTotalUnits(FlatType.THREE_ROOM),
                            project.isVisible() ? "Yes" : "No");
        }
        System.out.println("---------------------------------------------------------------------------------");
        
        // Display project details if user selects a project
        System.out.print("\nEnter project number to view details (or 0 to go back): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= projects.size()) {
                displayProjectDetails(projects.get(selection - 1));
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
        System.out.println("Manager in Charge: " + project.getManagerInCharge());
        System.out.println("Officer Slots: " + project.getOfficerSlots() + 
                         " (Filled: " + project.getOfficerIds().size() + ")");
        System.out.println("Visibility: " + (project.isVisible() ? "Visible" : "Hidden"));
        
        pressEnterToContinue("\nPress Enter to continue...");
    }
    
    /**
     * Handles the process of creating a new project
     */
    private void createNewProject() {
        System.out.println("\n===== CREATE NEW PROJECT =====");
        
        // Get project details
        System.out.print("Project Name: ");
        String projectName = scanner.nextLine().trim();
        if (projectName.isEmpty()) {
            System.out.println("Project name cannot be empty. Creation cancelled.");
            return;
        }
        
        System.out.print("Neighborhood: ");
        String neighborhood = scanner.nextLine().trim();
        if (neighborhood.isEmpty()) {
            System.out.println("Neighborhood cannot be empty. Creation cancelled.");
            return;
        }
        
        // Get number of units
        int twoRoomUnits = getIntInput("Number of 2-Room Units: ", 0);
        int threeRoomUnits = getIntInput("Number of 3-Room Units: ", 0);
        
        if (twoRoomUnits <= 0 && threeRoomUnits <= 0) {
            System.out.println("At least one type of unit must have a positive number of units. Creation cancelled.");
            return;
        }
        
        // Get application period
        LocalDate openingDate = getDateInput("Application Opening Date (yyyy-MM-dd): ");
        if (openingDate == null) {
            return;
        }
        
        LocalDate closingDate = getDateInput("Application Closing Date (yyyy-MM-dd): ");
        if (closingDate == null) {
            return;
        }
        
        if (closingDate.isBefore(openingDate)) {
            System.out.println("Closing date cannot be before opening date. Creation cancelled.");
            return;
        }
        
        // Get officer slots
        int officerSlots = getIntInput("Number of HDB Officer Slots: ", 1);
        if (officerSlots <= 0) {
            System.out.println("Number of officer slots must be at least 1. Creation cancelled.");
            return;
        }
        
        // Confirm creation
        System.out.println("\nYou are about to create a new project with the following details:");
        System.out.println("Project Name: " + projectName);
        System.out.println("Neighborhood: " + neighborhood);
        System.out.println("2-Room Units: " + twoRoomUnits);
        System.out.println("3-Room Units: " + threeRoomUnits);
        System.out.println("Application Period: " + openingDate + " to " + closingDate);
        System.out.println("Officer Slots: " + officerSlots);
        
        System.out.print("\nConfirm project creation? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            Project newProject = projectManager.createProject(
                projectName, neighborhood, twoRoomUnits, threeRoomUnits, 
                openingDate, closingDate, officerSlots);
            
            if (newProject != null) {
                System.out.println("\nProject created successfully!");
                System.out.println("Project ID: " + newProject.getProjectId());
                System.out.println("Note: The project is initially hidden. You can toggle its visibility later.");
            } else {
                System.out.println("\nFailed to create project. Please check if you are already handling another project during the same period.");
            }
        } else {
            System.out.println("\nProject creation cancelled.");
        }
    }
    
    /**
     * Handles the process of editing an existing project
     */
    private void editProject() {
        List<Project> myProjects = projectManager.getProjectsByCurrentManager();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects to edit.");
            return;
        }
        
        System.out.println("\n===== EDIT PROJECT =====");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-10s | %-10s | %-10s\n", 
                         "No.", "Project Name", "Neighborhood", "2-Room", "3-Room", "Visible");
        System.out.println("---------------------------------------------------------------------------------");
        
        int index = 1;
        for (Project project : myProjects) {
            System.out.printf("%-5d | %-20s | %-15s | %-10d | %-10d | %-10s\n", 
                            index++, 
                            project.getProjectName(), 
                            project.getNeighborhood(),
                            project.getTotalUnits(FlatType.TWO_ROOM),
                            project.getTotalUnits(FlatType.THREE_ROOM),
                            project.isVisible() ? "Yes" : "No");
        }
        System.out.println("---------------------------------------------------------------------------------");
        
        // Get project selection
        System.out.print("\nEnter project number to edit (or 0 to cancel): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= myProjects.size()) {
                Project projectToEdit = myProjects.get(selection - 1);
                editProjectDetails(projectToEdit);
            } else if (selection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Edits the details of a specific project
     * @param project Project to edit
     */
    private void editProjectDetails(Project project) {
        System.out.println("\n===== EDIT PROJECT: " + project.getProjectName() + " =====");
        System.out.println("(Leave field empty to keep current value)");
        
        // Get updated details
        System.out.print("Project Name [" + project.getProjectName() + "]: ");
        String projectName = scanner.nextLine().trim();
        if (!projectName.isEmpty()) {
            project.setProjectName(projectName);
        }
        
        System.out.print("Neighborhood [" + project.getNeighborhood() + "]: ");
        String neighborhood = scanner.nextLine().trim();
        if (!neighborhood.isEmpty()) {
            project.setNeighborhood(neighborhood);
        }
        
        // Get number of units
        String twoRoomInput = getStringInput("Number of 2-Room Units [" + project.getTotalUnits(FlatType.TWO_ROOM) + "]: ");
        if (!twoRoomInput.isEmpty()) {
            try {
                int twoRoomUnits = Integer.parseInt(twoRoomInput);
                if (twoRoomUnits >= project.getTotalUnits(FlatType.TWO_ROOM) - 
                    (project.getTotalUnits(FlatType.TWO_ROOM) - project.getAvailableUnits(FlatType.TWO_ROOM))) {
                    project.setTotalUnits(FlatType.TWO_ROOM, twoRoomUnits);
                } else {
                    System.out.println("Cannot reduce units below the number already allocated.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }
        
        String threeRoomInput = getStringInput("Number of 3-Room Units [" + project.getTotalUnits(FlatType.THREE_ROOM) + "]: ");
        if (!threeRoomInput.isEmpty()) {
            try {
                int threeRoomUnits = Integer.parseInt(threeRoomInput);
                if (threeRoomUnits >= project.getTotalUnits(FlatType.THREE_ROOM) - 
                    (project.getTotalUnits(FlatType.THREE_ROOM) - project.getAvailableUnits(FlatType.THREE_ROOM))) {
                    project.setTotalUnits(FlatType.THREE_ROOM, threeRoomUnits);
                } else {
                    System.out.println("Cannot reduce units below the number already allocated.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }
        
        // Get application period
        String openingDateInput = getStringInput("Application Opening Date [" + project.getOpeningDate() + "]: ");
        if (!openingDateInput.isEmpty()) {
            try {
                LocalDate openingDate = LocalDate.parse(openingDateInput, dateFormatter);
                project.setOpeningDate(openingDate);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Keeping current value.");
            }
        }
        
        String closingDateInput = getStringInput("Application Closing Date [" + project.getClosingDate() + "]: ");
        if (!closingDateInput.isEmpty()) {
            try {
                LocalDate closingDate = LocalDate.parse(closingDateInput, dateFormatter);
                if (!closingDate.isBefore(project.getOpeningDate())) {
                    project.setClosingDate(closingDate);
                } else {
                    System.out.println("Closing date cannot be before opening date. Keeping current value.");
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Keeping current value.");
            }
        }
        
        // Get officer slots
        String officerSlotsInput = getStringInput("Number of HDB Officer Slots [" + project.getOfficerSlots() + "]: ");
        if (!officerSlotsInput.isEmpty()) {
            try {
                int officerSlots = Integer.parseInt(officerSlotsInput);
                if (officerSlots >= project.getOfficerIds().size()) {
                    project.setOfficerSlots(officerSlots);
                } else {
                    System.out.println("Cannot reduce slots below the number of officers already assigned.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Keeping current value.");
            }
        }
        
        // Confirm update
        System.out.println("\nYou are about to update the project with the following details:");
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("2-Room Units: " + project.getTotalUnits(FlatType.TWO_ROOM) + 
                         " (Available: " + project.getAvailableUnits(FlatType.TWO_ROOM) + ")");
        System.out.println("3-Room Units: " + project.getTotalUnits(FlatType.THREE_ROOM) + 
                         " (Available: " + project.getAvailableUnits(FlatType.THREE_ROOM) + ")");
        System.out.println("Application Period: " + project.getOpeningDate() + " to " + project.getClosingDate());
        System.out.println("Officer Slots: " + project.getOfficerSlots() + 
                         " (Filled: " + project.getOfficerIds().size() + ")");
        
        System.out.print("\nConfirm update? (Y/N): ");
        String confirm = scanner.nextLine().trim().toUpperCase();
        
        if (confirm.equals("Y")) {
            boolean success = projectManager.updateProject(project);
            
            if (success) {
                System.out.println("\nProject updated successfully!");
            } else {
                System.out.println("\nFailed to update project. Please try again later.");
            }
        } else {
            System.out.println("\nUpdate cancelled.");
        }
    }
    
    /**
     * Handles the process of toggling project visibility
     */
    private void toggleProjectVisibility() {
        List<Project> myProjects = projectManager.getProjectsByCurrentManager();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects to toggle visibility.");
            return;
        }
        
        System.out.println("\n===== TOGGLE PROJECT VISIBILITY =====");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-10s | %-10s | %-10s\n", 
                         "No.", "Project Name", "Neighborhood", "2-Room", "3-Room", "Visible");
        System.out.println("---------------------------------------------------------------------------------");
        
        int index = 1;
        for (Project project : myProjects) {
            System.out.printf("%-5d | %-20s | %-15s | %-10d | %-10d | %-10s\n", 
                            index++, 
                            project.getProjectName(), 
                            project.getNeighborhood(),
                            project.getTotalUnits(FlatType.TWO_ROOM),
                            project.getTotalUnits(FlatType.THREE_ROOM),
                            project.isVisible() ? "Yes" : "No");
        }
        System.out.println("---------------------------------------------------------------------------------");
        
        // Get project selection
        System.out.print("\nEnter project number to toggle visibility (or 0 to cancel): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= myProjects.size()) {
                Project selectedProject = myProjects.get(selection - 1);
                
                // Confirm toggle
                boolean newVisibility = !selectedProject.isVisible();
                System.out.println("\nYou are about to change the visibility of " + selectedProject.getProjectName() + 
                                 " from " + (selectedProject.isVisible() ? "visible" : "hidden") + 
                                 " to " + (newVisibility ? "visible" : "hidden") + ".");
                
                System.out.print("Confirm visibility change? (Y/N): ");
                String confirm = scanner.nextLine().trim().toUpperCase();
                
                if (confirm.equals("Y")) {
                    boolean success = projectManager.toggleProjectVisibility(
                        selectedProject.getProjectId(), newVisibility);
                    
                    if (success) {
                        System.out.println("\nProject visibility changed successfully!");
                    } else {
                        System.out.println("\nFailed to change project visibility. Please try again later.");
                    }
                } else {
                    System.out.println("\nVisibility change cancelled.");
                }
            } else if (selection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Handles the process of deleting a project
     */
    private void deleteProject() {
        List<Project> myProjects = projectManager.getProjectsByCurrentManager();
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects to delete.");
            return;
        }
        
        System.out.println("\n===== DELETE PROJECT =====");
        System.out.println("WARNING: This operation cannot be undone!");
        System.out.println("---------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-15s | %-10s | %-10s | %-10s\n", 
                         "No.", "Project Name", "Neighborhood", "2-Room", "3-Room", "Visible");
        System.out.println("---------------------------------------------------------------------------------");
        
        int index = 1;
        for (Project project : myProjects) {
            System.out.printf("%-5d | %-20s | %-15s | %-10d | %-10d | %-10s\n", 
                            index++, 
                            project.getProjectName(), 
                            project.getNeighborhood(),
                            project.getTotalUnits(FlatType.TWO_ROOM),
                            project.getTotalUnits(FlatType.THREE_ROOM),
                            project.isVisible() ? "Yes" : "No");
        }
        System.out.println("---------------------------------------------------------------------------------");
        
        // Get project selection
        System.out.print("\nEnter project number to delete (or 0 to cancel): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= myProjects.size()) {
                Project selectedProject = myProjects.get(selection - 1);
                
                // Confirm deletion
                System.out.println("\nYou are about to permanently delete the project: " + selectedProject.getProjectName());
                System.out.println("All associated applications, registrations, and enquiries will also be deleted.");
                System.out.print("To confirm deletion, type the project name: ");
                
                String confirmName = scanner.nextLine().trim();
                
                if (confirmName.equals(selectedProject.getProjectName())) {
                    boolean success = projectManager.deleteProject(selectedProject.getProjectId());
                    
                    if (success) {
                        System.out.println("\nProject deleted successfully!");
                    } else {
                        System.out.println("\nFailed to delete project. Please try again later.");
                    }
                } else {
                    System.out.println("\nProject name did not match. Deletion cancelled.");
                }
            } else if (selection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Helper method to get integer input with validation
     * @param prompt Prompt to display
     * @param minValue Minimum valid value
     * @return Integer value or the minimum value if input is invalid
     */
    private int getIntInput(String prompt, int minValue) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            try {
                int value = Integer.parseInt(input);
                if (value >= minValue) {
                    return value;
                } else {
                    System.out.println("Value must be at least " + minValue + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    /**
     * Helper method to get date input with validation
     * @param prompt Prompt to display
     * @return LocalDate object or null if input is invalid
     */
    private LocalDate getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            try {
                return LocalDate.parse(input, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd format.");
                System.out.print("Do you want to try again? (Y/N): ");
                String retry = scanner.nextLine().trim().toUpperCase();
                if (!retry.equals("Y")) {
                    return null;
                }
            }
        }
    }
    
    /**
     * Helper method to get string input
     * @param prompt Prompt to display
     * @return String input
     */
    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}