package models.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import interfaces.IProjectService;
import models.entity.Project;
import models.entity.Registration;
import models.entity.User;
import models.enumeration.FlatType;
import models.enumeration.MaritalStatus;
import models.enumeration.RegistrationStatus;
import services.ProjectService;
import stores.AuthStore;
import stores.DataStore;

/**
 * Manager class for handling project-related operations
 * Acts as a controller between the UI and the data layer
 */
public class ProjectManager {
    private final AuthStore authStore;
    private final DataStore dataStore;
    private final IProjectService projectService;
    
    /**
     * Constructor
     */
    public ProjectManager() {
        this.authStore = AuthStore.getInstance();
        this.dataStore = DataStore.getInstance();
        this.projectService = ProjectService.getInstance();
    }
    
    /**
     * Creates a new BTO project
     * @param projectName Name of the project
     * @param neighborhood Neighborhood of the project
     * @param twoRoomUnits Number of 2-Room units
     * @param threeRoomUnits Number of 3-Room units
     * @param openingDate Application opening date
     * @param closingDate Application closing date
     * @param officerSlots Number of HDB Officer slots
     * @return Created project or null if creation failed
     */
    public Project createProject(String projectName, String neighborhood, 
                              int twoRoomUnits, int threeRoomUnits,
                              LocalDate openingDate, LocalDate closingDate, 
                              int officerSlots) {
        // Check if user is an HDB Manager
        User currentUser = authStore.getCurrentUser();
        if (currentUser == null || !authStore.isHdbManager()) {
            return null;
        }
        
        // Check if manager is already handling another project during this period
        List<Project> managerProjects = dataStore.getProjectsByManager(currentUser.getNric());
        for (Project project : managerProjects) {
            // Check for date overlap
            if (datesOverlap(project.getOpeningDate(), project.getClosingDate(),
                            openingDate, closingDate)) {
                return null;
            }
        }
        
        // Create the project
        String projectId = UUID.randomUUID().toString();
        Project project = new Project(projectId, projectName, neighborhood, 
                                    currentUser.getNric(), openingDate, closingDate, officerSlots);
        
        // Set units
        project.setTotalUnits(FlatType.TWO_ROOM, twoRoomUnits);
        project.setTotalUnits(FlatType.THREE_ROOM, threeRoomUnits);
        project.setAvailableUnits(FlatType.TWO_ROOM, twoRoomUnits);
        project.setAvailableUnits(FlatType.THREE_ROOM, threeRoomUnits);
        
        // Save the project
        if (dataStore.addProject(project)) {
            return project;
        }
        
        return null;
    }
    
    /**
     * Updates an existing project
     * @param project Project with updated details
     * @return true if update successful, false otherwise
     */
    public boolean updateProject(Project project) {
        // Check if user is an HDB Manager
        if (!authStore.isHdbManager()) {
            return false;
        }
        
        // Check if user is the manager in charge of this project
        User currentUser = authStore.getCurrentUser();
        Project existingProject = dataStore.getProjectById(project.getProjectId());
        
        if (existingProject == null || 
            !existingProject.getManagerInCharge().equals(currentUser.getNric())) {
            return false;
        }
        
        return dataStore.updateProject(project);
    }
    
    /**
     * Deletes a project
     * @param projectId ID of the project to delete
     * @return true if deletion successful, false otherwise
     */
    public boolean deleteProject(String projectId) {
        // Check if user is an HDB Manager
        if (!authStore.isHdbManager()) {
            return false;
        }
        
        // Check if user is the manager in charge of this project
        User currentUser = authStore.getCurrentUser();
        Project project = dataStore.getProjectById(projectId);
        
        if (project == null || 
            !project.getManagerInCharge().equals(currentUser.getNric())) {
            return false;
        }
        
        return dataStore.deleteProject(projectId);
    }
    
    /**
     * Toggles the visibility of a project
     * @param projectId ID of the project
     * @param visible Visibility status to set
     * @return true if toggle successful, false otherwise
     */
    public boolean toggleProjectVisibility(String projectId, boolean visible) {
        // Check if user is an HDB Manager
        if (!authStore.isHdbManager()) {
            return false;
        }
        
        // Check if user is the manager in charge of this project
        User currentUser = authStore.getCurrentUser();
        Project project = dataStore.getProjectById(projectId);
        
        if (project == null || 
            !project.getManagerInCharge().equals(currentUser.getNric())) {
            return false;
        }
        
        project.setVisible(visible);
        return dataStore.updateProject(project);
    }
    
    /**
     * Gets a project by ID
     * @param projectId Project ID
     * @return Project object if found, null otherwise
     */
    public Project getProjectById(String projectId) {
        return dataStore.getProjectById(projectId);
    }
    
    /**
     * Gets all projects
     * Only HDB Managers can see all projects regardless of visibility
     * @return List of all viewable projects
     */
    public List<Project> getAllProjects() {
        // HDB Manager can see all projects
        if (authStore.isHdbManager()) {
            return dataStore.getAllProjects();
        }
        
        // Others can only see visible projects
        return getVisibleProjects();
    }
    
    /**
     * Gets all visible projects
     * @return List of visible projects
     */
    public List<Project> getVisibleProjects() {
        return dataStore.getVisibleProjects();
    }
    
    /**
     * Gets projects created by the current manager
     * @return List of projects created by the current manager
     */
    public List<Project> getProjectsByCurrentManager() {
        User currentUser = authStore.getCurrentUser();
        if (currentUser == null || !authStore.isHdbManager()) {
            return new ArrayList<>();
        }
        
        return dataStore.getProjectsByManager(currentUser.getNric());
    }
    
    /**
     * Gets projects that a user is eligible to apply for
     * @return List of eligible projects
     */
    public List<Project> getEligibleProjects() {
        User currentUser = authStore.getCurrentUser();
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        List<Project> eligibleProjects = new ArrayList<>();
        List<Project> visibleProjects = dataStore.getVisibleProjects();
        
        for (Project project : visibleProjects) {
            // Skip projects where user is an HDB Officer
            if (project.getOfficerIds().contains(currentUser.getNric())) {
                continue;
            }
            
            // Check eligibility based on marital status and age
            if (currentUser.getMaritalStatus() == MaritalStatus.SINGLE) {
                // Singles (35+ years) can only apply for 2-Room flats
                if (currentUser.getAge() >= 35 && 
                    project.getAvailableUnits(FlatType.TWO_ROOM) > 0) {
                    eligibleProjects.add(project);
                }
            } else if (currentUser.getMaritalStatus() == MaritalStatus.MARRIED) {
                // Married (21+ years) can apply for any flat type
                if (currentUser.getAge() >= 21 && 
                   (project.getAvailableUnits(FlatType.TWO_ROOM) > 0 || 
                    project.getAvailableUnits(FlatType.THREE_ROOM) > 0)) {
                    eligibleProjects.add(project);
                }
            }
        }
        
        return eligibleProjects;
    }
    
    /**
     * Registers the current user as an HDB Officer for a project
     * @param projectId ID of the project
     * @return true if registration successful, false otherwise
     */
    public boolean registerAsOfficer(String projectId) {
        User currentUser = authStore.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        Project project = dataStore.getProjectById(projectId);
        if (project == null) {
            return false;
        }
        
        // Check if user has applied for this project
        if (currentUser.getAppliedProjectId() != null && 
            currentUser.getAppliedProjectId().equals(projectId)) {
            return false;
        }
        
        // Check if user is already an officer for another project with overlapping dates
        List<Registration> officerRegistrations = dataStore.getRegistrationsByOfficer(currentUser.getNric());
        for (Registration registration : officerRegistrations) {
            if (registration.getStatus() == RegistrationStatus.APPROVED) {
                Project otherProject = dataStore.getProjectById(registration.getProjectId());
                if (otherProject != null && 
                    datesOverlap(otherProject.getOpeningDate(), otherProject.getClosingDate(),
                                project.getOpeningDate(), project.getClosingDate())) {
                    return false;
                }
            }
        }
        
        // Check if the project has available officer slots
        if (project.getOfficerIds().size() >= project.getOfficerSlots()) {
            return false;
        }
        
        // Create registration
        String registrationId = UUID.randomUUID().toString();
        Registration registration = new Registration(registrationId, currentUser.getNric(), projectId);
        return dataStore.addRegistration(registration);
    }
    
    /**
     * Approves or rejects an HDB Officer registration
     * @param registrationId Registration ID
     * @param approve true to approve, false to reject
     * @return true if operation successful, false otherwise
     */
    public boolean processOfficerRegistration(String registrationId, boolean approve) {
        // Check if user is an HDB Manager
        if (!authStore.isHdbManager()) {
            return false;
        }
        
        Registration registration = dataStore.getRegistrationById(registrationId);
        if (registration == null) {
            return false;
        }
        
        // Check if user is the manager in charge of this project
        User currentUser = authStore.getCurrentUser();
        Project project = dataStore.getProjectById(registration.getProjectId());
        
        if (project == null || 
            !project.getManagerInCharge().equals(currentUser.getNric())) {
            return false;
        }
        
        if (approve) {
            // Check if project still has officer slots
            if (project.getOfficerIds().size() >= project.getOfficerSlots()) {
                return false;
            }
            
            // Update registration status
            registration.setStatus(RegistrationStatus.APPROVED);
            
            // Add officer to project
            project.addOfficer(registration.getOfficerNric());
            dataStore.updateProject(project);
        } else {
            // Reject registration
            registration.setStatus(RegistrationStatus.REJECTED);
        }
        
        return dataStore.updateRegistration(registration);
    }
    
    /**
     * Gets all pending officer registrations for projects managed by the current manager
     * @return List of pending registrations
     */
    public List<Registration> getPendingOfficerRegistrations() {
        // Check if user is an HDB Manager
        if (!authStore.isHdbManager()) {
            return new ArrayList<>();
        }
        
        User currentUser = authStore.getCurrentUser();
        List<Project> managerProjects = dataStore.getProjectsByManager(currentUser.getNric());
        
        List<Registration> pendingRegistrations = new ArrayList<>();
        for (Project project : managerProjects) {
            List<Registration> projectRegistrations = 
                dataStore.getRegistrationsByProjectAndStatus(project.getProjectId(), RegistrationStatus.PENDING);
            pendingRegistrations.addAll(projectRegistrations);
        }
        
        return pendingRegistrations;
    }
    
    /**
     * Helper method to check if two date ranges overlap
     */
    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !start1.isAfter(end2) && !start2.isAfter(end1);
    }
}