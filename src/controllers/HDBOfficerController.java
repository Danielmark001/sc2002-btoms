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

public class HDBOfficerController extends ApplicantController {

    private static final Scanner sc = new Scanner(System.in);
    private BTOProjectService projectService;
    private BTOProjectAvailableView projectView;
    private BTOApplicationView applicationView;

    public HDBOfficerController() {
        this.projectService = new BTOProjectService();
        this.projectView = new BTOProjectAvailableView();
        this.applicationView = new BTOApplicationView();
    }

    @Override
    public void start() {
        int choice;

        do {
            System.out.println(TextDecorationUtils.boldText("Hi, " + AuthStore.getCurrentUser().getName() + "!"));
            
            System.out.println(TextDecorationUtils.underlineText("SETTINGS"));
            System.out.println("1. Change Password");

            System.out.println(TextDecorationUtils.underlineText("BTO PROJECTS"));
            System.out.println("2. View Available BTO Projects");
            System.out.println("3. Apply for a BTO Project");
            System.out.println("4. View My BTO Applications");

            System.out.println(TextDecorationUtils.underlineText("BTO OFFICER"));
            System.out.println("5. View Joinable BTO Projects");
            System.out.println("6. Join BTO Project as Officer");
            System.out.println("7. View Joined BTO Projects");

            System.out.println("\n0. Logout");

            choice = sc.nextInt();
            sc.nextLine(); // consume newline

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
        
        projectService.joinProjectAsOfficer(selectedProject, hdbOfficer);
        System.out.println("Successfully joined the project as an HDB officer.");
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
            System.out.println("\nYou already have an existing BTO application. Only one application is allowed at a time.");
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
}

