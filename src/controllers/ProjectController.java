package controllers;

import models.BTOProject;
import models.HDBManager;
import models.User;
import models.enumeration.FlatType;
import services.ProjectService;
import services.UserService;
import views.ProjectView;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling project-related operations
 */
public class ProjectController {
    private ProjectView projectView;
    private ProjectService projectService;
    private UserService userService;
    
    /**
     * Constructor for ProjectController
     * 
     * @param projectView View for project operations
     */
    public ProjectController(ProjectView projectView) {
        this.projectView = projectView;
        this.projectService = ProjectService.getInstance();
        this.userService = UserService.getInstance();
    }
    
    /**
     * Creates a new BTO project
     * 
     * @param projectName Name of the project
     * @param neighborhood Neighborhood of the project
     * @param twoRoomCount Number of 2-room flats
     * @param threeRoomCount Number of 3-room flats
     * @param openingDate Application opening date
     * @param closingDate Application closing date
     * @param officerSlots Number of available officer slots
     * @return true if creation succeeds
     */
    public boolean createProject(String projectName, String neighborhood, 
            int twoRoomCount, int threeRoomCount, 
            LocalDate openingDate, LocalDate closingDate, int officerSlots) {
        
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        Map<String, Integer> unitCounts = new HashMap<>();
        unitCounts.put(FlatType.TWO_ROOM.name(), twoRoomCount);
        unitCounts.put(FlatType.THREE_ROOM.name(), threeRoomCount);
        
        BTOProject project = projectService.createProject(
                (HDBManager) currentUser, 
                projectName, 
                neighborhood, 
                unitCounts, 
                openingDate, 
                closingDate, 
                officerSlots
        );
        
        return project != null;
    }
    
    /**
     * Edits an existing BTO project
     * 
     * @param project Project to edit
     * @param projectName New project name
     * @param neighborhood New neighborhood
     * @param twoRoomCount New number of 2-room flats
     * @param threeRoomCount New number of 3-room flats
     * @param openingDate New application opening date
     * @param closingDate New application closing date
     * @param officerSlots New number of available officer slots
     * @return true if edit succeeds
     */
    public boolean editProject(BTOProject project, String projectName, String neighborhood, 
            int twoRoomCount, int threeRoomCount, 
            LocalDate openingDate, LocalDate closingDate, int officerSlots) {
        
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        if (!project.getManager().equals(manager)) {
            return false;
        }
        
        Map<FlatType, Integer> unitCounts = new HashMap<>();
        unitCounts.put(FlatType.TWO_ROOM, twoRoomCount);
        unitCounts.put(FlatType.THREE_ROOM, threeRoomCount);
        
        return projectService.editProject(
                project, 
                projectName, 
                neighborhood, 
                unitCounts, 
                openingDate, 
                closingDate, 
                officerSlots
        );
    }
    
    /**
     * Deletes a BTO project
     * 
     * @param project Project to delete
     * @return true if deletion succeeds
     */
    public boolean deleteProject(BTOProject project) {
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        if (!project.getManager().equals(manager)) {
            return false;
        }
        
        return projectService.deleteProject(project);
    }
    
    /**
     * Toggles a project's visibility
     * 
     * @param project Project to toggle
     * @param visible New visibility status
     * @return true if toggle succeeds
     */
    public boolean toggleProjectVisibility(BTOProject project, boolean visible) {
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        if (!project.getManager().equals(manager)) {
            return false;
        }
        
        projectService.toggleProjectVisibility(project, visible);
        projectService.saveProjects();
        return true;
    }
    
    /**
     * Gets all BTO projects
     * 
     * @return List of all projects
     */
    public List<BTOProject> getAllProjects() {
        return projectService.getAllProjects();
    }
    
    /**
     * Gets projects visible to the current user
     * 
     * @return List of visible projects
     */
    public List<BTOProject> getVisibleProjects() {
        User currentUser = userService.getCurrentUser();
        return projectService.getVisibleProjects(currentUser);
    }
    
    /**
     * Gets projects created by the current manager
     * 
     * @return List of projects created by the current manager
     */
    public List<BTOProject> getProjectsByCurrentManager() {
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return List.of();
        }
        
        return projectService.getProjectsByManager((HDBManager) currentUser);
    }
    
    /**
     * Gets a project by name
     * 
     * @param projectName Name of the project
     * @return Project if found, null otherwise
     */
    public BTOProject getProjectByName(String projectName) {
        return projectService.getProjectByName(projectName);
    }

    /**
     * Gets a project by ID
     * 
     * @param projectId ID of the project
     * @return Project if found, null otherwise
     */
    public BTOProject getProjectById(String projectId) {
        return projectService.getProjectById(projectId);
    }
    /**
     * Gets a project by its index
     * 
     * @param index Index of the project
     * @return Project if found, null otherwise
     */
    public BTOProject getProjectByIndex(int index) {
        List<BTOProject> projects = projectService.getAllProjects();
        if (index < 0 || index >= projects.size()) {
            return null;
        }
        return projects.get(index);

    
    
}