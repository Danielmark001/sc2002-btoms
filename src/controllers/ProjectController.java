package controllers;

import models.BTOProject;
import models.HDBManager;
import models.HDBOfficer;
import models.Registration;
import models.User;
import models.Enquiry;
import enumeration.FlatType;
import enumeration.RegistrationStatus;
import services.ProjectService;
import services.UserService;
import services.EnquiryService;
import view.ProjectView;
import util.InputValidator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Controller for handling project-related operations
 */
public class ProjectController {
    private ProjectView projectView;
    private ProjectService projectService;
    private UserService userService;
    private EnquiryService enquiryService;
    
    /**
     * Constructor for ProjectController
     * 
     * @param projectView View for project operations
     */
    public ProjectController(ProjectView projectView) {
        this.projectView = projectView;
        this.projectService = ProjectService.getInstance();
        this.userService = UserService.getInstance();
        this.enquiryService = EnquiryService.getInstance();
    }
    
    /**
     * Default constructor
     */
    public ProjectController() {
        this.projectService = ProjectService.getInstance();
        this.userService = UserService.getInstance();
        this.enquiryService = EnquiryService.getInstance();
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
     * @return true if creation succeeds
     */
    public boolean createProject(String projectName, String neighborhood, 
            int twoRoomCount, int threeRoomCount, 
            LocalDate openingDate, LocalDate closingDate, int officerSlots) {
        
        // Validate inputs
        try {
            InputValidator.validateNonEmpty(projectName, "Project name cannot be empty");
            InputValidator.validateLength(projectName, 3, 100, "Project name must be between 3 and 100 characters");
            
            InputValidator.validateNonEmpty(neighborhood, "Neighborhood cannot be empty");
            InputValidator.validateLength(neighborhood, 3, 50, "Neighborhood must be between 3 and 50 characters");
            
            InputValidator.validateRange(twoRoomCount, 0, 1000, "2-Room count must be between 0 and 1000");
            InputValidator.validateRange(threeRoomCount, 0, 1000, "3-Room count must be between 0 and 1000");
            
            if (twoRoomCount == 0 && threeRoomCount == 0) {
                throw new IllegalArgumentException("At least one flat type must have units");
            }
            
            InputValidator.validateFutureDate(openingDate, "Opening date must be in the future");
            InputValidator.validateDateRange(openingDate, closingDate, "Closing date must be after opening date");
            
            InputValidator.validateRange(officerSlots, 1, 10, "Officer slots must be between 1 and 10");
        } catch (IllegalArgumentException e) {
            if (projectView != null) {
                projectView.displayError(e.getMessage());
            }
            return false;
        }
        
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            if (projectView != null) {
                projectView.displayError("Only HDB Managers can create projects");
            }
            return false;
        }
        
        try {
            BTOProject project = projectService.createProject(
                    projectName, 
                    neighborhood, 
                    twoRoomCount, 
                    threeRoomCount, 
                    openingDate, 
                    closingDate, 
                    officerSlots);
            
            if (project != null) {
                if (projectView != null) {
                    projectView.displaySuccess("Project created successfully");
                }
                return true;
            }
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error creating project: " + e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * Edits an existing BTO project
     * 
     * @param project Project to edit
     * @param projectName New name
     * @param neighborhood New neighborhood
     * @param twoRoomCount New 2-room count
     * @param threeRoomCount New 3-room count
     * @param openingDate New opening date
     * @param closingDate New closing date
     * @param officerSlots New officer slots count
     * @return true if edit succeeds
     */
    public boolean editProject(BTOProject project, String projectName, String neighborhood, 
            int twoRoomCount, int threeRoomCount, 
            LocalDate openingDate, LocalDate closingDate, int officerSlots) {
        
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            if (projectView != null) {
                projectView.displayError("Only HDB Managers can edit projects");
            }
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        if (!project.getHdbManager().equals(manager)) {
            if (projectView != null) {
                projectView.displayError("You can only edit projects you manage");
            }
            return false;
        }
        
        try {
            Map<FlatType, Integer> unitCounts = new HashMap<>();
            unitCounts.put(FlatType.TWO_ROOM, twoRoomCount);
            unitCounts.put(FlatType.THREE_ROOM, threeRoomCount);
            
            // Update project properties
            project.setProjectName(projectName);
            project.setNeighborhood(neighborhood);
            project.setApplicationOpeningDate(openingDate);
            project.setApplicationClosingDate(closingDate);
            project.setFlatTypes(unitCounts);
            project.setAvailableHDBOfficerSlots(officerSlots);
            
            boolean updated = projectService.updateProject(project);
            
            if (updated && projectView != null) {
                projectView.displaySuccess("Project updated successfully");
            }
            
            return updated;
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error updating project: " + e.getMessage());
            }
            return false;
        }
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
        
        boolean deleted = projectService.deleteProject(project);
        
        if (deleted && projectView != null) {
            projectView.displaySuccess("Project deleted successfully");
        }
        
        return deleted;
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
            if (projectView != null) {
                projectView.displayError("Only HDB Managers can toggle project visibility");
            }
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        if (!project.getHdbManager().equals(manager)) {
            if (projectView != null) {
                projectView.displayError("You can only manage projects you own");
            }
            return false;
        }
        
        boolean success = projectService.toggleProjectVisibility(project.getProjectId(), visible);
        
        if (success && projectView != null) {
            projectView.displaySuccess("Project visibility " + (visible ? "enabled" : "disabled"));
        }
        
        return success;
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
        return projectService.getVisibleProjects();
    }
    
    /**
     * Gets projects eligible for the current user to apply for
     * 
     * @return List of eligible projects
     */
    public List<BTOProject> getEligibleProjects() {
        return projectService.getEligibleProjects();
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
     * Gets projects that an officer is handling
     * 
     * @param officerNric Officer's NRIC
     * @return List of projects the officer is handling
     */
    public List<BTOProject> getProjectsByOfficer(String officerNric) {
        User user = userService.getUserByNRIC(officerNric);
        if (!(user instanceof HDBOfficer)) {
            return new ArrayList<>();
        }
        
        HDBOfficer officer = (HDBOfficer) user;
        List<BTOProject> result = new ArrayList<>();
        if (officer.getHandlingProject() != null) {
            result.add(officer.getHandlingProject());
        }
        return result;
    }
    
    /**
     * Gets the project that the current officer is handling
     * 
     * @return Project if found, null otherwise
     */
    public BTOProject getHandlingProject() {
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            return null;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        return officer.getHandlingProject();
    }
    
    /**
     * Registers the current user as an HDB Officer for a project
     * 
     * @param projectId ID of the project
     * @return true if registration succeeds
     */
    public boolean registerAsOfficer(String projectId) {
        return projectService.registerAsOfficer(projectId);
    }
    
    /**
     * Gets officer registrations by the current user
     * 
     * @param officerNric Officer's NRIC
     * @return List of registrations
     */
    public List<Registration> getOfficerRegistrationsByUser(String officerNric) {
        User user = userService.getUserByNRIC(officerNric);
        if (user == null) {
            return new ArrayList<>();
        }

        return user.getRegistrations();
    }
    
    /**
     * Gets registrations by status
     * 
     * @param status Status to filter by
     * @return List of registrations with the specified status
     */
    public List<Registration> getRegistrationsByStatus(RegistrationStatus status) {
        return projectService.getRegistrationsByStatus(status);
    }
    
    /**
     * Gets enquiries for a specific project
     * 
     * @param project Project to get enquiries for
     * @return List of enquiries
     */
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        if (project == null) {
            return new ArrayList<>();
        }
        
        return enquiryService.getEnquiriesByProject(project);
    }
}