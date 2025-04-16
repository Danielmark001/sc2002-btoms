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
import services.BTOProjectService;
import stores.DataStore;
import utils.TextDecorationUtils;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


/**
 * Controller for HDB Manager operations
 */
public class HDBManagerController extends UserController {

    private static final Scanner sc = new Scanner(System.in);
    private final HDBManager hdbManager;
    private final BTOProjectService btoProjectService;

    /**
     * Constructor for HDBManagerController
     * @param hdbManager The HDB Manager user
     */
    public HDBManagerController(HDBManager hdbManager) {
        this.hdbManager = hdbManager;
        this.btoProjectService = new BTOProjectService();
    }

    /**
     * Start the HDB Manager menu
     */
    public void start() {
        boolean exit = false; // Declare the exit variable
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
                    exit = true;
                    System.out.println("Logging out...");
                    AuthController.endSession();
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
        } while(!exit);
    }

    /**
     * Create a new BTO project
     */
    private void createBTOProject() {
        System.out.println("\n===== Create BTO Project =====");
        
        // Check if the manager is already handling a project within an application period
        if (isHandlingActiveProject()) {
            System.out.println("You are already handling a project within an application period.");
            return;
        }
        
        // Get project details
        System.out.print("Enter project name: ");
        String projectName = sc.nextLine();
        
        System.out.print("Enter neighborhood (e.g. Yishun, Boon Lay): ");
        String neighborhood = sc.nextLine();
        
        // Get application dates
        LocalDate openingDate = null;
        LocalDate closingDate = null;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        while (openingDate == null) {
            System.out.print("Enter application opening date (yyyy-MM-dd): ");
            try {
                openingDate = LocalDate.parse(sc.nextLine(), formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
        
        while (closingDate == null) {
            System.out.print("Enter application closing date (yyyy-MM-dd): ");
            try {
                closingDate = LocalDate.parse(sc.nextLine(), formatter);
                if (closingDate.isBefore(openingDate)) {
                    System.out.println("Closing date must be after opening date.");
                    closingDate = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
        
        // Get flat types and units
        Map<FlatType, FlatTypeDetails> flatTypes = new HashMap<>();
        
        // 2-Room flats
        System.out.print("Enter number of 2-Room units: ");
        int twoRoomUnits = 0;
        try {
            twoRoomUnits = Integer.parseInt(sc.nextLine());
            if (twoRoomUnits > 0) {
                System.out.print("Enter price for 2-Room units: ");
                double twoRoomPrice = Double.parseDouble(sc.nextLine());
                flatTypes.put(FlatType.TWO_ROOM, new FlatTypeDetails(twoRoomUnits, twoRoomPrice));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        // 3-Room flats
        System.out.print("Enter number of 3-Room units: ");
        int threeRoomUnits = 0;
        try {
            threeRoomUnits = Integer.parseInt(sc.nextLine());
            if (threeRoomUnits > 0) {
                System.out.print("Enter price for 3-Room units: ");
                double threeRoomPrice = Double.parseDouble(sc.nextLine());
                flatTypes.put(FlatType.THREE_ROOM, new FlatTypeDetails(threeRoomUnits, threeRoomPrice));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        if (flatTypes.isEmpty()) {
            System.out.println("At least one flat type must be specified.");
            return;
        }
        
        // Get HDB Officer slots
        System.out.print("Enter number of HDB Officer slots (max 10): ");
        int hdbOfficerSlots = 0;
        try {
            hdbOfficerSlots = Integer.parseInt(sc.nextLine());
            if (hdbOfficerSlots < 0 || hdbOfficerSlots > 10) {
                System.out.println("HDB Officer slots must be between 0 and 10.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        // Create the project
        BTOProject project = new BTOProject(
            projectName, 
            neighborhood, 
            openingDate, 
            closingDate, 
            flatTypes, 
            hdbManager, 
            hdbOfficerSlots, 
            new ArrayList<>(), 
            false // Initially not visible
        );
        
        // Add to data store
        DataStore.getBTOProjectsData().put(projectName, project);
        DataStore.saveData();
        
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
        displayProjectDetails(project);
        
        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. Project Name");
        System.out.println("2. Neighborhood");
        System.out.println("3. Application Opening Date");
        System.out.println("4. Application Closing Date");
        System.out.println("5. Flat Types and Units");
        System.out.println("6. HDB Officer Slots");
        System.out.println("7. Visibility");
        System.out.println("0. Cancel");
        System.out.print("Enter your choice: ");
        
        int choice = 0;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        switch (choice) {
            case 0:
                return;
            case 1:
                System.out.print("Enter new project name: ");
                String newName = sc.nextLine();
                project.setProjectName(newName);
                // Update the key in the map
                DataStore.getBTOProjectsData().remove(project.getProjectName());
                DataStore.getBTOProjectsData().put(newName, project);
                break;
            case 2:
                System.out.print("Enter new neighborhood: ");
                project.setNeighborhood(sc.nextLine());
                break;
            case 3:
                System.out.print("Enter new application opening date (yyyy-MM-dd): ");
                try {
                    LocalDate newOpeningDate = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    if (newOpeningDate.isAfter(project.getApplicationClosingDate())) {
                        System.out.println("Opening date must be before closing date.");
                        return;
                    }
                    project.setApplicationOpeningDate(newOpeningDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                    return;
                }
                break;
            case 4:
                System.out.print("Enter new application closing date (yyyy-MM-dd): ");
                try {
                    LocalDate newClosingDate = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    if (newClosingDate.isBefore(project.getApplicationOpeningDate())) {
                        System.out.println("Closing date must be after opening date.");
                        return;
                    }
                    project.setApplicationClosingDate(newClosingDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Please use yyyy-MM-dd.");
                    return;
                }
                break;
            case 5:
                editFlatTypes(project);
                break;
            case 6:
                System.out.print("Enter new number of HDB Officer slots (max 10): ");
                try {
                    int newSlots = Integer.parseInt(sc.nextLine());
                    if (newSlots < 0 || newSlots < project.getHDBOfficers().size()) {
                        System.out.println("HDB Officer slots must be at least the number of current officers.");
                        return;
                    }
                    if (newSlots > 10) {
                        System.out.println("HDB Officer slots cannot exceed 10.");
                        return;
                    }
                    project.setHDBOfficerSlots(newSlots);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    return;
                }
                break;
            case 7:
                System.out.print("Set project visibility (true/false): ");
                try {
                    boolean newVisibility = Boolean.parseBoolean(sc.nextLine());
                    project.setVisible(newVisibility);
                } catch (Exception e) {
                    System.out.println("Invalid input. Please enter true or false.");
                    return;
                }
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        DataStore.saveData();
        System.out.println("Project updated successfully!");
    }

    /**
     * Edit flat types for a project
     * @param project The project to edit
     */
    private void editFlatTypes(BTOProject project) {
        System.out.println("\nCurrent flat types:");
        Map<FlatType, FlatTypeDetails> flatTypes = project.getFlatTypes();
        
        for (Map.Entry<FlatType, FlatTypeDetails> entry : flatTypes.entrySet()) {
            System.out.println(entry.getKey().getDisplayName() + ": " + entry.getValue().getUnits() + " units, $" + entry.getValue().getPrice());
        }
        
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Edit 2-Room units");
        System.out.println("2. Edit 3-Room units");
        System.out.println("0. Cancel");
        System.out.print("Enter your choice: ");
        
        int choice = 0;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        switch (choice) {
            case 0:
                return;
            case 1:
                System.out.print("Enter new number of 2-Room units: ");
                try {
                    int newUnits = Integer.parseInt(sc.nextLine());
                    if (newUnits < 0) {
                        System.out.println("Number of units cannot be negative.");
                        return;
                    }
                    
                    if (newUnits > 0) {
                        System.out.print("Enter new price for 2-Room units: ");
                        double newPrice = Double.parseDouble(sc.nextLine());
                        flatTypes.put(FlatType.TWO_ROOM, new FlatTypeDetails(newUnits, newPrice));
                    } else {
                        flatTypes.remove(FlatType.TWO_ROOM);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    return;
                }
                break;
            case 2:
                System.out.print("Enter new number of 3-Room units: ");
                try {
                    int newUnits = Integer.parseInt(sc.nextLine());
                    if (newUnits < 0) {
                        System.out.println("Number of units cannot be negative.");
                        return;
                    }
                    
                    if (newUnits > 0) {
                        System.out.print("Enter new price for 3-Room units: ");
                        double newPrice = Double.parseDouble(sc.nextLine());
                        flatTypes.put(FlatType.THREE_ROOM, new FlatTypeDetails(newUnits, newPrice));
                    } else {
                        flatTypes.remove(FlatType.THREE_ROOM);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    return;
                }
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        project.setFlatTypes(flatTypes);
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
        displayProjectDetails(project);
        
        System.out.print("Are you sure you want to delete this project? (yes/no): ");
        String confirmation = sc.nextLine().toLowerCase();
        
        if (confirmation.equals("yes")) {
            DataStore.getBTOProjectsData().remove(project.getProjectName());
            DataStore.saveData();
            System.out.println("Project deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * View all BTO projects
     */
    private void viewAllProjects() {
        System.out.println("\n===== All BTO Projects =====");
        
        List<BTOProject> projects = new ArrayList<>(DataStore.getBTOProjectsData().values());
        
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }
        
        for (int i = 0; i < projects.size(); i++) {
            System.out.println("\nProject " + (i + 1) + ":");
            displayProjectDetails(projects.get(i));
        }
    }

    /**
     * View projects created by the current HDB Manager
     */
    private void viewMyProjects() {
        System.out.println("\n===== My BTO Projects =====");
        
        List<BTOProject> myProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .collect(Collectors.toList());
        
        if (myProjects.isEmpty()) {
            System.out.println("You have not created any projects.");
            return;
        }
        
        for (int i = 0; i < myProjects.size(); i++) {
            System.out.println("\nProject " + (i + 1) + ":");
            displayProjectDetails(myProjects.get(i));
        }
    }

    /**
     * Toggle the visibility of a project
     */
    private void toggleProjectVisibility() {
        System.out.println("\n===== Toggle Project Visibility =====");
        
        // Get project to toggle
        BTOProject project = selectProject("Select project to toggle visibility: ", true);
        if (project == null) {
            return;
        }
        
        System.out.println("\nCurrent project details:");
        displayProjectDetails(project);
        
        System.out.println("Current visibility: " + (project.isVisible() ? "Visible" : "Hidden"));
        System.out.print("Set visibility to (true/false): ");
        
        try {
            boolean newVisibility = Boolean.parseBoolean(sc.nextLine());
            project.setVisible(newVisibility);
            DataStore.saveData();
            System.out.println("Project visibility updated successfully!");
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter true or false.");
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
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter a valid registration number.");
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
            registration.setStatus(RegistrationStatus.APPROVED);
            project.addHDBOfficer(officer);
            officer.addHandledProject(project);
            System.out.println("Registration approved successfully!");
        } else {
            registration.setStatus(RegistrationStatus.REJECTED);
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
        
        for (int i = 0; i < applications.size(); i++) {
            BTOApplication application = applications.get(i);
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
            System.out.println("\nApplication " + (i + 1) + ":");
            System.out.println("Application ID: " + application.getApplicationId());
            System.out.println("Applicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
            System.out.println("Project: " + application.getProject().getProjectName());
            System.out.println("Flat Type: " + (application.getFlatType() != null ? application.getFlatType().getDisplayName() : "Not specified"));
        }
        
        // Select application to approve/reject
        System.out.print("Enter application number to approve/reject (0 to cancel): ");
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
        
        // Check if flat type is specified
        if (application.getFlatType() == null) {
            System.out.println("Flat type must be specified before approval.");
            return;
        }
        
        // Check if there are units available for the selected flat type
        FlatTypeDetails flatTypeDetails = project.getFlatTypes().get(application.getFlatType());
        if (flatTypeDetails == null || flatTypeDetails.getUnits() <= 0) {
            System.out.println("No units available for the selected flat type.");
            return;
        }
        
        System.out.print("Approve application? (yes/no): ");
        String approval = sc.nextLine().toLowerCase();
        
        if (approval.equals("yes")) {
            application.setStatus(BTOApplicationStatus.SUCCESSFUL);
            // Decrease the number of available units
            flatTypeDetails.setUnits(flatTypeDetails.getUnits() - 1);
            System.out.println("Application approved successfully!");
        } else {
            application.setStatus(BTOApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application rejected.");
        }
        
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
        
        // Get successful applications for these projects
        List<BTOApplication> successfulApplications = DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> myProjects.contains(application.getProject()) && 
                                 application.getStatus() == BTOApplicationStatus.SUCCESSFUL)
            .collect(Collectors.toList());
        
        if (successfulApplications.isEmpty()) {
            System.out.println("No successful BTO applications found for your projects.");
            return;
        }
        
        // Display successful applications
        for (int i = 0; i < successfulApplications.size(); i++) {
            BTOApplication application = successfulApplications.get(i);
            System.out.println("\nApplication " + (i + 1) + ":");
            System.out.println("Application ID: " + application.getApplicationId());
            System.out.println("Applicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
            System.out.println("Project: " + application.getProject().getProjectName());
            System.out.println("Flat Type: " + application.getFlatType().getDisplayName());
        }
        
        // Select application to approve/reject withdrawal
        System.out.print("Enter application number to approve/reject withdrawal (0 to cancel): ");
        int choice = 0;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > successfulApplications.size()) {
                System.out.println("Invalid application number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        BTOApplication application = successfulApplications.get(choice - 1);
        BTOProject project = application.getProject();
        
        System.out.print("Approve withdrawal? (yes/no): ");
        String approval = sc.nextLine().toLowerCase();
        
        if (approval.equals("yes")) {
            // Increase the number of available units
            FlatTypeDetails flatTypeDetails = project.getFlatTypes().get(application.getFlatType());
            flatTypeDetails.setUnits(flatTypeDetails.getUnits() + 1);
            
            // Remove the application
            DataStore.getBTOApplicationsData().remove(application.getApplicationId());
            System.out.println("Withdrawal approved successfully!");
        } else {
            System.out.println("Withdrawal rejected.");
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
        
        // Get successful applications for these projects
        List<BTOApplication> successfulApplications = DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> myProjects.contains(application.getProject()) && 
                                 application.getStatus() == BTOApplicationStatus.SUCCESSFUL)
            .collect(Collectors.toList());
        
        if (successfulApplications.isEmpty()) {
            System.out.println("No successful BTO applications found for your projects.");
            return;
        }
        
        System.out.println("\nFilter options:");
        System.out.println("1. All applicants");
        System.out.println("2. By project");
        System.out.println("3. By flat type");
        System.out.println("4. By marital status");
        System.out.println("5. By age range");
        System.out.print("Enter your choice: ");
        
        int choice = 0;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        List<BTOApplication> filteredApplications = new ArrayList<>(successfulApplications);
        
        switch (choice) {
            case 1:
                // No filtering needed
                break;
            case 2:
                // Filter by project
                BTOProject selectedProject = selectProject("Select project to filter by: ", true);
                if (selectedProject == null) {
                    return;
                }
                filteredApplications = filteredApplications.stream()
                    .filter(application -> application.getProject().equals(selectedProject))
                    .collect(Collectors.toList());
                break;
            case 3:
                // Filter by flat type
                System.out.println("Select flat type to filter by:");
                System.out.println("1. 2-Room");
                System.out.println("2. 3-Room");
                System.out.print("Enter your choice: ");
                
                int flatTypeChoice = 0;
                try {
                    flatTypeChoice = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    return;
                }
                
                final FlatType selectedFlatType;
                if (flatTypeChoice == 1) {
                    selectedFlatType = FlatType.TWO_ROOM;
                } else if (flatTypeChoice == 2) {
                    selectedFlatType = FlatType.THREE_ROOM;
                } else {
                    System.out.println("Invalid choice.");
                    return;
                }
                
                filteredApplications = filteredApplications.stream()
                    .filter(application -> application.getFlatType() == selectedFlatType)
                    .collect(Collectors.toList());
                break;
            case 4:
                // Filter by marital status
                System.out.println("Select marital status to filter by:");
                System.out.println("1. Married");
                System.out.println("2. Single");
                System.out.print("Enter your choice: ");
                
                int maritalStatusChoice = 0;
                try {
                    maritalStatusChoice = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    return;
                }
                
                final MaritalStatus selectedMaritalStatus;
                if (maritalStatusChoice == 1) {
                    selectedMaritalStatus = MaritalStatus.MARRIED;
                } else if (maritalStatusChoice == 2) {
                    selectedMaritalStatus = MaritalStatus.SINGLE;
                } else {
                    System.out.println("Invalid choice.");
                    return;
                }
                
                filteredApplications = filteredApplications.stream()
                    .filter(application -> application.getApplicant().getMaritalStatus() == selectedMaritalStatus)
                    .collect(Collectors.toList());
                break;
            case 5:
                // Filter by age range
                System.out.print("Enter minimum age: ");
                int minAge = 0;
                try {
                    minAge = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    return;
                }
                
                System.out.print("Enter maximum age: ");
                int maxAge = 0;
                try {
                    maxAge = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    return;
                }
                
                if (minAge > maxAge) {
                    System.out.println("Minimum age cannot be greater than maximum age.");
                    return;
                }
                
                final int finalMinAge = minAge;
                final int finalMaxAge = maxAge;
                filteredApplications = filteredApplications.stream()
                    .filter(application -> {
                        int age = application.getApplicant().getAge();
                        return age >= finalMinAge && age <= finalMaxAge;
                    })
                    .collect(Collectors.toList());
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }
        
        if (filteredApplications.isEmpty()) {
            System.out.println("No applications match the selected filter.");
            return;
        }
        
        // Display the report
        System.out.println("\n===== Applicant Report =====");
        for (BTOApplication application : filteredApplications) {
            System.out.println("\nApplicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
            System.out.println("Age: " + application.getApplicant().getAge());
            System.out.println("Marital Status: " + application.getApplicant().getMaritalStatus().getDisplayName());
            System.out.println("Project: " + application.getProject().getProjectName());
            System.out.println("Flat Type: " + application.getFlatType().getDisplayName());
        }
    }

    /**
     * View all enquiries
     */
    private void viewAllEnquiries() {
        System.out.println("\n===== All Enquiries =====");
        System.out.println("This feature is not implemented yet.");
    }

    /**
     * View and reply to project enquiries
     */
    private void viewAndReplyToProjectEnquiries() {
        System.out.println("\n===== View and Reply to Project Enquiries =====");
        System.out.println("This feature is not implemented yet.");
    }

    /**
     * Check if the HDB Manager is already handling a project within an application period
     * @return true if handling an active project, false otherwise
     */
    private boolean isHandlingActiveProject() {
        LocalDate today = LocalDate.now();
        
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBManager().equals(hdbManager))
            .anyMatch(project -> 
                !today.isBefore(project.getApplicationOpeningDate()) && 
                !today.isAfter(project.getApplicationClosingDate())
            );
    }

    /**
     * Select a project from a list
     * @param prompt The prompt to display
     * @param myProjectsOnly Whether to show only projects created by this HDB Manager
     * @return The selected project, or null if none selected
     */
    private BTOProject selectProject(String prompt, boolean myProjectsOnly) {
        List<BTOProject> projects;
        
        if (myProjectsOnly) {
            projects = DataStore.getBTOProjectsData().values().stream()
                .filter(project -> project.getHDBManager().equals(hdbManager))
                .collect(Collectors.toList());
        } else {
            projects = new ArrayList<>(DataStore.getBTOProjectsData().values());
        }
        
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return null;
        }
        
        System.out.println("\nAvailable projects:");
        for (int i = 0; i < projects.size(); i++) {
            BTOProject project = projects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        }
        
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

    /**
     * Display the details of a project
     * @param project The project to display
     */
    private void displayProjectDetails(BTOProject project) {
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Opening Date: " + project.getApplicationOpeningDate());
        System.out.println("Application Closing Date: " + project.getApplicationClosingDate());
        System.out.println("HDB Manager: " + project.getHDBManager().getName() + " (" + project.getHDBManager().getNric() + ")");
        System.out.println("HDB Officer Slots: " + project.getHDBOfficerSlots());
        System.out.println("HDB Officers: " + project.getHDBOfficers().size());
        System.out.println("Visible: " + (project.isVisible() ? "Yes" : "No"));
        
        System.out.println("Flat Types:");
        for (Map.Entry<FlatType, FlatTypeDetails> entry : project.getFlatTypes().entrySet()) {
            System.out.println("  " + entry.getKey().getDisplayName() + ": " + entry.getValue().getUnits() + " units, $" + entry.getValue().getPrice());
        }
    }
}
