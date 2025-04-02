package services;

import models.entity.Project;
import models.entity.User;
import models.enumeration.FlatType;
import stores.DataStore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectService {
    private static ProjectService instance;

    private ProjectService() {}

    public static synchronized ProjectService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    /**
     * Create a new project
     * @param projectName Name of the project
     * @param neighborhood Location of the project
     * @param applicationOpeningDate Project application opening date
     * @param applicationClosingDate Project application closing date
     * @param flatTypes Available flat types
     * @param hdbManager HDB Manager for the project
     * @return Created project
     */
    public Project createProject(String projectName, String neighborhood, 
                                 LocalDate applicationOpeningDate, 
                                 LocalDate applicationClosingDate, 
                                 List<FlatType> flatTypes, 
                                 User hdbManager) {
        // Check if project with same name already exists
        if (getProjectByName(projectName).isPresent()) {
            throw new IllegalArgumentException("Project with this name already exists");
        }

        Project newProject = new Project(projectName, neighborhood, 
                                         applicationOpeningDate, 
                                         applicationClosingDate);
        newProject.setFlatTypes(flatTypes);
        newProject.setHdbManager(hdbManager);
        
        DataStore.getProjects().add(newProject);
        return newProject;
    }

    /**
     * Get project by name
     * @param projectName Project name
     * @return Optional of project
     */
    public Optional<Project> getProjectByName(String projectName) {
        return DataStore.getProjects().stream()
            .filter(project -> project.getProjectName().equalsIgnoreCase(projectName))
            .findFirst();
    }

    /**
     * Update project details
     * @param projectName Project name
     * @param neighborhood New neighborhood (can be null)
     * @param applicationOpeningDate New opening date (can be null)
     * @param applicationClosingDate New closing date (can be null)
     * @param flatTypes New flat types (can be null)
     * @return Updated project
     */
    public Project updateProject(String projectName, String neighborhood, 
                                 LocalDate applicationOpeningDate, 
                                 LocalDate applicationClosingDate, 
                                 List<FlatType> flatTypes) {
        Project project = getProjectByName(projectName)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (neighborhood != null && !neighborhood.isEmpty()) {
            project.setNeighborhood(neighborhood);
        }
        if (applicationOpeningDate != null) {
            project.setApplicationOpeningDate(applicationOpeningDate);
        }
        if (applicationClosingDate != null) {
            project.setApplicationClosingDate(applicationClosingDate);
        }
        if (flatTypes != null && !flatTypes.isEmpty()) {
            project.setFlatTypes(flatTypes);
        }

        return project;
    }

    /**
     * Delete a project
     * @param projectName Project name to delete
     * @return True if project deleted successfully
     */
    public boolean deleteProject(String projectName) {
        Project project = getProjectByName(projectName)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        return DataStore.getProjects().remove(project);
    }

    /**
     * Toggle project visibility
     * @param projectName Project name
     * @param visibility New visibility status
     * @return Updated project
     */
    public Project toggleProjectVisibility(String projectName, boolean visibility) {
        Project project = getProjectByName(projectName)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        project.setVisibility(visibility);
        return project;
    }

    /**
     * Get all visible projects
     * @return List of visible projects
     */
    public List<Project> getVisibleProjects() {
        return DataStore.getProjects().stream()
            .filter(Project::isVisibility)
            .collect(Collectors.toList());
    }

    /**
     * Get projects filtered by flat type
     * @param flatType Flat type to filter
     * @return List of projects with specified flat type
     */
    public List<Project> getProjectsByFlatType(FlatType flatType) {
        return DataStore.getProjects().stream()
            .filter(project -> project.getFlatTypes().contains(flatType))
            .collect(Collectors.toList());
    }

    /**
     * Get projects managed by a specific HDB manager
     * @param hdbManager HDB Manager
     * @return List of projects managed by the HDB manager
     */
    public List<Project> getProjectsByHDBManager(User hdbManager) {
        return DataStore.getProjects().stream()
            .filter(project -> project.getHdbManager().equals(hdbManager))
            .collect(Collectors.toList());
    }

    /**
     * Check if a project is currently in its application period
     * @param projectName Project name
     * @return True if project is in application period, false otherwise
     */
    public boolean isInApplicationPeriod(String projectName) {
        Project project = getProjectByName(projectName)
            .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        LocalDate now = LocalDate.now();
        return !now.isBefore(project.getApplicationOpeningDate()) && 
               !now.isAfter(project.getApplicationClosingDate());
    }
}