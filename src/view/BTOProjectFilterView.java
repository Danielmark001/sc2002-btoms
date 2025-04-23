package view;

import enumeration.FlatType;
import java.util.List;
import java.util.Scanner;
import models.BTOProject;
import models.ProjectFilter;
import models.User;
import stores.AuthStore;
import stores.FilterStore;

/**
 * View class for managing project filters
 */
public class BTOProjectFilterView {
    private BTOProjectView projectView;
    private Scanner scanner;

    public BTOProjectFilterView() {
        this.projectView = new BTOProjectView();
        this.scanner = new Scanner(System.in);
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
            projectView.displayProjectListItem(i, projects.get(i));
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
        System.out.println("Sort order: " + getSortOrderString(filter.getSortOrder()));

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
     * Get string representation of sort order
     * @param sortOrder The sort order
     * @return String representation of sort order
     */
    private String getSortOrderString(ProjectFilter.ProjectSortOrder sortOrder) {
        switch (sortOrder) {
            case NAME_ASC:
                return "Project name (A-Z)";
            case NAME_DESC:
                return "Project name (Z-A)";
            case NEIGHBORHOOD_ASC:
                return "Neighborhood (A-Z)";
            case NEIGHBORHOOD_DESC:
                return "Neighborhood (Z-A)";
            case DATE_ASC:
                return "Application date (Earliest first)";
            case DATE_DESC:
                return "Application date (Latest first)";
            default:
                return "Unknown";
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
            String input = scanner.nextLine().trim();

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

            // Save filter settings for current user
            User currentUser = AuthStore.getCurrentUser();
            FilterStore.setProjectFilter(currentUser, filter);
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
        String input = scanner.nextLine().trim();

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

            System.out.println("Sort order updated to: " + getSortOrderString(filter.getSortOrder()));
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
        String name = scanner.nextLine().trim();

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
        String neighborhood = scanner.nextLine().trim();

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
        String input = scanner.nextLine().trim();

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

}