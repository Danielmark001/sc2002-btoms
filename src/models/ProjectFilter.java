package models;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import enumeration.FlatType;

/**
 * Provides filtering and sorting functionality for BTO projects.
 * 
 * This class enables users to filter BTO projects based on various criteria such as
 * project name, neighborhood, flat type, and visibility. It also allows sorting
 * projects in different orders. The filter settings can be customized and applied
 * to any list of BTOProject objects to get a filtered and sorted result set.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class ProjectFilter {
    // Sort order 
    private ProjectSortOrder sortOrder = ProjectSortOrder.NAME_ASC; // Default is alphabetical order
    
    // Filter criteria
    private String nameFilter = null;
    private String neighborhoodFilter = null;
    private FlatType flatTypeFilter = null;
    private boolean showVisibleOnly = true;
    
    /**
     * Enumeration of possible sort orders for BTO projects.
     */
    public enum ProjectSortOrder {
        /** Sort by project name in ascending order (A-Z) */
        NAME_ASC,
        /** Sort by project name in descending order (Z-A) */
        NAME_DESC,
        /** Sort by neighborhood name in ascending order (A-Z) */
        NEIGHBORHOOD_ASC,
        /** Sort by neighborhood name in descending order (Z-A) */
        NEIGHBORHOOD_DESC,
        /** Sort by application opening date in ascending order (earliest first) */
        DATE_ASC,
        /** Sort by application opening date in descending order (latest first) */
        DATE_DESC
    }
    
    /**
     * Constructs a new ProjectFilter with default settings.
     * Default settings: sort by name ascending, show visible projects only,
     * and no specific filters for name, neighborhood, or flat type.
     */
    public ProjectFilter() {
        // Default settings
    }
    
    /**
     * Applies the current filter settings to a list of BTO projects.
     * 
     * @param projects The list of BTO projects to filter and sort
     * @return A new list containing only the projects that match the filter criteria,
     *         sorted according to the current sort order
     */
    public List<BTOProject> applyFilter(List<BTOProject> projects) {
        // Start with all projects
        List<BTOProject> filtered = projects;
        
        // Apply name filter if set
        if (nameFilter != null && !nameFilter.isEmpty()) {
            final String filterLower = nameFilter.toLowerCase();
            filtered = filtered.stream()
                .filter(p -> p.getProjectName().toLowerCase().contains(filterLower))
                .collect(Collectors.toList());
        }
        
        // Apply neighborhood filter if set
        if (neighborhoodFilter != null && !neighborhoodFilter.isEmpty()) {
            final String filterLower = neighborhoodFilter.toLowerCase();
            filtered = filtered.stream()
                .filter(p -> p.getNeighborhood().toLowerCase().contains(filterLower))
                .collect(Collectors.toList());
        }
        
        // Apply flat type filter if set
        if (flatTypeFilter != null) {
            filtered = filtered.stream()
                .filter(p -> p.getFlatTypes().containsKey(flatTypeFilter))
                .collect(Collectors.toList());
        }
        
        // Apply visibility filter if needed
        if (showVisibleOnly) {
            filtered = filtered.stream()
                .filter(BTOProject::isVisible)
                .collect(Collectors.toList());
        }
        
        // Apply sorting
        Comparator<BTOProject> comparator = getComparator();
        return filtered.stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets the appropriate comparator based on the current sort order.
     * 
     * @return A Comparator for sorting BTO projects according to the current sort order
     */
    private Comparator<BTOProject> getComparator() {
        switch (sortOrder) {
            case NAME_DESC:
                return Comparator.comparing(BTOProject::getProjectName).reversed();
            case NEIGHBORHOOD_ASC:
                return Comparator.comparing(BTOProject::getNeighborhood);
            case NEIGHBORHOOD_DESC:
                return Comparator.comparing(BTOProject::getNeighborhood).reversed();
            case DATE_ASC:
                return Comparator.comparing(BTOProject::getApplicationOpeningDate);
            case DATE_DESC:
                return Comparator.comparing(BTOProject::getApplicationOpeningDate).reversed();
            case NAME_ASC:
            default:
                return Comparator.comparing(BTOProject::getProjectName);
        }
    }
    
    /**
     * Gets the current sort order.
     * 
     * @return The current sort order setting
     */
    public ProjectSortOrder getSortOrder() {
        return sortOrder;
    }
    
    /**
     * Sets the sort order for projects.
     * 
     * @param sortOrder The new sort order to use
     */
    public void setSortOrder(ProjectSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    /**
     * Gets the current name filter.
     * 
     * @return The project name filter string, or null if not set
     */
    public String getNameFilter() {
        return nameFilter;
    }
    
    /**
     * Sets the project name filter.
     * 
     * @param nameFilter The project name substring to filter by, or null to clear this filter
     */
    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }
    
    /**
     * Gets the current neighborhood filter.
     * 
     * @return The neighborhood filter string, or null if not set
     */
    public String getNeighborhoodFilter() {
        return neighborhoodFilter;
    }
    
    /**
     * Sets the neighborhood filter.
     * 
     * @param neighborhoodFilter The neighborhood substring to filter by, or null to clear this filter
     */
    public void setNeighborhoodFilter(String neighborhoodFilter) {
        this.neighborhoodFilter = neighborhoodFilter;
    }
    
    /**
     * Gets the current flat type filter.
     * 
     * @return The flat type filter, or null if not set
     */
    public FlatType getFlatTypeFilter() {
        return flatTypeFilter;
    }
    
    /**
     * Sets the flat type filter.
     * 
     * @param flatTypeFilter The flat type to filter by, or null to clear this filter
     */
    public void setFlatTypeFilter(FlatType flatTypeFilter) {
        this.flatTypeFilter = flatTypeFilter;
    }
    
    /**
     * Checks if the filter is set to show only visible projects.
     * 
     * @return true if only visible projects will be shown, false otherwise
     */
    public boolean isShowVisibleOnly() {
        return showVisibleOnly;
    }
    
    /**
     * Sets whether to show only visible projects.
     * 
     * @param showVisibleOnly true to show only visible projects, false to show all projects
     */
    public void setShowVisibleOnly(boolean showVisibleOnly) {
        this.showVisibleOnly = showVisibleOnly;
    }
    
    /**
     * Resets all filters to their default settings.
     * Default settings: sort by name ascending, show visible projects only,
     * and no specific filters for name, neighborhood, or flat type.
     */
    public void resetFilters() {
        this.sortOrder = ProjectSortOrder.NAME_ASC;
        this.nameFilter = null;
        this.neighborhoodFilter = null;
        this.flatTypeFilter = null;
        this.showVisibleOnly = true;
    }
}