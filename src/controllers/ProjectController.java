package controllers;

import models.BTOProject;
import models.HDBManager;
import models.HDBOfficer;
import models.Registration;
import models.User;
import enumeration.FlatType;
import enumeration.RegistrationStatus;
import services.ProjectService;
import services.UserService;
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
     * @return true if creation succeeds
     */
    
  
     */
    public boolean editProject(BTOProject project, String projectName, String neighborhood, 
            int twoRoomCount, int threeRoomCount, 
            LocalDate openingDate, LocalDate closingDate, int officerSlots) {
        
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        HDBManager manager = (HDBManager) currentUser;
        if (!project.getHdbManager().equals(manager)) {
            return false;
        }
        
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
        
        return true;
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
        if (!project.getHdbManager().equals(manager)) {
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
        if (!project.getHdbManager().equals(manager)) {
            return false;
        }
        
        project.setVisibility(visible);
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
            return new ArrayList<>();
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
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            return false;
        }
        
        BTOProject project = projectService.getProjectById(projectId);
        if (project == null) {
            return false;
        }
        
        // Check eligibility
        HDBOfficer officer = (HDBOfficer) currentUser;
        
        // Create registration
        Registration registration = new Registration();
        registration.setOfficer(officer);
        registration.setProject(project);
        registration.setRegistrationDate(LocalDate.now());
        
        // Add to officer's registrations
        officer.addRegistration(registration);
        
        // Add to project's registrations
        project.addRegistration(registration);
        
        return true;
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

    public List<Registration> getRegistrationsByStatus(RegistrationStatus status) {
        if (status == null) {
            return new ArrayList<>();
        }

        // Collect all registrations from all projects
        List<Registration> allRegistrations = new ArrayList<>();
        for (BTOProject project : projects) {
            allRegistrations.addAll(project.getRegistrations());
        }

        // Filter by status
        return allRegistrations.stream()
                .filter(reg -> reg.getStatus() == status)
                .collect(Collectors.toList());
    }
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
    
    Map<FlatType, Integer> unitCounts = new HashMap<>();
    unitCounts.put(FlatType.TWO_ROOM, twoRoomCount);
    unitCounts.put(FlatType.THREE_ROOM, threeRoomCount);
    
    try {
        BTOProject project = projectService.createProject(
                (HDBManager) currentUser, 
                projectName, 
                neighborhood, 
                openingDate, 
                closingDate
        );
        
        if (project != null) {
            project.setFlatTypes(unitCounts);
            project.setAvailableHDBOfficerSlots(officerSlots);
            
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

}