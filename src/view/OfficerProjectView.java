package view;

import controllers.ProjectController;
import models.BTOProject;

import java.util.List;
import java.util.Scanner;
import models.Enquiry;
import enumeration.FlatType;
import models.User;
import enumeration.UserStatus;
import enumeration.ApplicationStatus;


public class OfficerProjectView {
    private ProjectController projectController;
    private Scanner scanner;

    public OfficerProjectView(ProjectController projectController) {
        this.projectController = projectController;
        this.scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        BTOProject project = projectController.getHandlingProject();
        if (project != null) {
            System.out.println("Neighborhood: " + project.getNeighborhood());
            System.out.println("Available 2-Room Units: " + project.getAvailableUnits(FlatType.TWO_ROOM));
            System.out.println("Available 3-Room Units: " + project.getAvailableUnits(FlatType.THREE_ROOM));
        } else {
            System.out.println("No project is currently being handled.");
        }
    }
    
    private void displayEnquiries(List<Enquiry> enquiries) {
        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found for this project.");
        } else {
            for (Enquiry enquiry : enquiries) {
                System.out.println("ID: " + enquiry.getId());
                System.out.println("Submitter: " + enquiry.getSubmitter().getName());
                System.out.println("Content: " + enquiry.getEnquiryText()); 
                System.out.println("Status: " + enquiry.getStatus());
                System.out.println("-------------------------");
            }
        }
    }
}
.println("\n===== OFFICER PROJECT MENU =====");
        System.out.println("1. View Handling Project");
        System.out.println("2. View Enquiries");
        System.out.println("3. Process Booking");
        System.out.println("4. Back");
    }

    public void handleUserInput() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        switch (choice) {
            case 1 -> displayMenu();
            case 2 -> viewEnquiries();
            case 3 -> processBooking();
            case 4 -> System.out.println("Going back...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }


    private void viewEnquiries() {
        BTOProject project = projectController.getHandlingProject();
        if (project != null) {
            List<Enquiry> enquiries = projectController.getEnquiriesByProject(project);
            displayEnquiries(enquiries);
        } else {
            System.out.println("You are not currently handling any project.");
        }
    }

    private void processBooking() {
        // Navigate to ApplicationView for booking process
    }

   
    