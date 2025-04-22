package controllers;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import models.Applicant;
import models.BTOApplication;
import models.BTOProject;
import models.Enquiry;
import models.FlatTypeDetails;
import models.HDBOfficer;
import models.HDBOfficerRegistration;
import models.ProjectFilter;
import services.BTOProjectService;
import services.EnquiryService;
import stores.AuthStore;
import stores.DataStore;
import stores.FilterStore;
import utils.TextDecorationUtils;
import view.BTOApplicationView;
import view.BTOProjectAvailableView;
import view.BTOProjectFilterView;

/**
 * Controller for handling HDB Officer operations in the BTO system.
 * 
 * This controller manages the user interface and business logic for HDB Officer users,
 * including viewing and joining BTO projects, processing flat booking requests,
 * generating booking receipts, and handling their own BTO applications. It extends
 * the ApplicantController class to inherit applicant functions while adding
 * officer-specific capabilities.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class HDBOfficerController extends ApplicantController {

    private static final Scanner sc = new Scanner(System.in);
    private BTOProjectService projectService;
    private BTOProjectAvailableView projectView;
    private BTOApplicationView applicationView;
    private BTOProjectFilterView filterView;
    private final EnquiryService enquiryService;

    /**
     * Constructs a new HDBOfficerController with the necessary services and views.
     */
    public HDBOfficerController() {
        this.projectService = new BTOProjectService();
        this.projectView = new BTOProjectAvailableView();
        this.applicationView = new BTOApplicationView();
        this.filterView = new BTOProjectFilterView();
        this.enquiryService = new EnquiryService();
    }

    /**
     * Starts the HDB Officer menu system, displaying options and handling user selections.
     * This method overrides the start method in ApplicantController to present
     * officer-specific menu options.
     */
    @Override
    public void start() {
        int choice;

do {
            System.out.println();
            System.out.println("==========================================");
            System.out.println(TextDecorationUtils.boldText("Hi, " + AuthStore.getCurrentUser().getName() + "!"));
            System.out.println("==========================================");
            System.out.println();
            
            System.out.println(TextDecorationUtils.underlineText("SETTINGS"));
            System.out.println("└─ 1. Change Password");
            System.out.println();

            System.out.println(TextDecorationUtils.underlineText("BTO PROJECTS"));
            System.out.println("└─ 2. View Available BTO Projects");
            System.out.println("└─ 3. Apply for a BTO Project");
            System.out.println("└─ 4. View My BTO Applications");
            System.out.println();

            System.out.println(TextDecorationUtils.underlineText("BTO OFFICER"));
            System.out.println("└─ 5. View Joinable BTO Projects");
            System.out.println("└─ 6. Join BTO Project as Officer");
            System.out.println("└─ 7. View Joined BTO Projects");
            System.out.println("└─ 8. View HDB Officer Registrations");
            System.out.println("└─ 9. Process Flat Booking Requests");
            System.out.println("└─ 10. Generate Booking Receipt");
            System.out.println();

            System.out.println(TextDecorationUtils.underlineText("LOGOUT"));
            System.out.println("└─ 0. Logout");
            System.out.println();
            System.out.println("==========================================");
            System.out.print("Enter your choice: ");

            try {
                String input = sc.nextLine().trim();
                choice = Integer.parseInt(input);
                
                if (choice < 0 || choice > 10) {
                    System.out.println("Invalid choice. Please enter 0-10!");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    if (changePassword()) {
                        System.out.println("Restarting session...");
                        AuthController.endSession();
                        return;
                    }
                    break;
                case 2:
                    viewAvailableBTOProjects();
                    break;
                case 3:
                    applyForBTOProject();
                    break;
                case 4:
                    viewMyBTOApplications();
                    break;
                case 5:
                    viewJoinableBTOProjects();
                    break;
                case 6:
                    joinBTOProjectAsOfficer();
                    break;
                case 7:
                    viewJoinedBTOProjects();
                    break;
                case 8:
                    viewHDBOfficerRegistrations();
                    break;
                case 9:
                    processFlatBookingRequests();
                    break;
                case 10:
                    generateBookingReceipt();
                    break;
                case 0:
                    System.out.println("Logging out...");
                    AuthController.startSession();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }
    
    /**
     * Displays all BTO projects that the HDB officer can join.
     * Filters projects based on user preferences and allows officers
     * to view details of potential projects they could join.
     */
    private void viewJoinableBTOProjects() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        
        // Get the user's filter settings
        ProjectFilter filter = FilterStore.getProjectFilter(hdbOfficer);
        
        // Apply filters to get joinable projects
        List<BTOProject> filteredProjects = projectService.getJoinableProjects(hdbOfficer, filter);
        
        if (filteredProjects.isEmpty()) {
            System.out.println("\nNo joinable BTO projects match your current filters.");
            
            // Offer to reset filters
            System.out.print("Would you like to reset filters and view all joinable projects? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();
            
            if (response.equals("yes")) {
                filter.resetFilters();
                FilterStore.setProjectFilter(hdbOfficer, filter);
                filteredProjects = projectService.getJoinableProjects(hdbOfficer);
                
                if (filteredProjects.isEmpty()) {
                    System.out.println("\nNo joinable BTO projects at the moment.");
                    return;
                }
            } else {
                return;
            }
        }
        
        // Display projects with filter info
        filterView.displayFilteredProjects(filteredProjects, filter);
        
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
                        filter = filterView.showFilterOptions(filter);
                        // Apply updated filters
                        filteredProjects = projectService.getJoinableProjects(hdbOfficer, filter);
                        // Display projects with updated filters
                        filterView.displayFilteredProjects(filteredProjects, filter);
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
     * View details for a selected project.
     * 
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
            projectView.displayProjectInfo(selectedProject);
            
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    /**
     * Allows the HDB officer to join a BTO project.
     * 
     * Checks for project eligibility, application period validity,
     * and potential conflicts with other projects before submitting
     * a registration request for manager approval.
     */
    private void joinBTOProjectAsOfficer() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        
        // Check if officer has projects with overlapping application periods
        List<BTOProject> handledProjects = hdbOfficer.getHandledProjects();
        
        // Get filter settings
        ProjectFilter filter = FilterStore.getProjectFilter(hdbOfficer);
        
        // Get filtered joinable projects
        List<BTOProject> joinableProjects = projectService.getJoinableProjects(hdbOfficer, filter);
        
        if (joinableProjects.isEmpty()) {
            System.out.println("\nNo joinable BTO projects match your current filters.");
            
            // Offer to reset filters
            System.out.print("Would you like to reset filters and view all joinable projects? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();
            
            if (response.equals("yes")) {
                filter.resetFilters();
                FilterStore.setProjectFilter(hdbOfficer, filter);
                joinableProjects = projectService.getJoinableProjects(hdbOfficer);
                
                if (joinableProjects.isEmpty()) {
                    System.out.println("\nNo joinable BTO projects at the moment.");
                    return;
                }
            } else {
                return;
            }
        }
        
        // Display filtered projects
        filterView.displayFilteredProjects(joinableProjects, filter);
        
        // Select project to join
        System.out.print("\nEnter project number to join (1-" + joinableProjects.size() + ") or 0 to cancel: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(sc.nextLine().trim());
            if (projectChoice == 0) {
                return;
            }
            if (projectChoice < 1 || projectChoice > joinableProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        BTOProject selectedProject = joinableProjects.get(projectChoice - 1);

        // Check if project is within application period
        LocalDate today = LocalDate.now();
        if (today.isBefore(selectedProject.getApplicationOpeningDate()) || 
            today.isAfter(selectedProject.getApplicationClosingDate())) {
            System.out.println("\nThis project is not within its application period.");
            System.out.println("Application period: " + selectedProject.getApplicationOpeningDate() + 
                             " to " + selectedProject.getApplicationClosingDate());
            return;
        }

        // Check for overlapping application periods with handled projects
        for (BTOProject handledProject : handledProjects) {
            if (hasOverlappingPeriod(selectedProject, handledProject)) {
                System.out.println("\nYou cannot join this project because its application period overlaps with project: " + 
                                 handledProject.getProjectName());
                System.out.println("Existing project period: " + handledProject.getApplicationOpeningDate() + 
                                 " to " + handledProject.getApplicationClosingDate());
                return;
            }
        }
        
        // Confirm joining
        System.out.print("Confirm joining " + selectedProject.getProjectName() + " as an officer? (yes/no): ");
        String confirmation = sc.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("yes")) {
            System.out.println("Registration cancelled.");
            return;
        }
        
        // Create a new HDBOfficerRegistration with pending status
        HDBOfficerRegistration registration = new HDBOfficerRegistration(hdbOfficer, selectedProject);
        Map<String, HDBOfficerRegistration> registrations = DataStore.getHDBOfficerRegistrationsData();
        registrations.put(registration.getRegistrationId(), registration);
        DataStore.setHDBOfficerRegistrationsData(registrations);
        
        System.out.println("Registration request submitted successfully. Please wait for HDB Manager's approval.");
    }

    /**
     * Checks if two projects have overlapping application periods.
     * 
     * @param project1 The first project to check
     * @param project2 The second project to check
     * @return true if the projects have overlapping application periods, false otherwise
     */
    private boolean hasOverlappingPeriod(BTOProject project1, BTOProject project2) {
        return !project1.getApplicationClosingDate().isBefore(project2.getApplicationOpeningDate()) &&
               !project2.getApplicationClosingDate().isBefore(project1.getApplicationOpeningDate());
    }

    /**
     * Displays all BTO projects that the HDB officer has joined.
     * 
     * Filters projects based on user preferences and allows the officer
     * to view details of projects they are currently handling.
     */
    private void viewJoinedBTOProjects() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        
        // Get the user's filter settings
        ProjectFilter filter = FilterStore.getProjectFilter(hdbOfficer);
        
        // Apply filters to get joined projects
        List<BTOProject> filteredProjects = projectService.getJoinedProjects(hdbOfficer, filter);
        
        if (filteredProjects.isEmpty()) {
            System.out.println("\nNo joined BTO projects match your current filters.");
            
            // Offer to reset filters
            System.out.print("Would you like to reset filters and view all joined projects? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();
            
            if (response.equals("yes")) {
                filter.resetFilters();
                FilterStore.setProjectFilter(hdbOfficer, filter);
                filteredProjects = projectService.getJoinedProjects(hdbOfficer);
                
                if (filteredProjects.isEmpty()) {
                    System.out.println("\nYou have not joined any BTO projects.");
                    return;
                }
            } else {
                return;
            }
        }
        
        // Display projects with filter info
        filterView.displayFilteredProjects(filteredProjects, filter);
        
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
                        filter = filterView.showFilterOptions(filter);
                        // Apply updated filters
                        filteredProjects = projectService.getJoinedProjects(hdbOfficer, filter);
                        // Display projects with updated filters
                        filterView.displayFilteredProjects(filteredProjects, filter);
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
     * Displays available BTO projects for personal application.
     * This method overrides the implementation from ApplicantController.
     */
    @Override
    protected void viewAvailableBTOProjects() {
        // We're reusing the implementation from ApplicantController that already has filtering
        super.viewAvailableBTOProjects();
    }

    /**
     * Allows an HDB Officer to apply for a BTO project as a personal applicant.
     * 
     * This method overrides the implementation from ApplicantController to include
     * additional checks for officer-specific constraints, such as not applying for
     * projects they are assigned to as an officer.
     */
    @Override
    protected void applyForBTOProject() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        
        // Check if applicant already has an application
        if (projectService.hasExistingApplication(hdbOfficer)) {
            System.out.println("\nYou already have an existing BTO application. Only one application is allowed at a time.");
            return;
        }
        
        // Get filter settings
        ProjectFilter filter = FilterStore.getProjectFilter(hdbOfficer);
        
        // Get filtered available projects
        List<BTOProject> availableProjects = projectService.getAvailableProjects(hdbOfficer, filter);
        
        if (availableProjects.isEmpty()) {
            System.out.println("\nNo available BTO projects match your current filters.");
            
            // Offer to reset filters
            System.out.print("Would you like to reset filters and view all available projects? (yes/no): ");
            String response = sc.nextLine().trim().toLowerCase();
            
            if (response.equals("yes")) {
                filter.resetFilters();
                FilterStore.setProjectFilter(hdbOfficer, filter);
                availableProjects = projectService.getAvailableProjects(hdbOfficer);
                
                if (availableProjects.isEmpty()) {
                    System.out.println("\nNo available BTO projects at the moment.");
                    return;
                }
            } else {
                return;
            }
        }
        
        // Display filtered projects
        filterView.displayFilteredProjects(availableProjects, filter);
        
        // Select project to apply for
        System.out.print("\nEnter project number to apply for (1-" + availableProjects.size() + ") or 0 to cancel: ");
        int projectChoice;
        try {
            projectChoice = Integer.parseInt(sc.nextLine().trim());
            if (projectChoice == 0) {
                return;
            }
            if (projectChoice < 1 || projectChoice > availableProjects.size()) {
                System.out.println("Invalid project number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        BTOProject selectedProject = availableProjects.get(projectChoice - 1);

        // Check if the HDB officer is already assigned to the project
        if (!projectService.canOfficerApplyForProject(selectedProject, hdbOfficer)) {
            System.out.println("You cannot apply for a project that you are assigned to as an HDB officer.");
            return;
        }
        
        // Confirm application
        System.out.print("Confirm application for " + selectedProject.getProjectName() + "? (yes/no): ");
        String confirmation = sc.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("yes")) {
            System.out.println("Application cancelled.");
            return;
        }

        BTOApplication application = new BTOApplication(hdbOfficer, selectedProject);
        projectService.applyForBTOProject(application);

        System.out.println("Application submitted successfully. An HDB officer will contact you for flat booking if your application is successful.");
    }

    /**
     * Displays the HDB Officer's personal BTO applications.
     * This method overrides the implementation from ApplicantController.
     */
    @Override
    protected void viewMyBTOApplications() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        List<BTOApplication> applications = projectService.getApplicationsByApplicant(hdbOfficer);

        if (applications.isEmpty()) {
            System.out.println("\nYou have no BTO applications at the moment.");
            return;
        }

        System.out.println("\nYour BTO Applications:");
        for (BTOApplication application : applications) {
            applicationView.displayApplicationInfo(application);
        }
    }

    /**
     * Displays the HDB Officer's project registration requests and their status.
     */
    private void viewHDBOfficerRegistrations() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        List<HDBOfficerRegistration> registrations = DataStore.getHDBOfficerRegistrationsData().values().stream()
            .filter(reg -> reg.getHDBOfficer().equals(hdbOfficer))
            .collect(Collectors.toList());

        if (registrations.isEmpty()) {
            System.out.println("\nYou have no HDB officer registrations at the moment.");
            return;
        }

        System.out.println("\nYour HDB Officer Registrations:");
        for (HDBOfficerRegistration registration : registrations) {
            System.out.println("Project Name: " + registration.getProject().getProjectName());
            System.out.println("Status: " + registration.getStatus());
            System.out.println("----------------------------------------");
        }
    }

    /**
     * View and reply to enquiries for projects the officer is assigned to.
     * 
     * Allows officers to respond to applicant enquiries about BTO projects
     * they are handling.
     */
    protected void viewAndReplyToEnquiries() {
        HDBOfficer officer = (HDBOfficer) AuthStore.getCurrentUser();
        
        // Get projects the officer is assigned to
        List<BTOProject> assignedProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBOfficers().contains(officer))
            .collect(Collectors.toList());
        
        if (assignedProjects.isEmpty()) {
            System.out.println("\nYou are not assigned to any projects.");
            return;
        }
        
        // Get enquiries for assigned projects using the service
        List<Enquiry> projectEnquiries = new ArrayList<>();
        for (BTOProject project : assignedProjects) {
            projectEnquiries.addAll(enquiryService.getEnquiriesByProject(project));
        }
        
        if (projectEnquiries.isEmpty()) {
            System.out.println("\nThere are no enquiries for your assigned projects.");
            return;
        }
        
        // Display enquiries
        System.out.println("\n===== Project Enquiries =====");
        for (int i = 0; i < projectEnquiries.size(); i++) {
            Enquiry enquiry = projectEnquiries.get(i);
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
     * Process flat booking requests from applicants.
     * 
     * Displays successful applications for projects the officer is assigned to
     * and allows the officer to approve flat bookings, updating application status
     * and reducing available unit counts.
     */
    private void processFlatBookingRequests() {
        HDBOfficer officer = (HDBOfficer) AuthStore.getCurrentUser();
        
        // Get projects the officer is assigned to
        List<BTOProject> assignedProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBOfficers().contains(officer))
            .collect(Collectors.toList());
        
        if (assignedProjects.isEmpty()) {
            System.out.println("\nYou are not assigned to any projects.");
            return;
        }
        
        // Get successful applications for assigned projects
        List<BTOApplication> successfulApplications = DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> assignedProjects.contains(application.getProject()) && 
                                 application.getStatus() == BTOApplicationStatus.SUCCESSFUL)
            .collect(Collectors.toList());
        
        if (successfulApplications.isEmpty()) {
            System.out.println("\nNo successful applications found for your assigned projects.");
            return;
        }
        
        // Display successful applications
        System.out.println("\n===== Flat Booking Requests =====");
        for (int i = 0; i < successfulApplications.size(); i++) {
            BTOApplication application = successfulApplications.get(i);
            System.out.println("\n" + (i + 1) + ". Application ID: " + application.getApplicationId());
            System.out.println("   Applicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
            System.out.println("   Project: " + application.getProject().getProjectName());
            System.out.println("   Flat Type: " + application.getFlatType().getDisplayName());
            System.out.println("----------------------------------------");
        }
        
        // Select application to process
        System.out.print("\nEnter application number to process (0 to cancel): ");
        int choice;
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
        
        BTOApplication selectedApplication = successfulApplications.get(choice - 1);
        BTOProject project = selectedApplication.getProject();
        Applicant applicant = (Applicant) selectedApplication.getApplicant();
        
        // Check if applicant already has a booked flat
        boolean hasBookedFlat = DataStore.getBTOApplicationsData().values().stream()
            .anyMatch(app -> app.getApplicant().equals(applicant) && 
                           app.getStatus() == BTOApplicationStatus.BOOKED);
        
        if (hasBookedFlat) {
            System.out.println("\nThis applicant already has a booked flat.");
            return;
        }
        
        // Check if flat type is still available
        FlatTypeDetails flatTypeDetails = project.getFlatTypes().get(selectedApplication.getFlatType());
        if (flatTypeDetails == null || flatTypeDetails.getUnits() <= 0) {
            System.out.println("\nNo units available for the selected flat type.");
            return;
        }
        
        // Confirm booking
        System.out.print("Confirm booking for " + applicant.getName() + "? (yes/no): ");
        String confirmation = sc.nextLine().toLowerCase();
        
        if (!confirmation.equals("yes")) {
            System.out.println("Booking cancelled.");
            return;
        }
        
        // Update application status to BOOKED
        selectedApplication.setStatus(BTOApplicationStatus.BOOKED);
        
        // Decrease available units
        flatTypeDetails.setUnits(flatTypeDetails.getUnits() - 1);
        
        // Save changes
        DataStore.saveData();
        
        System.out.println("\nFlat booked successfully!");
        System.out.println("Application ID: " + selectedApplication.getApplicationId());
        System.out.println("Applicant: " + applicant.getName() + " (" + applicant.getNric() + ")");
        System.out.println("Project: " + project.getProjectName());
        System.out.println("Flat Type: " + selectedApplication.getFlatType().getDisplayName());
    }

    /**
     * Generate receipt for an applicant's flat booking.
     * 
     * Creates and displays a formatted receipt for a booked application,
     * including applicant details, project information, and flat specifications.
     */
    private void generateBookingReceipt() {
        HDBOfficer officer = (HDBOfficer) AuthStore.getCurrentUser();
        
        // Get projects the officer is assigned to
        List<BTOProject> assignedProjects = DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBOfficers().contains(officer))
            .collect(Collectors.toList());
        
        if (assignedProjects.isEmpty()) {
            System.out.println("\nYou are not assigned to any projects.");
            return;
        }
        
        // Get booked applications for assigned projects
        List<BTOApplication> bookedApplications = DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> assignedProjects.contains(application.getProject()) && 
                                application.getStatus() == BTOApplicationStatus.BOOKED)
            .collect(Collectors.toList());
        
        if (bookedApplications.isEmpty()) {
            System.out.println("\nNo booked applications found for your assigned projects.");
            return;
        }
        
        // Display booked applications
        System.out.println("\n===== Booked Applications =====");
        for (int i = 0; i < bookedApplications.size(); i++) {
            BTOApplication application = bookedApplications.get(i);
            System.out.println("\n" + (i + 1) + ". Application ID: " + application.getApplicationId());
            System.out.println("   Applicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
            System.out.println("   Project: " + application.getProject().getProjectName());
            System.out.println("   Flat Type: " + application.getFlatType().getDisplayName());
            System.out.println("----------------------------------------");
        }
        
        // Select application to generate receipt for
        System.out.print("\nEnter application number to generate receipt (0 to cancel): ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > bookedApplications.size()) {
                System.out.println("Invalid application number.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }
        
        BTOApplication selectedApplication = bookedApplications.get(choice - 1);
        BTOProject project = selectedApplication.getProject();
        Applicant applicant = (Applicant) selectedApplication.getApplicant();
        FlatType flatType = selectedApplication.getFlatType();
        FlatTypeDetails flatTypeDetails = project.getFlatTypes().get(flatType);
        
        // Generate and display the receipt
        System.out.println("\n========================================");
        System.out.println(TextDecorationUtils.boldText("          FLAT BOOKING RECEIPT          "));
        System.out.println("========================================");
        System.out.println("Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        System.out.println("Application ID: " + selectedApplication.getApplicationId());
        System.out.println("HDB Officer: " + officer.getName());
        System.out.println("----------------------------------------");
        
        System.out.println(TextDecorationUtils.boldText("APPLICANT DETAILS"));
        System.out.println("Name: " + applicant.getName());
        System.out.println("NRIC: " + applicant.getNric());
        System.out.println("Age: " + applicant.getAge());
        System.out.println("Marital Status: " + applicant.getMaritalStatus().getDisplayName());
        System.out.println("----------------------------------------");
        
        System.out.println(TextDecorationUtils.boldText("PROJECT DETAILS"));
        System.out.println("Project Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("HDB Manager: " + project.getHDBManager().getName());
        System.out.println("----------------------------------------");
        
        System.out.println(TextDecorationUtils.boldText("FLAT DETAILS"));
        System.out.println("Flat Type: " + flatType.getDisplayName());
        System.out.println("Price: $" + flatTypeDetails.getPrice());
        System.out.println("----------------------------------------");
        
        System.out.println(TextDecorationUtils.boldText("BOOKING STATUS:") + " CONFIRMED");
        System.out.println("========================================");

        System.out.print("\nPress Enter to continue...");
        sc.nextLine();
}
}