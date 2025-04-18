package models;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import enumeration.FlatType;

/**
 * Class for filtering and sorting BTO projects
 */
public class ProjectFilter {
    // Sort order 
    private ProjectSortOrder sortOrder = ProjectSortOrder.NAME_ASC; // Default is alphabetical order
    
    // Filter criteria
    private String nameFilter = null;
    private String neighborhoodFilter = null;
    private FlatType flatTypeFilter = null;
    private boolean showVisibleOnly = true;
    
    // Enum for sort orders
    public enum ProjectSortOrder {
        NAME_ASC,           // Project name A-Z
        NAME_DESC,          // Project name Z-A
        NEIGHBORHOOD_ASC,   // Neighborhood A-Z
        NEIGHBORHOOD_DESC,  // Neighborhood Z-A
        DATE_ASC,           // Application date (earliest first)
        DATE_DESC           // Application date (latest first)
    }
    
    // Default constructor
    public ProjectFilter() {
        // Default settings
    }
    
    // Apply filters to a list of projects
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
    
    // Get the appropriate comparator based on sort order
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
    
    // Getters and setters
    public ProjectSortOrder getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(ProjectSortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public String getNameFilter() {
        return nameFilter;
    }
    
    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }
    
    public String getNeighborhoodFilter() {
        return neighborhoodFilter;
    }
    
    public void setNeighborhoodFilter(String neighborhoodFilter) {
        this.neighborhoodFilter = neighborhoodFilter;
    }
    
    public FlatType getFlatTypeFilter() {
        return flatTypeFilter;
    }
    
    public void setFlatTypeFilter(FlatType flatTypeFilter) {
        this.flatTypeFilter = flatTypeFilter;
    }
    
    public boolean isShowVisibleOnly() {
        return showVisibleOnly;
    }
    
    public void setShowVisibleOnly(boolean showVisibleOnly) {
        this.showVisibleOnly = showVisibleOnly;
    }
    
    // Reset all filters to default
    public void resetFilters() {
        this.sortOrder = ProjectSortOrder.NAME_ASC;
        this.nameFilter = null;
        this.neighborhoodFilter = null;
        this.flatTypeFilter = null;
        this.showVisibleOnly = true;
    }
}