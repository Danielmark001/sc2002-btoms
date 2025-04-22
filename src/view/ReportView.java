package view;

import java.util.List;
import java.util.Scanner;

import models.BTOApplication;
import models.BTOProject;
import enumeration.FlatType;
import enumeration.MaritalStatus;
/**
 * View class for Reports
 */
public class ReportView {
    private static final Scanner sc = new Scanner(System.in);
    
    /**
     * Display filter options and get user choice
     * @return The selected filter option
     */
    public int displayFilterOptions() {
        System.out.println("\nFilter options:");
        System.out.println("1. All applicants");
        System.out.println("2. By project");
        System.out.println("3. By flat type");
        System.out.println("4. By marital status");
        System.out.println("5. By age range");
        System.out.print("Enter your choice: ");
        
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
    
    /**
     * Get project selection from user
     * @param projects List of available projects
     * @return Selected project index
     */
    public int getProjectSelection(List<BTOProject> projects) {
        System.out.println("\nAvailable projects:");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getProjectName());
        }
        System.out.print("Enter project number: ");
        
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
    
    /**
     * Get flat type selection from user
     * @return Selected flat type
     */
    public FlatType getFlatTypeSelection() {
        System.out.println("\nSelect flat type:");
        System.out.println("1. 2-Room");
        System.out.println("2. 3-Room");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine());
            return choice == 1 ? FlatType.TWO_ROOM : FlatType.THREE_ROOM;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return null;
        }
    }
    
    /**
     * Get marital status selection from user
     * @return Selected marital status
     */
    public MaritalStatus getMaritalStatusSelection() {
        System.out.println("\nSelect marital status:");
        System.out.println("1. Married");
        System.out.println("2. Single");
        System.out.print("Enter your choice: ");
        
        try {
            int choice = Integer.parseInt(sc.nextLine());
            return choice == 1 ? MaritalStatus.MARRIED : MaritalStatus.SINGLE;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return null;
        }
    }
    
    /**
     * Get age range from user
     * @return Array containing [minAge, maxAge]
     */
    public int[] getAgeRange() {
        System.out.print("\nEnter minimum age: ");
        int minAge;
        try {
            minAge = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return null;
        }
        
        System.out.print("Enter maximum age: ");
        int maxAge;
        try {
            maxAge = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return null;
        }
        
        if (minAge > maxAge) {
            System.out.println("Minimum age cannot be greater than maximum age.");
            return null;
        }
        
        return new int[]{minAge, maxAge};
    }
    
    /**
     * Display the report
     * @param applications List of applications to display
     */
    public void displayReport(List<BTOApplication> applications) {
        if (applications.isEmpty()) {
            System.out.println("No applications match the selected filter.");
            return;
        }
        
        System.out.println("\n===== Applicant Report =====");
        for (BTOApplication application : applications) {
            System.out.println("\nApplicant: " + application.getApplicant().getName() + " (" + application.getApplicant().getNric() + ")");
            System.out.println("Age: " + application.getApplicant().getAge());
            System.out.println("Marital Status: " + application.getApplicant().getMaritalStatus().getDisplayName());
            System.out.println("Project: " + application.getProject().getProjectName());
            System.out.println("Flat Type: " + application.getFlatType().getDisplayName());
        }
    }
} 