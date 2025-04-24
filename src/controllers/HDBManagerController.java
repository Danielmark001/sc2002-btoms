package controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;
import enumeration.RegistrationStatus;
import enumeration.MaritalStatus;
import models.BTOApplication;
import models.BTOProject;
import models.FlatTypeDetails;
import models.HDBManager;
import models.HDBOfficer;
import models.HDBOfficerRegistration;
import models.Applicant;
import models.Enquiry;
import models.ProjectFilter;
import models.User;
import models.WithdrawalRequest;
import services.BTOProjectService;
import services.EnquiryService;
import services.ReportService;
import stores.DataStore;
import stores.FilterStore;
import view.ReportView;
import utils.TextDecorationUtils;
import services.BTOProjectManagementService;
import view.BTOProjectManagementView;

/**
 * Controller class for handling HDB Manager operations in the BTO system.
 * 
 * This controller manages the administrative functions for HDB Managers,
 * including BTO project creation and management, HDB officer registration approval,
 * BTO application processing, withdrawal request handling, report generation,
 * and enquiry management. It extends the base UserController class and implements
 * the HDB Manager-specific menu system and workflows.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class HDBManagerController extends UserController {

    private static final Scanner sc = new Scanner(System.in);
    private final HDBManager hdbManager;
    private final BTOProjectService btoProjectService;
    private final ReportView reportView;
    private final EnquiryService enquiryService;
    private final ReportService reportService;
    private final BTOProjectManagementService projectManagementService;
    private final BTOProjectManagementView projectManagementView;
    
    // Currently handled application by this manager
    private BTOApplication currentApplication;

    /**
     * Constructor for HDBManagerController
     * @param hdbManager The HDB Manager user
     */
    public HDBManagerController(HDBManager hdbManager) {
        this.hdbManager = hdbManager;
        this.btoProjectService = new BTOProjectService();
        this.reportView = new ReportView();
        this.enquiryService = new EnquiryService();
        this.reportService = new ReportService();
        this.projectManagementService = new BTOProjectManagementService();
        this.projectManagementView = new BTOProjectManagementView();
        this.currentApplication = null;
    }
    
    /**
     * Checks if this HDB Manager is already handling an application within the application period.
     * 
     * @return true if already handling an application, false otherwise
     */
    private boolean isHandlingApplication() {
        if (currentApplication == null) {
            return false;
        }
        
        // Check if the application's project is within the application period
        BTOProject project = currentApplication.getProject();
        LocalDate today = LocalDate.now();
        boolean inApplicationPeriod = today.isAfter(project.getApplicationOpeningDate()) && 
                                     today.isBefore(project.getApplicationClosingDate());
        
        return inApplicationPeriod;
    }
    
    /**
     * Sets the current application being handled by this manager.
     * 
     * @param application The application to handle
     */
    private void setCurrentApplication(BTOApplication application) {
        this.currentApplication = application;
    }
    
    /**
     * Clears the current application being handled by this manager.
     */
    private void clearCurrentApplication() {
        this.currentApplication = null;
    }

    /**
     * Start the HDB Manager menu
     */
    public void start() {
        int choice;

do {
            System.out.println();
            System.out.println("==========================================");
            System.out.println(TextDecorationUtils.boldText("Hi, " + hdbManager.getName() + "!"));
            System.out.println("==========================================");
            System.out.println();
            
            System.out.println(TextDecorationUtils.underlineText("PROJECT MANAGEMENT"));
            System.out.println("└─ 1. Create BTO Project");
            System.out.println("└─ 2. Edit BTO Project");
            System.out.println("└─ 3. Delete BTO Project");
            System.out.println("└─ 4. View All Projects");
            System.out.println("└─ 5. View My Projects");
            System.out.println("└─ 6. Toggle Project Visibility");
            System.out.println();

            System.out.println(TextDecorationUtils.underlineText("HDB OFFICER MANAGEMENT"));
            System.out.println("└─ 7. View HDB Officer Registrations");
            System.out.println("└─ 8. Approve/Reject HDB Officer Registration");
            System.out.println();

            System.out.println(TextDecorationUtils.underlineText("APPLICATION MANAGEMENT"));
            System.out.println("└─ 9. View BTO Applications");
            System.out.println("└─ 10. Approve/Reject BTO Application");
            System.out.println("└─ 11. Approve/Reject Application Withdrawal");
            System.out.println("└─ 12. Generate Applicant Report");
            System.out.println();

            System.out.println(TextDecorationUtils.underlineText("ENQUIRY MANAGEMENT"));
            System.out.println("└─ 13. View All Enquiries");
            System.out.println("└─ 14. View and Reply to Project Enquiries");
            System.out.println();

            System.out.println(TextDecorationUtils.underlineText("SETTINGS"));
            System.out.println("└─ 15. Change Password");
            System.out.println();

            System.out.println(TextDecorationUtils.underlineText("LOGOUT"));
            System.out.println("└─ 0. Logout");
            System.out.println();
            System.out.println("==========================================");
            
            // Display current application if handling one
            if (isHandlingApplication()) {
                System.out.println("Currently handling application: " + currentApplication.getApplicationId());
                System.out.println("Project: " + currentApplication.getProject().getProjectName());
                System.out.println("==========================================");
            }
            
            System.out.print("Enter your choice: ");
            String input = sc.nextLine();
            if (input.matches("[0-9]+")) {
                choice = Integer.parseInt(input);
                if (choice < 0 || choice > 15) {
                    System.out.println("Invalid input. Please enter 0-15!");
                    continue;
                }
            } else {
                System.out.println("Invalid input. Please enter an integer.\n");
                continue;
            }
            
            switch (choice) {
                case 0:
                    System.out.println("Logging out...");
                    AuthController.startSession();
                    return;
                case 1:
                    createBTOProject();
                    break;
                case 2:
                    editBTOProject();
                    break;
                case 3:
                    deleteBTOProject();
                    break;
                case 4:
                    viewAllProjects();
                    break;
                case 5:
                    viewMyProjects();
                    break;
                case 6:
                    toggleProjectVisibility();
                    break;
                case 7:
                    viewHDBOfficerRegistrations();
                    break;
                case 8:
                    approveRejectHDBOfficerRegistration();
                    break;
                case 9:
                    viewBTOApplications();
                    break;
                case 10:
                    approveRejectBTOApplication();
                    break;
                case 11:
                    approveRejectApplicationWithdrawal();
                    break;
                case 12:
                    generateApplicantReport();
                    break;
                case 13:
                    viewAllEnquiries();
                    break;
                case 14:
                    viewAndReplyToProjectEnquiries();
                    break;
                case 15:
                    changePassword();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        } while(true);
    }

    /**
     * Create a new BTO project
     */
  private void createBTOProject() {
        // Get project creation details from the view
        Object[] details = projectManagementView.getProjectCreationDetails();
        if (details == null) {
            return;
        }
        
        // Get the application dates from the details
        LocalDate openingDate = (LocalDate) details[2];
        LocalDate closingDate = (LocalDate) details[3];
        
        // Check if the new project's dates overlap with existing projects managed by this HDB manager
        if (projectManagementService.hasOverlappingProjectDates(hdbManager, openingDate, closingDate)) {
            System.out.println("You already have a project with overlapping application dates.");
            System.out.println("Please choose different dates that don't overlap with your existing projects.");
            return;
        }

        if (!projectManagementService.isProjectNameUnique(details[0].toString())) {
            System.out.println("Project name already exists. Please choose a different name.");
            return;
        }
        
        // Type checking for the map
        if (!(details[4] instanceof Map<?, ?>)) {
            System.out.println("Invalid flat types data.");
            return;
        }
        
        Map<?, ?> rawMap = (Map<?, ?>) details[4];
        Map<FlatType, FlatTypeDetails> flatTypes = new HashMap<>();
        
        // Verify each entry in the map
        for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
            if (!(entry.getKey() instanceof FlatType) || !(entry.getValue() instanceof FlatTypeDetails)) {
                System.out.println("Invalid flat types data format.");
                return;
            }
            flatTypes.put((FlatType) entry.getKey(), (FlatTypeDetails) entry.getValue());
        }
        
        BTOProject project = projectManagementService.createProject(
            (String) details[0],
            (String) details[1],
            openingDate,
            closingDate,
            flatTypes,
            hdbManager,
            (int) details[5]
        );
        
        System.out.println("BTO Project created successfully!");
    }

    /**
     * Edit an existing BTO project
     */
    private void editBTOProject() {
        System.out.println("\n===== Edit BTO Project =====");
        
        // Get project to edit
        BTOProject project = selectProject("Select project to edit: ", true);
        if (project == null) {
            return;
        }
        
        // Check if the project is within application period
        LocalDate today = LocalDate.now();
        if (today.isAfter(project.getApplicationOpeningDate()) && today.isBefore(project.getApplicationClosingDate())) {
            System.out.println("Cannot edit a project that is currently in application period.");
            return;
        }
        
        System.out.println("\nCurrent project details:");
        projectManagementView.displayProjectDetails(project);
        
        int choice = projectManagementView.getEditChoice();
        if (choice == -1) {
            return;
        }
        
        switch (choice) {
            case 0:
                return;
            case 1:
                String newName = projectManagementView.getNewProjectName();
                projectManagementService.updateProjectName(project, newName);
                break;
            case 2:
                String newNeighborhood = projectManagementView.getNewNeighborhood();
                projectManagementService.updateNeighborhood(project, newNeighborhood);
                break;
            case 3:
            case 4:
                LocalDate[] newDates = projectManagementView.getNewApplicationDates();
                if (newDates != null) {
                    projectManagementService.updateApplicationDates(project, newDates[0], newDates[1]);
                }
                break;
            case 5:
                Map<FlatType, FlatTypeDetails> newFlatTypes = projectManagementView.getFlatTypes();
                if (newFlatTypes != null) {
                    projectManagementService.updateFlatTypes(project, newFlatTypes);
                }
                break;
            case 6:
                int newSlots = projectManagementView.getNewHDBOfficerSlots(project.getHDBOfficers().size());
                if (newSlots != -1) {
                    projectManagementService.updateHDBOfficerSlots(project, newSlots);
                }
                break;
            case 7:
                boolean newVisibility = projectManagementView.getNewVisibility();
                projectManagementService.updateVisibility(project, newVisibility);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        System.out.println("Project updated successfully!");
    }

    /**
     * Delete a BTO project
     */
    private void deleteBTOProject() {
        System.out.println("\n===== Delete BTO Project =====");
        
        // Get project to delete
        BTOProject project = selectProject("Select project to delete: ", true);
        if (project == null) {
            return;
        }
        
        // Check if the project is within application period
        LocalDate today = LocalDate.now();
        if (today.isAfter(project.getApplicationOpeningDate()) && today.isBefore(project.getApplicationClosingDate())) {
            System.out.println("Cannot delete a project that is currently in application period.");
            return;
        }
        
        System.out.println("\nProject details:");
        projectManagementView.displayProjectDetails(project);
        
        if (projectManagementView.getDeletionConfirmation()) {
            projectManagementService.deleteProject(project);
            System.out.println("Project deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * View all BTO projects with filtering
     */
    private void viewAllProjects() {
        System.out.println("\n===== All BTO Projects =====");
        
        // Get filter settings
        ProjectFilter filter = FilterStore.getProjectFilter(hdbManager);
        
        // Apply filter to all projects
        List<BTOProject> filteredProjects = btoProjectService.getAllProjects(filter);
        
        if (filteredProjects.isEmpty()) {
            System.out.println("\nNo projects match the current filters.");
            
            // Offer to reset filters
            System.out.print("Would you like to reset filters and view all projects? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();
            
            if (response.equals("yes")) {
                filter.resetFilters();
                FilterStore.setProjectFilter(hdbManager, filter);
                filteredProjects = new ArrayList<>(DataStore.getBTOProjectsData().values());
                
                if (filteredProjects.isEmpty()) {
                    System.out.println("No projects found.");
                    return;
                }
            } else {
                return;
            }
        }
        
        // Display filtered projects
        projectManagementView.displayFilteredProjects(filteredProjects, filter);
        
        boolean done = false;
        while (!done) {
            System.out.println("\nOptions:");
            System.out.println("1. View project details");
            System.out.println("2. Filter projects");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");
            
            String input = sc.nextLine().trim();
            
            try {
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 0:
                        done = true;
                        break;
                    case 1:
                        viewProjectDetails(filteredProjects);
                        break;
                    case 2:
                        // Show filter options
                        filter = projectManagementView.showFilterOptions(filter);
                        // Apply updated filters
                        filteredProjects = btoProjectService.getAllProjects(filter);
                        // Display projects with updated filters
                        projectManagementView.displayFilteredProjects(filteredProjects, filter);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    /**
     * View details for a selected project
     * @param projects List of projects to choose from
     */
    private void viewProjectDetails(List<BTOProject> projects) {
        System.out.print("Enter project number (1-" + projects.size() + ") or 0 to cancel: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine().trim());
            
            if (choice == 0) {
                return;
            }
            
            if (choice < 1 || choice > projects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
            
            BTOProject selectedProject = projects.get(choice - 1);
            projectManagementView.displayProjectDetails(selectedProject);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * View projects created by the current HDB Manager with filtering
     */
    private void viewMyProjects() {
        System.out.println("\n===== My BTO Projects =====");
        
        // Get filter settings
        ProjectFilter filter = FilterStore.getProjectFilter(hdbManager);
        
        // Get managed projects
        List<BTOProject> myProjects = projectManagementService.getManagedProjects(hdbManager);
        
        // Apply filter
        List<BTOProject> filteredProjects = filter.applyFilter(myProjects);
        
        if (filteredProjects.isEmpty()) {
            System.out.println("\nNo projects match the current filters.");
            
            // Offer to reset filters
            System.out.print("Would you like to reset filters and view all your projects? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();
            
            if (response.equals("yes")) {
                filter.resetFilters();
                FilterStore.setProjectFilter(hdbManager, filter);
                filteredProjects = myProjects;
                
                if (filteredProjects.isEmpty()) {
                    System.out.println("You have not created any projects.");
                    return;
                }
            } else {
                return;
            }
        }
        
        // Display filtered projects
        projectManagementView.displayFilteredProjects(filteredProjects, filter);
        
        boolean done = false;
        while (!done) {
            System.out.println("\nOptions:");
            System.out.println("1. View project details");
            System.out.println("2. Filter projects");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");
            
            String input = sc.nextLine().trim();
            
            try {
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 0:
                        done = true;
                        break;
                    case 1:
                        viewProjectDetails(filteredProjects);
                        break;
                    case 2:
                        // Show filter options
                        filter = projectManagementView.showFilterOptions(filter);
                        // Apply updated filters
                        filteredProjects = filter.applyFilter(myProjects);
                        // Save filter settings
                        FilterStore.setProjectFilter(hdbManager, filter);
                        // Display projects with updated filters
                        projectManagementView.displayFilteredProjects(filteredProjects, filter);
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    /**
     * Toggle the visibility of a project
     */
    private void toggleProjectVisibility() {
        System.out.println("\n===== Toggle Project Visibility =====");
        
        // Get filter settings
        ProjectFilter filter = FilterStore.getProjectFilter(hdbManager);
        
        // Get managed projects with filter
        List<BTOProject> myProjects = projectManagementService.getManagedProjects(hdbManager);
        List<BTOProject> filteredProjects = filter.applyFilter(myProjects);
        
        if (filteredProjects.isEmpty()) {
            System.out.println("\nNo projects match the current filters.");
            
            // Offer to reset filters
            System.out.print("Would you like to reset filters and view all your projects? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();
            
            if (response.equals("yes")) {
                filter.resetFilters();
                FilterStore.setProjectFilter(hdbManager, filter);
                filteredProjects = myProjects;
                
                if (filteredProjects.isEmpty()) {
                    System.out.println("You have not created any projects.");
                    return;
                }
            } else {
                return;
            }
        }
        
        // Display filtered projects
        projectManagementView.displayFilteredProjects(filteredProjects, filter);
        
        // Select project to toggle visibility
        System.out.print("\nEnter project number to toggle visibility (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine().trim());
            
            if (choice == 0) {
                return;
            }
            
            if (choice < 1 || choice > filteredProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
            
            BTOProject selectedProject = filteredProjects.get(choice - 1);
            
            System.out.println("\nCurrent project details:");
            projectManagementView.displayProjectDetails(selectedProject);
            
            System.out.println("Current visibility: " + (selectedProject.isVisible() ? "Visible" : "Hidden"));
            System.out.print("Set visibility to (true/false): ");
            
            boolean newVisibility = Boolean.parseBoolean(sc.nextLine().trim());
            projectManagementService.updateVisibility(selectedProject, newVisibility);
            System.out.println("Project visibility updated successfully!");
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * View HDB Officer registrations
     */
    private void viewHDBOfficerRegistrations() {
        System.out.println("\n===== HDB Officer Registrations =====");
        
        // Get projects managed by this HDB Manager
        List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .collect(Collectors.toList());
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects.");
            return;
        }
        
        // Get registrations for these projects
        List<HDBOfficerRegistration> registrations = DataStore.getHDBOfficerRegistrationsData().values().stream()
            .filter(registration -> myProjects.contains(registration.getProject()))
            .collect(Collectors.toList());
        
        if (registrations.isEmpty()) {
            System.out.println("No HDB Officer registrations found for your projects.");
            return;
        }
        
        for (int i = 0; i < registrations.size(); i++) {
            HDBOfficerRegistration registration = registrations.get(i);
            System.out.println("\nRegistration " + (i + 1) + ":");
            System.out.println("Registration ID: " + registration.getRegistrationId());
            System.out.println("HDB Officer: " + registration.getHDBOfficer().getName() + " (" + registration.getHDBOfficer().getNric() + ")");
            System.out.println("Project: " + registration.getProject().getProjectName());
            System.out.println("Status: " + registration.getStatus().getDisplayName());
        }
    }

    /**
     * Approve or reject an HDB Officer registration
     */
    private void approveRejectHDBOfficerRegistration() {
        System.out.println("\n===== Approve/Reject HDB Officer Registration =====");
        
        // Get projects managed by this HDB Manager
        List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .collect(Collectors.toList());
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects.");
            return;
        }
        
        // Get pending registrations for these projects
        List<HDBOfficerRegistration> pendingRegistrations = DataStore.getHDBOfficerRegistrationsData().values().stream()
            .filter(registration -> myProjects.contains(registration.getProject()) && 
                                  registration.getStatus() == RegistrationStatus.PENDING)
            .collect(Collectors.toList());
        
        if (pendingRegistrations.isEmpty()) {
            System.out.println("No pending HDB Officer registrations found for your projects.");
            return;
        }
        
        // Display pending registrations
        for (int i = 0; i < pendingRegistrations.size(); i++) {
            HDBOfficerRegistration registration = pendingRegistrations.get(i);
            System.out.println("\nRegistration " + (i + 1) + ":");
            System.out.println("Registration ID: " + registration.getRegistrationId());
            System.out.println("HDB Officer: " + registration.getHDBOfficer().getName() + " (" + registration.getHDBOfficer().getNric() + ")");
            System.out.println("Project: " + registration.getProject().getProjectName());
        }
        
        // Select registration to approve/reject
        System.out.print("Enter registration number to approve/reject (0 to cancel): ");
        int choice = 0;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > pendingRegistrations.size()) {
                System.out.println("Invalid registration number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        HDBOfficerRegistration registration = pendingRegistrations.get(choice - 1);
        BTOProject project = registration.getProject();
        HDBOfficer officer = registration.getHDBOfficer();
        
        // Check if project has available slots
        if (project.getHDBOfficers().size() >= project.getHDBOfficerSlots()) {
            System.out.println("Project has no available HDB Officer slots.");
            return;
        }
        
        System.out.print("Approve registration? (yes/no): ");
        String approval = sc.nextLine().toLowerCase();
        
        if (approval.equals("yes")) {
            registration.setStatus(RegistrationStatus.APPROVED);
            project.addHDBOfficer(officer);
            officer.addHandledProject(project);
            System.out.println("Registration approved successfully!");
        } else {
            registration.setStatus(RegistrationStatus.REJECTED);
            System.out.println("Registration rejected.");
        }
        
        DataStore.saveData();
    }

    /**
     * View BTO applications
     */
    private void viewBTOApplications() {
        System.out.println("\n===== BTO Applications =====");
        
        // Get projects managed by this HDB Manager
        List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .collect(Collectors.toList());
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects.");
            return;
        }
        
        // Get applications for these projects
        List<BTOApplication> applications = DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> myProjects.contains(application.getProject()))
            .collect(Collectors.toList());
        
        if (applications.isEmpty()) {
            System.out.println("No BTO applications found for your projects.");
            return;
        }
        
        System.out.println("\nFilter applications by:");
        System.out.println("1. All applications");
        System.out.println("2. Pending applications");
        System.out.println("3. Successful applications");
        System.out.println("4. Unsuccessful applications");
        System.out.println("5. Booked applications");
        System.out.print("Enter your choice: ");
        
        int filterChoice = 1;
        try {
            filterChoice = Integer.parseInt(sc.nextLine().trim());
            if (filterChoice < 1 || filterChoice > 5) {
                System.out.println("Invalid choice. Showing all applications.");
                filterChoice = 1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Showing all applications.");
            filterChoice = 1;
        }
        
        // Apply status filter
        List<BTOApplication> filteredApplications = applications;
        switch (filterChoice) {
            case 2:
                filteredApplications = applications.stream()
                    .filter(application -> application.getStatus() == BTOApplicationStatus.PENDING)
                    .collect(Collectors.toList());
                System.out.println("\nShowing pending applications:");
                break;
            case 3:
                filteredApplications = applications.stream()
                    .filter(application -> application.getStatus() == BTOApplicationStatus.SUCCESSFUL)
                    .collect(Collectors.toList());
                System.out.println("\nShowing successful applications:");
                break;
            case 4:
                filteredApplications = applications.stream()
                    .filter(application -> application.getStatus() == BTOApplicationStatus.UNSUCCESSFUL)
                    .collect(Collectors.toList());
                System.out.println("\nShowing unsuccessful applications:");
                break;
            case 5:
                filteredApplications = applications.stream()
                    .filter(application -> application.getStatus() == BTOApplicationStatus.BOOKED)
                    .collect(Collectors.toList());
                System.out.println("\nShowing booked applications:");
                break;
            default:
                System.out.println("\nShowing all applications:");
                break;
        }
        
        if (filteredApplications.isEmpty()) {
            System.out.println("No applications found with the selected filter.");
            return;
        }
        
        for (int i = 0; i < filteredApplications.size(); i++) {
            BTOApplication application = filteredApplications.get(i);
            System.out.println("\nApplication " + (i + 1) + ":");
            System.out.println("Application ID: " + application.getApplicationId());
            System.out.println("Applicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
            System.out.println("Project: " + application.getProject().getProjectName());
            System.out.println("Flat Type: " + (application.getFlatType() != null ? application.getFlatType().getDisplayName() : "Not specified"));
            System.out.println("Status: " + application.getStatus().getDisplayName());
        }
    }

    /**
     * Approve or reject a BTO application
     */
    private void approveRejectBTOApplication() {
        System.out.println("\n===== Approve/Reject BTO Application =====");
        
        // Check if manager is already handling an application within the application period
        if (isHandlingApplication()) {
            BTOProject currentProject = currentApplication.getProject();
            System.out.println("You are already handling an application for " + 
                              currentProject.getProjectName() + 
                              " (ID: " + currentApplication.getApplicationId() + ").");
            System.out.println("You cannot handle another application until you complete this one.");
            return;
        }
        
        // Get projects managed by this HDB Manager
        List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .collect(Collectors.toList());
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects.");
            return;
        }
        
        // Get pending applications for these projects
        List<BTOApplication> pendingApplications = DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> myProjects.contains(application.getProject()) && 
                                 application.getStatus() == BTOApplicationStatus.PENDING)
            .collect(Collectors.toList());
        
        if (pendingApplications.isEmpty()) {
            System.out.println("No pending BTO applications found for your projects.");
            return;
        }
        
        // Display pending applications
        for (int i = 0; i < pendingApplications.size(); i++) {
            BTOApplication application = pendingApplications.get(i);
            User applicantUser = application.getApplicant();
            System.out.println("\nApplication " + (i + 1) + ":");
            System.out.println("Application ID: " + application.getApplicationId());
            System.out.println("Applicant: " + applicantUser.getName() + " (" + applicantUser.getNric() + ")");
            System.out.println("Project: " + application.getProject().getProjectName());
            
            // Get eligible flat types for this applicant - safely check type
            Map<FlatType, FlatTypeDetails> eligibleFlatTypes;
            
            if (applicantUser instanceof Applicant) {
                eligibleFlatTypes = btoProjectService.getEligibleFlatTypes(
                    application.getProject(), 
                    (Applicant) applicantUser
                );
            } else if (applicantUser instanceof HDBOfficer) {
                eligibleFlatTypes = btoProjectService.getEligibleFlatTypes(
                    application.getProject(), 
                    (HDBOfficer) applicantUser
                );
            } else {
                System.out.println("Unsupported user type for applicant. Skipping eligibility check.");
                continue;
            }
            
            // Display eligible flat types
            System.out.println("Eligible Flat Types:");
            for (Map.Entry<FlatType, FlatTypeDetails> entry : eligibleFlatTypes.entrySet()) {
                if (entry.getValue().getUnits() > 0) {
                    System.out.println(entry.getKey().getDisplayName() + " (" + entry.getValue().getUnits() + " units available)");
                }
            }
            System.out.println();
        }
        
        // Select application to approve/reject
        System.out.print("Enter application number (1-" + pendingApplications.size() +") to approve/reject (0 to cancel): ");
        int choice = 0;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > pendingApplications.size()) {
                System.out.println("Invalid application number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        BTOApplication application = pendingApplications.get(choice - 1);
        BTOProject project = application.getProject();
        User applicantUser = application.getApplicant();
        
        // Check if the project is in application period
        LocalDate today = LocalDate.now();
        boolean inApplicationPeriod = today.isAfter(project.getApplicationOpeningDate()) && 
                                     today.isBefore(project.getApplicationClosingDate());
        
        // Set this as the current application being handled if in application period
        if (inApplicationPeriod) {
            setCurrentApplication(application);
        }
        
        // Get eligible flat types for this applicant based on user type
        Map<FlatType, FlatTypeDetails> eligibleFlatTypes;
        
        if (applicantUser instanceof Applicant) {
            eligibleFlatTypes = btoProjectService.getEligibleFlatTypes(
                project, (Applicant) applicantUser);
        } else if (applicantUser instanceof HDBOfficer) {
            eligibleFlatTypes = btoProjectService.getEligibleFlatTypes(
                project, (HDBOfficer) applicantUser);
        } else {
            System.out.println("Unsupported user type for application.");
            clearCurrentApplication();
            return;
        }
        
        // Check if there are any available units for eligible flat types
        boolean hasAvailableUnits = false;
        for (Map.Entry<FlatType, FlatTypeDetails> entry : eligibleFlatTypes.entrySet()) {
            if (entry.getValue().getUnits() > 0) {
                hasAvailableUnits = true;
                break;
            }
        }
        
        if (!hasAvailableUnits) {
            System.out.println("No units available for any eligible flat type in this project.");
            clearCurrentApplication();
            return;
        }
        
        System.out.print("Approve application? (yes/no): ");
        String approval = sc.nextLine().toLowerCase();
        
        if (approval.equals("yes")) {
            application.setStatus(BTOApplicationStatus.SUCCESSFUL);
            System.out.println("Application approved successfully!");
            System.out.println("The applicant can now request to book a flat.");
        } else {
            application.setStatus(BTOApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application rejected.");
        }
        
        // Clear the current application being handled
        clearCurrentApplication();
        
        DataStore.saveData();
    }

    /**
     * Approve or reject an application withdrawal request
     */
    private void approveRejectApplicationWithdrawal() {
        System.out.println("\n===== Approve/Reject Application Withdrawal =====");
        
        // Get projects managed by this HDB Manager
        List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .collect(Collectors.toList());
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects.");
            return;
        }
        
        // Get pending withdrawal requests for these projects
        List<WithdrawalRequest> pendingRequests = DataStore.getWithdrawalRequestsData().values().stream()
            .filter(request -> myProjects.contains(request.getApplication().getProject()) && !request.isApproved())
            .collect(Collectors.toList());
        
        if (pendingRequests.isEmpty()) {
            System.out.println("No pending withdrawal requests found for your projects.");
            return;
        }
        
        // Display pending withdrawal requests
        for (int i = 0; i < pendingRequests.size(); i++) {
            WithdrawalRequest request = pendingRequests.get(i);
            BTOApplication application = request.getApplication();
            System.out.println("\nRequest " + (i + 1) + ":");
            System.out.println("Request ID: " + request.getRequestId());
            System.out.println("Application ID: " + application.getApplicationId());
            System.out.println("Applicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
            System.out.println("Project: " + application.getProject().getProjectName());
            System.out.println("Requested At: " + request.getRequestedAt());
            System.out.println("----------------------------------------");
        }
        
        // Select request to approve/reject
        System.out.print("\nEnter request number to approve/reject (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > pendingRequests.size()) {
                System.out.println("Invalid request number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        WithdrawalRequest selectedRequest = pendingRequests.get(choice - 1);
        BTOApplication application = selectedRequest.getApplication();
        
        // Check if application is already booked
        if (application.getStatus() == BTOApplicationStatus.BOOKED) {
            System.out.println("\nCannot process withdrawal for an application that has already been booked.");
            return;
        }
        
        System.out.print("Approve withdrawal request? (yes/no): ");
        String approval = sc.nextLine().toLowerCase();
        
        if (approval.equals("yes")) {
            selectedRequest.approve(hdbManager.getName());
            System.out.println("Withdrawal request approved successfully!");
            System.out.println("The application has been marked as unsuccessful.");
        } else {
            selectedRequest.reject(hdbManager.getName());
            System.out.println("Withdrawal request rejected.");
        }
        
        DataStore.saveData();
    }

    /**
     * Generate a report of applicants
     */
    private void generateApplicantReport() {
        System.out.println("\n===== Generate Applicant Report =====");
        
        // Get projects managed by this HDB Manager
        List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .collect(Collectors.toList());
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects.");
            return;
        }
        
        // Get successful and booked applications
        List<BTOApplication> applications = reportService.getAllSuccessfulAndBookedApplications();
        
        if (applications.isEmpty()) {
            System.out.println("No successful or booked BTO applications found for your projects.");
            return;
        }
        
        // Get filter choice from user
        int filterChoice = reportView.displayFilterOptions();
        if (filterChoice == -1) {
            return;
        }
        
        List<BTOApplication> filteredApplications = new ArrayList<>(applications);
        
        switch (filterChoice) {
            case 1:
                // No filtering needed
                break;
            case 2:
                // Filter by project
                int projectChoice = reportView.getProjectSelection(myProjects);
                if (projectChoice == -1) {
                    return;
                }
                filteredApplications = reportService.filterByProject(filteredApplications, myProjects.get(projectChoice - 1));
                break;
            case 3:
                // Filter by flat type
                FlatType flatType = reportView.getFlatTypeSelection();
                if (flatType == null) {
                    return;
                }
                filteredApplications = reportService.filterByFlatType(filteredApplications, flatType);
                break;
            case 4:
                // Filter by marital status
                MaritalStatus maritalStatus = reportView.getMaritalStatusSelection();
                if (maritalStatus == null) {
                    return;
                }
                filteredApplications = reportService.filterByMaritalStatus(filteredApplications, maritalStatus);
                break;
            case 5:
                // Filter by age range
                int[] ageRange = reportView.getAgeRange();
                if (ageRange == null) {
                    return;
                }
                filteredApplications = reportService.filterByAgeRange(filteredApplications, ageRange[0], ageRange[1]);
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        // Display the report
        reportView.displayReport(filteredApplications);
    }

    /**
     * View all enquiries
     */
    private void viewAllEnquiries() {
        System.out.println("\n===== All Enquiries =====");
        
        List<Enquiry> allEnquiries = enquiryService.getAllEnquiries();
        
        if (allEnquiries.isEmpty()) {
            System.out.println("There are no enquiries in the system.");
            return;
        }
        
        // Display all enquiries
        for (int i = 0; i < allEnquiries.size(); i++) {
            Enquiry enquiry = allEnquiries.get(i);
            System.out.println("\n" + (i + 1) + ". Enquiry ID: " + enquiry.getEnquiryId());
            System.out.println("   Project: " + enquiry.getProject().getProjectName());
            System.out.println("   Applicant: " + enquiry.getApplicant().getName() + " (" + enquiry.getApplicant().getNric() + ")");
            System.out.println("   Message: " + enquiry.getMessage());
            System.out.println("   Submitted: " + enquiry.getCreatedAt());
            if (enquiry.hasReply()) {
                System.out.println("   Reply: " + enquiry.getReply());
                System.out.println("   Replied: " + enquiry.getRepliedAt());
            } else {
                System.out.println("   Status: Pending reply");
            }
            System.out.println("----------------------------------------");
        }
    }

    /**
     * View and reply to project enquiries for projects managed by this HDB Manager
     */
    private void viewAndReplyToProjectEnquiries() {
        // Get projects managed by this HDB Manager
        List<BTOProject> managedProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .collect(Collectors.toList());
        
        if (managedProjects.isEmpty()) {
            System.out.println("\nYou have not created any projects.");
            return;
        }
        
        // Display managed projects
        System.out.println("\n===== Your Managed Projects =====");
        for (int i = 0; i < managedProjects.size(); i++) {
            BTOProject project = managedProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName());
        }
        
        // Select project
        System.out.print("\nEnter project number: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(sc.nextLine());
            if (projectChoice < 1 || projectChoice > managedProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        BTOProject selectedProject = managedProjects.get(projectChoice - 1);
        
        // Get enquiries for selected project
        List<Enquiry> projectEnquiries = enquiryService.getEnquiriesByProject(selectedProject);
        
        if (projectEnquiries.isEmpty()) {
            System.out.println("\nThere are no enquiries for this project.");
            return;
        }
        
        // Display enquiries
        System.out.println("\n===== Project Enquiries =====");
        for (int i = 0; i < projectEnquiries.size(); i++) {
            Enquiry enquiry = projectEnquiries.get(i);
            System.out.println("\n" + (i + 1) + ". Enquiry ID: " + enquiry.getEnquiryId());
            System.out.println("   Applicant: " + enquiry.getApplicant().getName() + " (" + enquiry.getApplicant().getNric() + ")");
            System.out.println("   Message: " + enquiry.getMessage());
            System.out.println("   Submitted: " + enquiry.getCreatedAt());
            if (enquiry.hasReply()) {
                System.out.println("   Reply: " + enquiry.getReply());
                System.out.println("   Replied: " + enquiry.getRepliedAt());
            } else {
                System.out.println("   Status: Pending reply");
            }
            System.out.println("----------------------------------------");
        }
        
        // Select enquiry to reply
        System.out.print("\nEnter enquiry number to reply (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > projectEnquiries.size()) {
                System.out.println("Invalid enquiry number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        Enquiry selectedEnquiry = projectEnquiries.get(choice - 1);
        
        // Check if already replied
        if (selectedEnquiry.hasReply()) {
            System.out.println("\nThis enquiry has already been replied to.");
            return;
        }
        
        // Get reply
        System.out.print("Enter your reply: ");
        String reply = sc.nextLine();
        
        if (reply.trim().isEmpty()) {
            System.out.println("Reply cannot be empty.");
            return;
        }
        
        // Use the service to reply
        if (enquiryService.replyToEnquiry(selectedEnquiry, reply)) {
            System.out.println("\nReply submitted successfully!");
        } else {
            System.out.println("\nFailed to submit reply.");
        }
    }
    
    /**
     * Select a project from a list
     * @param prompt The prompt to display
     * @param myProjectsOnly Whether to show only projects created by this HDB Manager
     * @return The selected project, or null if none selected
     */
    private BTOProject selectProject(String prompt, boolean myProjectsOnly) {
        // Get filter settings
        ProjectFilter filter = FilterStore.getProjectFilter(hdbManager);
        
        List<BTOProject> projects;
        
        if (myProjectsOnly) {
            projects = DataStore.getBTOProjectsData().values().stream()
                .filter(project -> project.getHDBManager().equals(hdbManager))
                .collect(Collectors.toList());
        } else {
            projects = new ArrayList<BTOProject>(DataStore.getBTOProjectsData().values());
        }
        
        // Apply filter
        projects = filter.applyFilter(projects);
        
        if (projects.isEmpty()) {
            // Offer to reset filters
            System.out.println("\nNo projects match the current filters.");
            System.out.print("Would you like to reset filters and view all projects? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();
            
            if (response.equals("yes")) {
                filter.resetFilters();
                FilterStore.setProjectFilter(hdbManager, filter);
                
                if (myProjectsOnly) {
                    projects = DataStore.getBTOProjectsData().values().stream()
                        .filter(project -> project.getHDBManager().equals(hdbManager))
                        .collect(Collectors.toList());
                } else {
                    projects = new ArrayList<BTOProject>(DataStore.getBTOProjectsData().values());
                }
                
                if (projects.isEmpty()) {
                    System.out.println("No projects found.");
                    return null;
                }
            } else {
                return null;
            }
        }
        
        // Display filtered projects
        projectManagementView.displayFilteredProjects(projects, filter);
        
        System.out.print(prompt);
        int choice = 0;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice < 1 || choice > projects.size()) {
                System.out.println("Invalid project number.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return null;
        }
        
        return projects.get(choice - 1);
    }
}