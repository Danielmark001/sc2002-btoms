package controllers;

import java.util.Scanner;
import stores.AuthStore;
import utils.TextDecorationUtils;
import models.HDBOfficer;
import models.BTOProject;
import models.BTOApplication;
import models.User;
import services.BTOProjectService;
import view.BTOProjectAvailableView;
import view.BTOApplicationView;
import java.util.List;
import java.util.Map;
import stores.DataStore;
import models.HDBOfficerRegistration;
import java.time.LocalDate;
import java.util.stream.Collectors;
import models.Enquiry;
import services.EnquiryService;
import java.util.ArrayList;
import models.Applicant;
import models.FlatTypeDetails;
import enumeration.BTOApplicationStatus;

public class HDBOfficerController extends ApplicantController {

    private static final Scanner sc = new Scanner(System.in);
    private BTOProjectService projectService;
    private BTOProjectAvailableView projectView;
    private BTOApplicationView applicationView;
    private final EnquiryService enquiryService;

    public HDBOfficerController() {
        this.projectService = new BTOProjectService();
        this.projectView = new BTOProjectAvailableView();
        this.applicationView = new BTOApplicationView();
        this.enquiryService = new EnquiryService();
    }

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
            System.out.println();

            System.out.println(TextDecorationUtils.underlineText("LOGOUT"));
            System.out.println("└─ 0. Logout");
            System.out.println();
            System.out.println("==========================================");
            System.out.print("Enter your choice: ");

            String input = sc.nextLine();
            if (input.matches("[0-9]+")) {
                choice = Integer.parseInt(input);
                if (choice < 0 || choice > 9) {
                    System.out.println("Invalid input. Please enter 0-9!");
                    continue;
                }
            } else {
                System.out.println("Invalid input. Please enter an integer.\n");
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
                case 0:
                    System.out.println("Logging out...");
                    AuthController.endSession();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }

    /**
     * Displays all BTO projects that the HDB officer can join
     */
    private void viewJoinableBTOProjects() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        List<BTOProject> joinableProjects = projectService.getJoinableProjects(hdbOfficer);
        
        if (joinableProjects.isEmpty()) {
            System.out.println("\nNo joinable BTO projects at the moment.");
            return;
        }
        
        System.out.println("\nJoinable BTO Projects:");
        for (BTOProject project : joinableProjects) {
            projectView.displayProjectInfo(project);
            System.out.println("----------------------------------------");
        }
    }

    /**
     * Allows the HDB officer to join a BTO project
     */
    private void joinBTOProjectAsOfficer() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        
        // Check if officer has projects with overlapping application periods
        List<BTOProject> handledProjects = hdbOfficer.getHandledProjects();
        
        List<BTOProject> joinableProjects = projectService.getJoinableProjects(hdbOfficer);
        
        if (joinableProjects.isEmpty()) {
            System.out.println("\nNo joinable BTO projects at the moment.");
            return;
        }
        
        System.out.println("\nJoinable BTO Projects:");
        for (BTOProject project : joinableProjects) {
            projectView.displayProjectInfo(project);
            System.out.println("----------------------------------------");
        }
        
        System.out.print("Enter the project name you want to join (Enter X to cancel): ");
        String projectName = sc.nextLine();
        
        if (projectName.equalsIgnoreCase("X")) {
            return;
        }
        
        BTOProject selectedProject = null;
        for (BTOProject project : joinableProjects) {
            if (project.getProjectName().equals(projectName)) {
                selectedProject = project;
                break;
            }
        }
        
        if (selectedProject == null) {
            System.out.println("Invalid project name. Please try again.");
            return;
        }

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
        
        // Create a new HDBOfficerRegistration with pending status
        HDBOfficerRegistration registration = new HDBOfficerRegistration(hdbOfficer, selectedProject);
        Map<String, HDBOfficerRegistration> registrations = DataStore.getHDBOfficerRegistrationsData();
        registrations.put(registration.getRegistrationId(), registration);
        DataStore.setHDBOfficerRegistrationsData(registrations);
        DataStore.saveData();
        
        System.out.println("Registration request submitted successfully. Please wait for HDB Manager's approval.");
    }

    /**
     * Checks if two projects have overlapping application periods
     */
    private boolean hasOverlappingPeriod(BTOProject project1, BTOProject project2) {
        return !project1.getApplicationClosingDate().isBefore(project2.getApplicationOpeningDate()) &&
               !project2.getApplicationClosingDate().isBefore(project1.getApplicationOpeningDate());
    }

    /**
     * Displays all BTO projects that the HDB officer has joined
     */
    private void viewJoinedBTOProjects() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        List<BTOProject> joinedProjects = projectService.getJoinedProjects(hdbOfficer);
        
        if (joinedProjects.isEmpty()) {
            System.out.println("\nYou have not joined any BTO projects.");
            return;
        }
        
        System.out.println("\nJoined BTO Projects:");
        for (BTOProject project : joinedProjects) {
            projectView.displayProjectInfo(project);
            System.out.println("----------------------------------------");
        }
    }

    @Override
    protected void viewAvailableBTOProjects() {
        User user = AuthStore.getCurrentUser();
        List<BTOProject> availableProjects = projectService.getAvailableProjects(user);
        
        if (availableProjects.isEmpty()) {
            System.out.println("\nNo available BTO projects at the moment.");
            return;
        }
        
        System.out.println("\nAvailable BTO Projects:");
        for (BTOProject project : availableProjects) {
            projectView.displayProjectInfo(project);
            System.out.println("----------------------------------------");
        }
    }

    @Override
    protected void applyForBTOProject() {
        HDBOfficer hdbOfficer = (HDBOfficer) AuthStore.getCurrentUser();
        
        // Check if applicant already has an application
        if (projectService.hasExistingApplication(hdbOfficer)) {
            System.out.println("\nYou already have an existing BTO application. Only 1 application is allowed at a time.");
            return;
        }
        
        List<BTOProject> availableProjects = projectService.getAvailableProjects(hdbOfficer);

        if (availableProjects.isEmpty()) {
            System.out.println("\nNo available BTO projects at the moment.");
            return;
        }

        System.out.print("Enter the project name you want to apply for (Enter X to cancel): ");
        String projectName = sc.nextLine();

        if (projectName.equalsIgnoreCase("X")) {
            return;
        }

        BTOProject selectedProject = null;
        for (BTOProject project : availableProjects) {
            if (project.getProjectName().equals(projectName)) {
                selectedProject = project;
                break;
            }
        }

        if (selectedProject == null) {
            System.out.println("Invalid project name. Please try again.");
            return;
        }

        // Check if the HDB officer is already assigned to the project
        if (!projectService.canOfficerApplyForProject(selectedProject, hdbOfficer)) {
            System.out.println("You cannot apply for a project that you are assigned to as an HDB officer.");
            return;
        }

        BTOApplication application = new BTOApplication(hdbOfficer, selectedProject);
        projectService.applyForBTOProject(application);

        System.out.println("Application submitted successfully. An HDB officer will contact you for flat booking if your application is successful.");
    }

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
     * View and reply to enquiries for projects the officer is assigned to
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
     * Process flat booking requests from applicants
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
}

