package view;

import enumeration.FlatType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import models.BTOProject;
import models.FlatTypeDetails;
import models.ProjectFilter;

/**
 * View class for BTO project management operations
 */
public class BTOProjectManagementView {
    
    private static final Scanner sc = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Display project creation form and get project details
     * @return Array of project details or null if cancelled
     */
    public Object[] getProjectCreationDetails() {
        System.out.println("\n===== Create BTO Project =====");
        
        // Project name
        System.out.print("Enter project name (X to cancel): ");
        String projectName = sc.nextLine();
        if (projectName.equalsIgnoreCase("X")) {
            return null;
        }
        
        // Neighborhood
        System.out.print("Enter neighborhood: ");
        String neighborhood = sc.nextLine();
        
        // Application dates
        LocalDate openingDate = null;
        LocalDate closingDate = null;
        
        boolean validDates = false;
        while (!validDates) {
            try {
                System.out.print("Enter application opening date (dd/mm/yyyy): ");
                String openingDateStr = sc.nextLine();
                openingDate = LocalDate.parse(openingDateStr, DATE_FORMATTER);
                
                System.out.print("Enter application closing date (dd/mm/yyyy): ");
                String closingDateStr = sc.nextLine();
                closingDate = LocalDate.parse(closingDateStr, DATE_FORMATTER);
                
                if (closingDate.isBefore(openingDate)) {
                    System.out.println("Closing date must be after opening date!");
                } else {
                    validDates = true;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please use dd/mm/yyyy.");
            }
        }
        
        // Flat types
        Map<FlatType, FlatTypeDetails> flatTypes = getFlatTypes();
        
        // HDB Officer slots
        int hdbOfficerSlots = 0;
        boolean validSlots = false;
        while (!validSlots) {
            try {
                System.out.print("Enter number of HDB Officer slots: ");
                hdbOfficerSlots = Integer.parseInt(sc.nextLine());
                if (hdbOfficerSlots < 1) {
                    System.out.println("Number of slots must be at least 1!");
                } else {
                    validSlots = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
            }
        }
        
        return new Object[] {
            projectName,
            neighborhood,
            openingDate,
            closingDate,
            flatTypes,
            hdbOfficerSlots
        };
    }
    
    /**
     * Get flat types and details for a project
     * @return Map of flat types to their details
     */
    public Map<FlatType, FlatTypeDetails> getFlatTypes() {
        Map<FlatType, FlatTypeDetails> flatTypes = new HashMap<>();
        
        System.out.println("\nFlat Types:");
        
        for (FlatType flatType : FlatType.values()) {
            System.out.println(flatType.getDisplayName());
            
            boolean validUnits = false;
            int units = 0;
            while (!validUnits) {
                try {
                    System.out.print("Enter number of units (0 to skip): ");
                    units = Integer.parseInt(sc.nextLine());
                    if (units < 0) {
                        System.out.println("Number of units cannot be negative!");
                    } else {
                        validUnits = true;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input! Please enter a valid number.");
                }
            }
            
            if (units > 0) {
                boolean validPrice = false;
                double price = 0;
                while (!validPrice) {
                    try {
                        System.out.print("Enter price: ");
                        price = Double.parseDouble(sc.nextLine());
                        if (price <= 0) {
                            System.out.println("Price must be positive!");
                        } else {
                            validPrice = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input! Please enter a valid number.");
                    }
                }
                
                flatTypes.put(flatType, new FlatTypeDetails(units, price));
            }
        }
        
        return flatTypes;
    }
    
    /**
     * Display project details
     * @param project The project to display
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
        
        System.out.println("----------------------------------------");
    }
    
    /**
     * Display filtered projects
     * @param projects The list of projects to display
     * @param filter The filter applied to the projects
     */
    public void displayFilteredProjects(List<BTOProject> projects, ProjectFilter filter) {
        if (projects.isEmpty()) {
            System.out.println("\nNo projects match the current filters.");
            return;
        }
        
        // Display filter information
        displayCurrentFilters(filter);
        
        // Display projects
        System.out.println("\n===== BTO Projects =====");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getProjectName() + " (" + projects.get(i).getNeighborhood() + ")");
        }
        
        System.out.println("\nTotal projects: " + projects.size());
    }
    
    /**
     * Display the current filters
     * @param filter The filter to display
     */
    public void displayCurrentFilters(ProjectFilter filter) {
        System.out.println("\n===== Current Filters =====");
        
        // Sort order
        String sortOrder = "";
        switch (filter.getSortOrder()) {
            case NAME_ASC:
                sortOrder = "Project name (A-Z)";
                break;
            case NAME_DESC:
                sortOrder = "Project name (Z-A)";
                break;
            case NEIGHBORHOOD_ASC:
                sortOrder = "Neighborhood (A-Z)";
                break;
            case NEIGHBORHOOD_DESC:
                sortOrder = "Neighborhood (Z-A)";
                break;
            case DATE_ASC:
                sortOrder = "Application date (Earliest first)";
                break;
            case DATE_DESC:
                sortOrder = "Application date (Latest first)";
                break;
        }
        System.out.println("Sort order: " + sortOrder);
        
        // Name filter
        if (filter.getNameFilter() != null && !filter.getNameFilter().isEmpty()) {
            System.out.println("Project name contains: " + filter.getNameFilter());
        }
        
        // Neighborhood filter
        if (filter.getNeighborhoodFilter() != null && !filter.getNeighborhoodFilter().isEmpty()) {
            System.out.println("Neighborhood contains: " + filter.getNeighborhoodFilter());
        }
        
        // Flat type filter
        if (filter.getFlatTypeFilter() != null) {
            System.out.println("Flat type: " + filter.getFlatTypeFilter().getDisplayName());
        }
        
        // Visibility filter
        if (filter.isShowVisibleOnly()) {
            System.out.println("Showing visible projects only");
        } else {
            System.out.println("Showing all projects");
        }
    }
    
    /**
     * Show filter options menu
     * @param filter The current filter
     * @return Updated filter
     */
    public ProjectFilter showFilterOptions(ProjectFilter filter) {
        boolean done = false;
        
        while (!done) {
            System.out.println("\n===== Filter Options =====");
            System.out.println("1. Set sort order");
            System.out.println("2. Filter by project name");
            System.out.println("3. Filter by neighborhood");
            System.out.println("4. Filter by flat type");
            System.out.println("5. Reset all filters");
            System.out.println("0. Back to previous menu");
            
            System.out.print("\nEnter your choice: ");
            String input = sc.nextLine().trim();
            
            try {
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 0:
                        done = true;
                        break;
                    case 1:
                        setSortOrder(filter);
                        break;
                    case 2:
                        setNameFilter(filter);
                        break;
                    case 3:
                        setNeighborhoodFilter(filter);
                        break;
                    case 4:
                        setFlatTypeFilter(filter);
                        break;
                    case 5:
                        filter.resetFilters();
                        System.out.println("All filters have been reset to default.");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        
        return filter;
    }
    
    /**
     * Set sort order
     * @param filter The filter to update
     */
    private void setSortOrder(ProjectFilter filter) {
        System.out.println("\n===== Set Sort Order =====");
        System.out.println("1. Project name (A-Z)");
        System.out.println("2. Project name (Z-A)");
        System.out.println("3. Neighborhood (A-Z)");
        System.out.println("4. Neighborhood (Z-A)");
        System.out.println("5. Application date (Earliest first)");
        System.out.println("6. Application date (Latest first)");
        
        System.out.print("\nEnter your choice: ");
        String input = sc.nextLine().trim();
        
        try {
            int choice = Integer.parseInt(input);
            
            switch (choice) {
                case 1:
                    filter.setSortOrder(ProjectFilter.ProjectSortOrder.NAME_ASC);
                    break;
                case 2:
                    filter.setSortOrder(ProjectFilter.ProjectSortOrder.NAME_DESC);
                    break;
                case 3:
                    filter.setSortOrder(ProjectFilter.ProjectSortOrder.NEIGHBORHOOD_ASC);
                    break;
                case 4:
                    filter.setSortOrder(ProjectFilter.ProjectSortOrder.NEIGHBORHOOD_DESC);
                    break;
                case 5:
                    filter.setSortOrder(ProjectFilter.ProjectSortOrder.DATE_ASC);
                    break;
                case 6:
                    filter.setSortOrder(ProjectFilter.ProjectSortOrder.DATE_DESC);
                    break;
                default:
                    System.out.println("Invalid choice. Sort order not changed.");
                    return;
            }
            
            System.out.println("Sort order updated successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Sort order not changed.");
        }
    }
    
    /**
     * Set name filter
     * @param filter The filter to update
     */
    private void setNameFilter(ProjectFilter filter) {
        System.out.println("\n===== Filter by Project Name =====");
        System.out.println("Enter project name to filter by (leave empty to clear filter):");
        String name = sc.nextLine().trim();
        
        if (name.isEmpty()) {
            filter.setNameFilter(null);
            System.out.println("Project name filter cleared.");
        } else {
            filter.setNameFilter(name);
            System.out.println("Projects will be filtered to include '" + name + "' in their name.");
        }
    }
    
    /**
     * Set neighborhood filter
     * @param filter The filter to update
     */
    private void setNeighborhoodFilter(ProjectFilter filter) {
        System.out.println("\n===== Filter by Neighborhood =====");
        System.out.println("Enter neighborhood to filter by (leave empty to clear filter):");
        String neighborhood = sc.nextLine().trim();
        
        if (neighborhood.isEmpty()) {
            filter.setNeighborhoodFilter(null);
            System.out.println("Neighborhood filter cleared.");
        } else {
            filter.setNeighborhoodFilter(neighborhood);
            System.out.println("Projects will be filtered to include '" + neighborhood + "' in their neighborhood.");
        }
    }
    
    /**
     * Set flat type filter
     * @param filter The filter to update
     */
    private void setFlatTypeFilter(ProjectFilter filter) {
        System.out.println("\n===== Filter by Flat Type =====");
        System.out.println("1. 2-room");
        System.out.println("2. 3-room");
        System.out.println("0. Clear filter");
        
        System.out.print("\nEnter your choice: ");
        String input = sc.nextLine().trim();
        
        try {
            int choice = Integer.parseInt(input);
            
            switch (choice) {
                case 0:
                    filter.setFlatTypeFilter(null);
                    System.out.println("Flat type filter cleared.");
                    break;
                case 1:
                    filter.setFlatTypeFilter(FlatType.TWO_ROOM);
                    System.out.println("Projects will be filtered to only include 2-room flats.");
                    break;
                case 2:
                    filter.setFlatTypeFilter(FlatType.THREE_ROOM);
                    System.out.println("Projects will be filtered to only include 3-room flats.");
                    break;
                default:
                    System.out.println("Invalid choice. Flat type filter not changed.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Flat type filter not changed.");
        }
    }
    
    /**
     * Get edit choice
     * @return The user's choice
     */
    public int getEditChoice() {
        System.out.println("\nWhat would you like to edit?");
        System.out.println("1. Project Name");
        System.out.println("2. Neighborhood");
        System.out.println("3. Application Opening Date");
        System.out.println("4. Application Closing Date");
        System.out.println("5. Flat Types");
        System.out.println("6. HDB Officer Slots");
        System.out.println("7. Visibility");
        System.out.println("0. Cancel");
        
        System.out.print("\nEnter your choice: ");
        try {
            int choice = Integer.parseInt(sc.nextLine());
            if (choice < 0 || choice > 7) {
                System.out.println("Invalid choice.");
                return -1;
            }
            return choice;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return -1;
        }
    }
    
    /**
     * Get new project name
     * @return The new project name
     */
    public String getNewProjectName() {
        System.out.print("Enter new project name: ");
        return sc.nextLine();
    }
    
    /**
     * Get new neighborhood
     * @return The new neighborhood
     */
    public String getNewNeighborhood() {
        System.out.print("Enter new neighborhood: ");
        return sc.nextLine();
    }
    
    /**
     * Get new application dates
     * @return Array with [openingDate, closingDate] or null if invalid
     */
    public LocalDate[] getNewApplicationDates() {
        LocalDate openingDate = null;
        LocalDate closingDate = null;
        
        boolean validDates = false;
        while (!validDates) {
            try {
                System.out.print("Enter new application opening date (dd/mm/yyyy): ");
                String openingDateStr = sc.nextLine();
                openingDate = LocalDate.parse(openingDateStr, DATE_FORMATTER);
                
                System.out.print("Enter new application closing date (dd/mm/yyyy): ");
                String closingDateStr = sc.nextLine();
                closingDate = LocalDate.parse(closingDateStr, DATE_FORMATTER);
                
                if (closingDate.isBefore(openingDate)) {
                    System.out.println("Closing date must be after opening date!");
                } else {
                    validDates = true;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format! Please use dd/mm/yyyy.");
                return null;
            }
        }
        
        return new LocalDate[] {openingDate, closingDate};
    }
    
    /**
     * Get new HDB officer slots
     * @param currentOfficers Current number of officers
     * @return New number of slots or -1 if invalid
     */
    public int getNewHDBOfficerSlots(int currentOfficers) {
        int slots = -1;
        boolean validSlots = false;
        
        while (!validSlots) {
            try {
                System.out.print("Enter new number of HDB Officer slots: ");
                slots = Integer.parseInt(sc.nextLine());
                
                if (slots < currentOfficers) {
                    System.out.println("New slots must be at least the current number of officers (" + currentOfficers + ")!");
                } else {
                    validSlots = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.");
                return -1;
            }
        }
        
        return slots;
    }
    
    /**
     * Get new visibility setting
     * @return The new visibility
     */
    public boolean getNewVisibility() {
        boolean validInput = false;
        boolean visibility = false;
        
        while (!validInput) {
            System.out.print("Enter new visibility (true/false): ");
            String input = sc.nextLine().toLowerCase();
            
            if (input.equals("true")) {
                visibility = true;
                validInput = true;
            } else if (input.equals("false")) {
                visibility = false;
                validInput = true;
            } else {
                System.out.println("Invalid input! Please enter true or false.");
            }
        }
        
        return visibility;
    }
    
    /**
     * Get deletion confirmation
     * @return true if confirmed, false otherwise
     */
    public boolean getDeletionConfirmation() {
        System.out.print("Are you sure you want to delete this project? (yes/no): ");
        String input = sc.nextLine().toLowerCase();
        
        return input.equals("yes");
    }
}