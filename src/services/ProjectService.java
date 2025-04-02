package services;

import models.BTOProject;
import models.HDBManager;
import models.User;
import enumeration.FlatType;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ProjectService {
    private static ProjectService instance;
    private final List<BTOProject> projects;

    private ProjectService() {
        this.projects = new CopyOnWriteArrayList<>();
    }

    public static synchronized ProjectService getInstance() {
        if (instance == null) {
            instance = new ProjectService();
        }
        return instance;
    }

    /**
     * Create a new BTO project
     */
    public BTOProject createProject(
        HDBManager manager, 
        String projectName, 
        String neighborhood, 
        LocalDate openingDate, 
        LocalDate closingDate
    ) {
        // Validate inputs
        validateProjectCreationInputs(
            manager, projectName, neighborhood, 
            openingDate, closingDate
        );

        // Check for duplicate project name
        if (isProjectNameExists(projectName)) {
            throw new IllegalArgumentException("Project with name '" + projectName + "' already exists");
        }

        // Create and add project
        BTOProject newProject = new BTOProject(
            projectName, 
            neighborhood, 
            openingDate, 
            closingDate
        );
        
        // Set default values
        newProject.setAvailableHDBOfficerSlots(10); // Default max officer slots
        newProject.setVisibility(true); // Default to visible
        
        projects.add(newProject);
        return newProject;
    }

    /**
     * Comprehensive input validation for project creation
     */
    private void validateProjectCreationInputs(
        HDBManager manager, 
        String projectName, 
        String neighborhood, 
        LocalDate openingDate, 
        LocalDate closingDate
    ) {
        // Validate manager
        if (manager == null) {
            throw new IllegalArgumentException("HDB Manager cannot be null");
        }

        // Validate project name
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }

        // Validate neighborhood
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be empty");
        }

        // Validate dates
        if (openingDate == null || closingDate == null) {
            throw new IllegalArgumentException("Opening and closing dates cannot be null");
        }

        if (openingDate.isAfter(closingDate)) {
            throw new IllegalArgumentException("Opening date must be before closing date");
        }

        if (openingDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Opening date cannot be in the past");
        }
    }

    /**
     * Check if project name already exists
     */
    private boolean isProjectNameExists(String projectName) {
        return projects.stream()
            .anyMatch(p -> p.getProjectName().equalsIgnoreCase(projectName));
    }

    /**
     * Add method to set flat types for a project
     */
    public void setProjectFlatTypes(BTOProject project, Map<FlatType, Integer> flatTypes) {
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null");
        }

        // Validate flat types
        if (flatTypes == null || flatTypes.isEmpty()) {
            throw new IllegalArgumentException("Flat types cannot be null or empty");
        }

        // Ensure non-negative unit counts
        flatTypes.forEach((type, count) -> {
            if (count < 0) {
                throw new IllegalArgumentException("Unit count cannot be negative for " + type);
            }
        });

        project.setFlatTypes(flatTypes);
    }
    /**
     * Get a project by its name
     */
    public BTOProject getProjectByName(String projectName) {
        return projects.stream()
                .filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
                .findFirst()
                .orElse(null);
    }
    /**
     * Get all projects
     */
    public List<BTOProject> getAllProjects() {
        return new ArrayList<>(projects);
    }

    
    /**
     * Get a project by its ID
     */
    public BTOProject getProjectById(String projectId) {
        return projects.stream()
                .filter(p -> p.getProjectId().equalsIgnoreCase(projectId))
                .findFirst()
                .orElse(null);
    }

    // Rest of the methods remain the same as in the previous implementation
    // ... (getProjectByName, getAllProjects, etc.)
}