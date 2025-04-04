package view;

import controllers.EnquiryController;
import models.Enquiry;
import models.BTOProject;


import java.util.List;
import java.util.Scanner;

public class EnquiryView {
    private EnquiryController enquiryController;
    private Scanner scanner;

    public EnquiryView(EnquiryController enquiryController) {
        this.enquiryController = enquiryController;
        this.scanner = new Scanner(System.in);
    }


    public void displayMenu() {
        System.out.println("\n===== ENQUIRY MENU =====");
        System.out.println("1. Create Enquiry");
        System.out.println("2. View My Enquiries");
        System.out.println("3. View Project Enquiries");
        System.out.println("4. View All Enquiries");
        System.out.println("5. Reply to Enquiry");
        System.out.println("6. Back");
    }

    public void handleUserInput() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> createEnquiry();
            case 2 -> viewMyEnquiries();
            case 3 -> viewProjectEnquiries();
            case 4 -> viewAllEnquiries();
            case 5 -> replyToEnquiry();
            case 6 -> System.out.println("Going back...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    private void createEnquiry() {
        System.out.println("Enter project name:");
        String projectName = scanner.nextLine();
        BTOProject project = getProjectByName(projectName);

        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        System.out.println("Enter enquiry content:");
        String content = scanner.nextLine();

        boolean success = enquiryController.createEnquiry(project, content);
        if (success) {
            System.out.println("Enquiry created successfully.");
        } else {
            System.out.println("Failed to create enquiry.");
        }
    }

    private void viewMyEnquiries() {
        List<Enquiry> enquiries = enquiryController.getCurrentUserEnquiries();
        displayEnquiries(enquiries);
    }

    private void viewProjectEnquiries() {
        System.out.println("Enter project name:");
        String projectName = scanner.nextLine();
        BTOProject project = getProjectByName(projectName);

        if (project == null) {
            System.out.println("Project not found.");
            return;
        }

        List<Enquiry> enquiries = enquiryController.getEnquiriesByProject(project);
        displayEnquiries(enquiries);
    }

    private void viewAllEnquiries() {
        List<Enquiry> enquiries = enquiryController.getAllEnquiries();
        displayEnquiries(enquiries);
    }

    private void replyToEnquiry() {
        System.out.println("Enter enquiry ID:");
        String enquiryId = scanner.nextLine();
        Enquiry enquiry = getEnquiryById(enquiryId);

        if (enquiry == null) {
            System.out.println("Enquiry not found.");
            return;
        }

        System.out.println("Enter reply content:");
        String content = scanner.nextLine();

        boolean success = enquiryController.replyToEnquiry(enquiry, content);
        if (success) {
            System.out.println("Reply submitted successfully.");
        } else {
            System.out.println("Failed to submit reply.");
        }
    }

    private void displayEnquiries(List<Enquiry> enquiries) {
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
        } else {
            for (Enquiry enquiry : enquiries) {
                System.out.println("ID: " + enquiry.getId());
                System.out.println("Project: " + enquiry.getProject().getProjectName());
                System.out.println("Submitter: " + enquiry.getSubmitter().getName());
                System.out.println("Content: " + enquiry.getEnquiryText());
                System.out.println("Status: " + enquiry.getStatus());
                System.out.println("-------------------------");
            }
        }
    }

    private BTOProject getProjectByName(String projectName) {
        // Assuming there's a method in the controller to get a project by name
        return null;
    }

    private Enquiry getEnquiryById(String enquiryId) {
        // Assuming there's a method in the controller to get an enquiry by ID
        return null;
    }

}