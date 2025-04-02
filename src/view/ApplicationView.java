package view;

import java.util.List;
import java.util.Scanner;

import models.Application;
import models.BTOProject;
import models.User;
import enumeration.ApplicationStatus;
import enumeration.FlatType;
import manager.ApplicationManager;
import manager.ProjectManager;
import manager.UserManager;

/**
 * View for handling application-related operations for applicants
 */
public class ApplicationView extends BaseView {
    private final ApplicationManager applicationManager;
    private final ProjectManager projectManager;
    private final UserManager userManager;
    
    /**
     * Constructor
     * @param scanner Scanner object for user input
     */
    public ApplicationView(Scanner scanner) {
        super(scanner);
        this.applicationManager = new ApplicationManager();
        this.projectManager = new ProjectManager();
        this.userManager = new UserManager();
    }
    
    @Override
    public void displayMenu() {
        System.out.println("\n===== BTO APPLICATIONS =====");
        System.out.println("1. View My Applications");
        System.out.println("2. Request Withdrawal");
        
        if (loginController.isHdbOfficer()) {
            System.out.println("3. Process Flat Booking (for HDB Officers only)");
        }
        
        System.out.println("4. Back to Main Menu");
    }
    
    @Override
    public boolean handleRequest() {
        System.out.print("\nEnter your choice: ");
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                viewMyApplications();
                return true;
            case "2":
                requestWithdrawal();
                return true;
            case "3":
                if (loginController.isHdbOfficer()) {
                    processBooking();
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
                return true;
            case "4":
                return false; // Back to main menu
            default:
                System.out.println("Invalid choice. Please try again.");
                return true;
        }
    }
    
    /**
     * Displays all applications made by the current user
     */
    private void viewMyApplications() {
        User currentUser = getCurrentUser();
        List<Application> applications = applicationManager.getApplicationsByUser(currentUser.getNric());
        
        if (applications.isEmpty()) {
            System.out.println("You have no applications.");
            return;
        }
        
        System.out.println("\n===== MY APPLICATIONS =====");
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-10s | %-15s | %-15s\n", 
                         "No.", "Project Name", "Flat Type", "Status", "Application Date");
        System.out.println("---------------------------------------------------------------------------");
        
        int index = 1;
        for (Application app : applications) {
            Project project = projectManager.getProjectById(app.getProjectId());
            String projectName = (project != null) ? project.getProjectName() : "N/A";
            
            System.out.printf("%-5d | %-20s | %-10s | %-15s | %-15s\n", 
                            index++, 
                            projectName, 
                            app.getFlatType().getDisplayName(),
                            app.getStatus().getDisplayName(),
                            app.getApplicationDate());
        }
        System.out.println("---------------------------------------------------------------------------");
        
        // Display application details if user selects an application
        System.out.print("\nEnter application number to view details (or 0 to go back): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= applications.size()) {
                displayApplicationDetails(applications.get(selection - 1));
            } else if (selection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Displays detailed information about a specific application
     * @param application Application to display
     */
    private void displayApplicationDetails(Application application) {
        Project project = projectManager.getProjectById(application.getProjectId());
        String projectName = (project != null) ? project.getProjectName() : "N/A";
        String neighborhood = (project != null) ? project.getNeighborhood() : "N/A";
        
        System.out.println("\n===== APPLICATION DETAILS =====");
        System.out.println("Application ID: " + application.getApplicationId());
        System.out.println("Project Name: " + projectName);
        System.out.println("Neighborhood: " + neighborhood);
        System.out.println("Flat Type: " + application.getFlatType().getDisplayName());
        System.out.println("Status: " + application.getStatus().getDisplayName());
        System.out.println("Application Date: " + application.getApplicationDate());
        System.out.println("Last Status Update: " + application.getStatusUpdateDate());
        
        if (application.isWithdrawalRequested()) {
            System.out.println("Withdrawal Requested: Yes");
        }
        
        pressEnterToContinue("\nPress Enter to continue...");
    }
    
    /**
     * Handles the process of requesting withdrawal for an application
     */
    private void requestWithdrawal() {
        User currentUser = getCurrentUser();
        List<Application> applications = applicationManager.getApplicationsByUser(currentUser.getNric());
        
        if (applications.isEmpty()) {
            System.out.println("You have no applications to withdraw.");
            return;
        }
        
        System.out.println("\n===== REQUEST WITHDRAWAL =====");
        System.out.println("---------------------------------------------------------------------------");
        System.out.printf("%-5s | %-20s | %-10s | %-15s | %-15s\n", 
                         "No.", "Project Name", "Flat Type", "Status", "Application Date");
        System.out.println("---------------------------------------------------------------------------");
        
        int index = 1;
        for (Application app : applications) {
            // Skip applications that already have withdrawal requested
            if (app.isWithdrawalRequested()) {
                continue;
            }
            
            Project project = projectManager.getProjectById(app.getProjectId());
            String projectName = (project != null) ? project.getProjectName() : "N/A";
            
            System.out.printf("%-5d | %-20s | %-10s | %-15s | %-15s\n", 
                            index++, 
                            projectName, 
                            app.getFlatType().getDisplayName(),
                            app.getStatus().getDisplayName(),
                            app.getApplicationDate());
        }
        System.out.println("---------------------------------------------------------------------------");
        
        // Get user selection
        System.out.print("\nEnter application number to request withdrawal (or 0 to cancel): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            
            if (selection > 0 && selection <= applications.size()) {
                Application selectedApp = applications.get(selection - 1);
                
                // Confirm withdrawal request
                System.out.println("\nYou are about to request withdrawal for your application.");
                System.out.println("Note: Withdrawal requests are subject to approval by HDB Management.");
                System.out.print("Confirm withdrawal request? (Y/N): ");
                
                String confirm = scanner.nextLine().trim().toUpperCase();
                if (confirm.equals("Y")) {
                    boolean success = applicationManager.requestWithdrawal(selectedApp.getApplicationId());
                    
                    if (success) {
                        System.out.println("\nWithdrawal request submitted successfully! Your request is now pending approval.");
                    } else {
                        System.out.println("\nFailed to submit withdrawal request. Please try again later.");
                    }
                } else {
                    System.out.println("\nWithdrawal request cancelled.");
                }
            } else if (selection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Handles the process of booking a flat for a successful application
     * Only available for HDB Officers
     */
    private void processBooking() {
        // Get projects that the officer is in charge of
        User currentUser = getCurrentUser();
        List<Project> officerProjects = projectManager.getProjectsByOfficer(currentUser.getNric());
        
        if (officerProjects.isEmpty()) {
            System.out.println("You are not in charge of any projects.");
            return;
        }
        
        System.out.println("\n===== PROCESS FLAT BOOKING =====");
        System.out.println("Select a project to process bookings for:");
        System.out.println("----------------------------------");
        System.out.printf("%-5s | %-20s | %-15s\n", "No.", "Project Name", "Neighborhood");
        System.out.println("----------------------------------");
        
        int index = 1;
        for (Project project : officerProjects) {
            System.out.printf("%-5d | %-20s | %-15s\n", 
                            index++, 
                            project.getProjectName(), 
                            project.getNeighborhood());
        }
        System.out.println("----------------------------------");
        
        // Get project selection
        System.out.print("\nEnter project number (or 0 to cancel): ");
        try {
            int projectSelection = Integer.parseInt(scanner.nextLine().trim());
            
            if (projectSelection > 0 && projectSelection <= officerProjects.size()) {
                Project selectedProject = officerProjects.get(projectSelection - 1);
                processBookingForProject(selectedProject);
            } else if (projectSelection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Processes booking for a specific project
     * @param project Project to process bookings for
     */
    private void processBookingForProject(Project project) {
        // Get successful applications for the project
        List<Application> successfulApps = applicationManager.getApplicationsByProjectAndStatus(
            project.getProjectId(), ApplicationStatus.SUCCESSFUL);
        
        if (successfulApps.isEmpty()) {
            System.out.println("No successful applications to process for this project.");
            return;
        }
        
        System.out.println("\n===== SUCCESSFUL APPLICATIONS FOR " + project.getProjectName() + " =====");
        System.out.println("--------------------------------------------------");
        System.out.printf("%-5s | %-15s | %-10s | %-15s\n", 
                         "No.", "Applicant", "Flat Type", "Application Date");
        System.out.println("--------------------------------------------------");
        
        int index = 1;
        for (Application app : successfulApps) {
            User applicant = userManager.getUserByNRIC(app.getApplicantNric());
            String applicantNric = (applicant != null) ? applicant.getNric() : "N/A";
            
            System.out.printf("%-5d | %-15s | %-10s | %-15s\n", 
                            index++, 
                            applicantNric, 
                            app.getFlatType().getDisplayName(),
                            app.getApplicationDate());
        }
        System.out.println("--------------------------------------------------");
        
        // Get application selection
        System.out.print("\nEnter application number to process booking (or 0 to cancel): ");
        try {
            int appSelection = Integer.parseInt(scanner.nextLine().trim());
            
            if (appSelection > 0 && appSelection <= successfulApps.size()) {
                Application selectedApp = successfulApps.get(appSelection - 1);
                User applicant = userManager.getUserByNRIC(selectedApp.getApplicantNric());
                
                if (applicant == null) {
                    System.out.println("Error: Applicant data not found.");
                    return;
                }
                
                // Display applicant details
                System.out.println("\n===== APPLICANT DETAILS =====");
                System.out.println("NRIC: " + applicant.getNric());
                System.out.println("Age: " + applicant.getAge());
                System.out.println("Marital Status: " + applicant.getMaritalStatus());
                System.out.println("Applied For: " + selectedApp.getFlatType().getDisplayName());
                
                // Confirm booking
                System.out.println("\nYou are about to process flat booking for this applicant.");
                System.out.print("Confirm booking? (Y/N): ");
                
                String confirm = scanner.nextLine().trim().toUpperCase();
                if (confirm.equals("Y")) {
                    boolean success = applicationManager.processBooking(selectedApp.getApplicationId());
                    
                    if (success) {
                        System.out.println("\nBooking processed successfully!");
                        generateAndDisplayReceipt(applicant.getNric());
                    } else {
                        System.out.println("\nFailed to process booking. Please check if there are available units.");
                    }
                } else {
                    System.out.println("\nBooking process cancelled.");
                }
            } else if (appSelection != 0) {
                System.out.println("Invalid selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    
    /**
     * Generates and displays a booking receipt for an applicant
     * @param applicantNric Applicant's NRIC
     */
    private void generateAndDisplayReceipt(String applicantNric) {
        String receipt = applicationManager.generateBookingReceipt(applicantNric);
        
        System.out.println("\n" + receipt);
        
        // Offer to save the receipt to a file
        System.out.print("\nDo you want to save this receipt to a file? (Y/N): ");
        String saveChoice = scanner.nextLine().trim().toUpperCase();
        
        if (saveChoice.equals("Y")) {
            System.out.print("Enter filename (default: receipt.txt): ");
            String filename = scanner.nextLine().trim();
            
            if (filename.isEmpty()) {
                filename = "receipt.txt";
            }
            
            boolean saved = applicationManager.saveReceiptToFile(receipt, filename);
            
            if (saved) {
                System.out.println("Receipt saved to " + filename + " successfully.");
            } else {
                System.out.println("Failed to save receipt. Please try again later.");
            }
        }
    }
}