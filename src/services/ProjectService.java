package services;

import models.BTOProject;
import models.HDBManager;
import models.User;
import enumeration.FlatType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectService {
    private static ProjectService instance;
    private List<BTOProject> projects;

    private ProjectService() {
        this.projects = new ArrayList<>();
    }

    public static synchronized ProjectService getInstance() {
        if (instance == null) {
            instance = new ProjectService();
        }
        return instance;
    }

    /**
     * Create a new BTO project
     * 
     * @param manager HDB Manager creating the project
     * @param projectName Name of the project
     * @param neighborhood Project neighborhood
     * @param unitCounts Map of flat types and their counts
     * @param openingDate Application opening date
     * @param closingDate Application closing date
     * @param officerSlots Number of available officer slots
     * @return Created project
     */
    public BTOProject createProject(HDBManager manager, String projectName, String neighborhood, 
                                    Map<String, Integer> unitCounts, 
                                    LocalDate openingDate, LocalDate closingDate, 
                                    int officerSlots) {
        // Validate project creation
        if (getProjectByName(projectName) != null) {
            throw new IllegalArgumentException("Project with this name already exists");
        }

        // Convert unit counts to FlatType map
        Map<FlatType, Integer> flatTypeCounts = new HashMap<>();
        flatTypeCounts.put(FlatType.TWO_ROOM, unitCounts.getOrDefault(FlatType.TWO_ROOM.name(), 0));
        flatTypeCounts.put(FlatType.THREE_ROOM, unitCounts.getOrDefault(FlatType.THREE_ROOM.name(), 0));

        BTOProject newProject = new BTOProject(projectName, neighborhood, flatTypeCounts, 
                                               openingDate, closingDate, manager, officerSlots);
        
        projects.add(newProject);
        return newProject;
    }

    /**
     * Get project by name
     * 
     * @param projectName Name of the project
     * @return Project or null if not found
     */
    public BTOProject getProjectByName(String projectName) {
        return projects.stream()
            .filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Get all projects
     * 
     * @return List of all projects
     */
    public List<BTOProject> getAllProjects() {
        return new ArrayList<>(projects);
    }

    /**
     * Get visible projects for a user
     * 
     * @param user Current user
     * @return List of visible projects
     */
    public List<BTOProject> getVisibleProjects(User user) {
        return projects.stream()
            .filter(BTOProject::isVisibility)
            .collect(Collectors.toList());
    }

    /**
     * Get projects by manager
     * 
     * @param manager HDB Manager
     * @return List of projects managed by the manager
     */
    public List<BTOProject> getProjectsByManager(HDBManager manager) {
        return projects.stream()
            .filter(p -> p.getManager().equals(manager))
            .collect(Collectors.toList());
    }

    /**
     * Save projects (placeholder for potential file/database persistence)
     */
    public void saveProjects() {
        // Implement persistence logic if needed
    }

    /**
     * Edit an existing project
     * 
     * @param project Project to edit
     * @param projectName New project name
     * @param neighborhood New neighborhood
     * @param unitCounts New unit counts
     * @param openingDate New opening date
     * @param closingDate New closing date
     * @param officerSlots New officer slots
     * @return true if edit successful
     */
    public boolean editProject(BTOProject project, String projectName, String neighborhood, 
                                Map<FlatType, Integer> unitCounts, 
                                LocalDate openingDate, LocalDate closingDate, 
                                int officerSlots) {
        // Validate manager
        if (project == null) {
            return false;
        }

        project.setProjectName(projectName);
        project.setNeighborhood(neighborhood);
        project.setFlatTypes(unitCounts);
        project.setApplicationOpeningDate(openingDate);
        project.setApplicationClosingDate(closingDate);
        project.setAvailableHDBOfficerSlots(officerSlots);

        return true;
    }

    /**
     * Delete a project
     * 
     * @param project Project to delete
     * @return true if deletion successful
     */
    public boolean deleteProject(BTOProject project) {
        return projects.remove(project);
    }

    /**
     * Toggle project visibility
     * 
     * @param project Project to toggle
     * @param visible New visibility status
     */
    public void toggleProjectVisibility(BTOProject project, boolean visible) {
        project.setVisibility(visible);
    }
}