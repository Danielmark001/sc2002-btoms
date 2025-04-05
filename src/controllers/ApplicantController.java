package controllers;

import java.util.Scanner;
import java.util.List;
import models.Applicant;
import models.BTOProject;
import stores.AuthStore;
import view.BTOProjectAvailableView;

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
}



