package controllers;

import java.time.LocalDate;
import java.util.List;

import models.BTOProject;
import models.HDBManager;
import models.User;
import services.ProjectService;
import services.UserService;
import view.ProjectView;
import models.HDBOfficer;
import models.Enquiry;
import models.Registration;
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
     * Default constructor
     */
    public ProjectController() {
        this.projectService = ProjectService.getInstance();
        this.userService = UserService.getInstance();
    }

    /**
     * Starts the controller's main operation
     */
    public void start() {
        // Implementation depends on the view
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
     * @return Created project, or null if creation fails
     */
    public BTOProject createProject(String projectName, String neighborhood,
            int twoRoomCount, int threeRoomCount,
            LocalDate openingDate, LocalDate closingDate,
            int officerSlots) {

        // Validate manager
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            if (projectView != null) {
                projectView.displayError("Only HDB Managers can create projects");
            }
            return null;
        }

        try {
            // Create the project using ProjectService
            BTOProject project = projectService.createProject(
                    projectName, neighborhood, twoRoomCount, threeRoomCount,
                    openingDate, closingDate, officerSlots);

            // Set visibility to false by default
            if (project != null) {
                projectService.toggleProjectVisibility(project.getProjectId(), false);
            }

            return project;
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error creating project: " + e.getMessage());
            }
            return null;
        }
    }

    /**
     * Updates an existing project
     * 
     * @param project Project to update
     * @return true if update succeeds
     */
    public boolean updateProject(BTOProject project) {
        if (project == null) {
            if (projectView != null) {
                projectView.displayError("Project cannot be null");
            }
            return false;
        }

        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            if (projectView != null) {
                projectView.displayError("Only HDB Managers can update projects");
            }
            return false;
        }

        HDBManager manager = (HDBManager) currentUser;
        if (!project.getHdbManager().equals(manager)) {
            if (projectView != null) {
                projectView.displayError("You can only update projects you manage");
            }
            return false;
        }

        try {
            boolean success = projectService.updateProject(project);

            if (success && projectView != null) {
                projectView.displaySuccess("Project updated successfully");
            }

            return success;
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error updating project: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Deletes a project
     * 
     * @param project Project to delete
     * @return true if deletion succeeds
     */
    public boolean deleteProject(BTOProject project) {
        if (project == null) {
            if (projectView != null) {
                projectView.displayError("Project cannot be null");
            }
            return false;
        }

        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            if (projectView != null) {
                projectView.displayError("Only HDB Managers can delete projects");
            }
            return false;
        }

        HDBManager manager = (HDBManager) currentUser;
        if (!project.getHdbManager().equals(manager)) {
            if (projectView != null) {
                projectView.displayError("You can only delete projects you manage");
            }
            return false;
        }

        try {
            boolean success = projectService.deleteProject(project.getProjectId());

            if (success && projectView != null) {
                projectView.displaySuccess("Project deleted successfully");
            }

            return success;
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error deleting project: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Toggles a project's visibility
     * 
     * @param projectId ID of the project
     * @param visible Visibility to set
     * @return true if toggle succeeds
     */
    public boolean toggleProjectVisibility(String projectId, boolean visible) {
        if (projectId == null) {
            if (projectView != null) {
                projectView.displayError("Project ID cannot be null");
            }
            return false;
        }

        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            if (projectView != null) {
                projectView.displayError("Only HDB Managers can toggle project visibility");
            }
            return false;
        }

        BTOProject project = projectService.getProjectById(projectId);
        if (project == null) {
            if (projectView != null) {
                projectView.displayError("Project not found");
            }
            return false;
        }

        HDBManager manager = (HDBManager) currentUser;
        if (!project.getHdbManager().equals(manager)) {
            if (projectView != null) {
                projectView.displayError("You can only manage visibility for projects you own");
            }
            return false;
        }

        try {
            boolean success = projectService.toggleProjectVisibility(projectId, visible);

            if (success && projectView != null) {
                projectView.displaySuccess("Project visibility set to " + (visible ? "visible" : "hidden"));
            }

            return success;
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error toggling project visibility: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Gets all projects
     * 
     * @return List of all projects
     */
    public List<BTOProject> getAllProjects() {
        return projectService.getAllProjects();
    }

    /**
     * Gets projects created by the current manager
     * 
     * @return List of projects created by the current manager
     */
    public List<BTOProject> getProjectsByCurrentManager() {
        return projectService.getProjectsByCurrentManager();
    }

    /**
     * Gets a project by ID
     * 
     * @param projectId ID of the project to get
     * @return Project if found, null otherwise
     */
    public BTOProject getProjectById(String projectId) {
        return projectService.getProjectById(projectId);
    }

    /**
     * Gets visible projects
     * 
     * @return List of visible projects
     */
    public List<BTOProject> getVisibleProjects() {
        return projectService.getVisibleProjects();
    }

    /**
     * Gets projects eligible for the current user
     * 
     * @return List of eligible projects
     */
    public List<BTOProject> getEligibleProjects() {
        return projectService.getEligibleProjects();
    }

    public List<BTOProject> getProjectsByOfficer(String message) {

        // TODO Auto-generated method stub
        return null;
    }

    
    public BTOProject getHandlingProject() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Registration> getOfficerRegistrationsByUser(String nric) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean registerAsOfficer(String id) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean registerAsManager(String id) {
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean registerAsApplicant(String id) {
        // TODO Auto-generated method stub
        return false;
    }
}