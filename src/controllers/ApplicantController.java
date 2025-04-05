package controllers;

import enumeration.FlatType;
import java.util.Scanner;
import java.util.List;
import models.Applicant;
import models.BTOApplication;
import models.BTOProject;
import stores.AuthStore;
import view.BTOProjectAvailableView;
import services.CsvDataService;
import utils.EnumParser;

public class ApplicantController extends UserController {

    private static final Scanner sc = new Scanner(System.in);
    private BTOProjectApplicantService projectService;
    private BTOProjectAvailableView projectView;

    public ApplicantController() {
        this.projectService = new BTOProjectApplicantService();
        this.projectView = new BTOProjectAvailableView();
    }

    public void start() {
        int choice;

        do {
            System.out.println("1. Change Password");
            System.out.println("2. View Available BTO Projects");
            System.out.println("3. Apply for a BTO Project");

            System.out.println("0. Logout");

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
                case 0:
                    System.out.println("Logging out...");
                    AuthController.endSession();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }

    private void viewAvailableBTOProjects() {
        Applicant applicant = (Applicant) AuthStore.getCurrentUser();

        
        List<BTOProject> availableProjects = projectService.getAvailableProjects(applicant);
        
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

    private void applyForBTOProject() {
        Applicant applicant = (Applicant) AuthStore.getCurrentUser();
        
        // Check if applicant already has an application
        if (projectService.hasExistingApplication(applicant)) {
            System.out.println("\nYou already have an existing BTO application. Only one application is allowed at a time.");
            return;
        }
        
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

        BTOApplication application = new BTOApplication(applicant, selectedProject);
        projectService.applyForBTOProject(application);

        System.out.println("Application submitted successfully. An HDB officer will contact you for flat booking if your application is successful.");
    }
}



