package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import enumeration.FlatType;
import models.BTOProject;
import models.FlatTypeDetails;

public class BTOProjectManagementView {
    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Get project creation details from user
     * @return Array containing [projectName, neighborhood, openingDate, closingDate, flatTypes, hdbOfficerSlots]
     */
    public Object[] getProjectCreationDetails() {
        System.out.println("\n===== Create BTO Project =====");
        
        System.out.print("Enter project name: ");
        String projectName = sc.nextLine();
        
        System.out.print("Enter neighborhood (e.g. Yishun, Boon Lay): ");
        String neighborhood = sc.nextLine();
        
        LocalDate openingDate = getDate("Enter application opening date (yyyy-MM-dd): ");
        if (openingDate == null) return null;
        
        LocalDate closingDate = getDate("Enter application closing date (yyyy-MM-dd): ");
        if (closingDate == null) return null;
        
        if (closingDate.isBefore(openingDate)) {
            System.out.println("Closing date must be after opening date.");
            return null;
        }
        
        Map<FlatType, FlatTypeDetails> flatTypes = getFlatTypes();
        if (flatTypes == null) return null;
        
        int hdbOfficerSlots = getHDBOfficerSlots();
        if (hdbOfficerSlots == -1) return null;
        
        return new Object[]{projectName, neighborhood, openingDate, closingDate, flatTypes, hdbOfficerSlots};
    }
    
    /**
     * Get date input from user
     * @param prompt Prompt to display
     * @return Parsed date or null if invalid
     */
    private LocalDate getDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return LocalDate.parse(sc.nextLine(), formatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }
    
    /**
     * Get flat types and their details from user input
     * @return Map of flat types to their details, or null if cancelled
     */
    public Map<FlatType, FlatTypeDetails> getFlatTypes() {
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
            return null;
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
            return null;
        }
        
        if (flatTypes.isEmpty()) {
            System.out.println("At least one flat type must be specified.");
            return null;
        }
        
        return flatTypes;
    }
    
    /**
     * Get HDB Officer slots from user
     * @return Number of slots or -1 if invalid
     */
    private int getHDBOfficerSlots() {
        System.out.print("Enter number of HDB Officer slots (max 10): ");
        try {
            int slots = Integer.parseInt(sc.nextLine());
            if (slots < 0 || slots > 10) {
                System.out.println("HDB Officer slots must be between 0 and 10.");
                return -1;
            }
            return slots;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
    
    /**
     * Display project details
     * @param project Project to display
     */
    public void displayProjectDetails(BTOProject project) {
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
    
    /**
     * Get edit choice from user
     * @return Edit choice (1-7) or 0 to cancel
     */
    public int getEditChoice() {
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
        
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
    
    /**
     * Get new project name from user
     * @return New project name
     */
    public String getNewProjectName() {
        System.out.print("Enter new project name: ");
        return sc.nextLine();
    }
    
    /**
     * Get new neighborhood from user
     * @return New neighborhood
     */
    public String getNewNeighborhood() {
        System.out.print("Enter new neighborhood: ");
        return sc.nextLine();
    }
    
    /**
     * Get new application dates from user
     * @return Array containing [openingDate, closingDate] or null if invalid
     */
    public LocalDate[] getNewApplicationDates() {
        LocalDate openingDate = getDate("Enter new application opening date (yyyy-MM-dd): ");
        if (openingDate == null) return null;
        
        LocalDate closingDate = getDate("Enter new application closing date (yyyy-MM-dd): ");
        if (closingDate == null) return null;
        
        if (closingDate.isBefore(openingDate)) {
            System.out.println("Closing date must be after opening date.");
            return null;
        }
        
        return new LocalDate[]{openingDate, closingDate};
    }
    
    /**
     * Get new HDB Officer slots from user
     * @param currentOfficers Number of current officers
     * @return New number of slots or -1 if invalid
     */
    public int getNewHDBOfficerSlots(int currentOfficers) {
        System.out.print("Enter new number of HDB Officer slots (max 10): ");
        try {
            int newSlots = Integer.parseInt(sc.nextLine());
            if (newSlots < currentOfficers) {
                System.out.println("HDB Officer slots must be at least the number of current officers.");
                return -1;
            }
            if (newSlots > 10) {
                System.out.println("HDB Officer slots cannot exceed 10.");
                return -1;
            }
            return newSlots;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
    
    /**
     * Get new visibility from user
     * @return New visibility status
     */
    public boolean getNewVisibility() {
        System.out.print("Set project visibility (true/false): ");
        try {
            return Boolean.parseBoolean(sc.nextLine());
        } catch (Exception e) {
            System.out.println("Invalid input. Please enter true or false.");
            return false;
        }
    }
    
    /**
     * Get confirmation for deletion
     * @return true if confirmed, false otherwise
     */
    public boolean getDeletionConfirmation() {
        System.out.print("Are you sure you want to delete this project? (yes/no): ");
        return sc.nextLine().toLowerCase().equals("yes");
    }
} 