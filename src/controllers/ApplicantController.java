package controllers;
import java.util.Scanner;
import java.util.List;
import models.Applicant;
import models.BTOApplication;
import models.BTOProject;
import models.User;
import stores.AuthStore;
import view.BTOProjectAvailableView;
import view.BTOApplicationView;
import services.BTOProjectService;
import utils.TextDecorationUtils;
import java.time.LocalDate;

public class ApplicantController extends UserController {

    private static final Scanner sc = new Scanner(System.in);
    private BTOProjectService projectService;
    private BTOProjectAvailableView projectView;
    private BTOApplicationView applicationView;

    public ApplicantController() {
        this.projectService = new BTOProjectService();
        this.projectView = new BTOProjectAvailableView();
        this.applicationView = new BTOApplicationView();
    }

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
                case 0:
                    System.out.println("Logging out...");
                    AuthController.endSession();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }

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

    protected void applyForBTOProject() {
        Applicant applicant = (Applicant) AuthStore.getCurrentUser();
        
        List<BTOProject> availableProjects = projectService.getAvailableProjects(applicant);

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

        // Check eligibility one final time before applying
        if (!projectService.isEligible(applicant, selectedProject)) {
            System.out.println("\nYou are not eligible to apply for this project.");
            if (!selectedProject.isVisible()) {
                System.out.println("This project is not visible.");
            }
            if (LocalDate.now().isBefore(selectedProject.getApplicationOpeningDate()) || 
                LocalDate.now().isAfter(selectedProject.getApplicationClosingDate())) {
                System.out.println("This project is not within its application period.");
                System.out.println("Application period: " + selectedProject.getApplicationOpeningDate() + 
                                 " to " + selectedProject.getApplicationClosingDate());
            }
            if (projectService.hasExistingApplication(applicant)) {
                System.out.println("You already have an existing BTO application.");
            }
            return;
        }

        BTOApplication application = new BTOApplication(applicant, selectedProject);
        projectService.applyForBTOProject(application);

        System.out.println("Application submitted successfully. An HDB officer will contact you for flat booking if your application is successful.");
    }

    protected void viewMyBTOApplications() {
        Applicant applicant = (Applicant) AuthStore.getCurrentUser();
        List<BTOApplication> applications = projectService.getApplicationsByApplicant(applicant);

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



