package interfaces;

import java.time.LocalDate;
import java.util.List;

import models.entity.Project;
import models.entity.Registration;
import models.enumeration.RegistrationStatus;

/**
 * Interface for the Project Service
 * Defines methods for project-related operations
 */
public interface IProjectService {
    
    /**
     * Gets the singleton instance of ProjectService
     * @return ProjectService instance
     */
    public static IProjectService getInstance() {
        return null; // Implemented by concrete class
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
    Project createProject(String projectName, String neighborhood, 
                       int twoRoomUnits, int threeRoomUnits,
                       LocalDate openingDate, LocalDate closingDate, 
                       int officerSlots);
    
    /**
     * Updates an existing project
     * @param project Project to update
     * @return true if update successful, false otherwise
     */
    boolean updateProject(Project project);
    
    /**
     * Deletes a project
     * @param projectId ID of the project to delete
     * @return true if deletion successful, false otherwise
     */
    boolean deleteProject(String projectId);
    
    /**
     * Toggles the visibility of a project
     * @param projectId ID of the project
     * @param visible Visibility status to set
     * @return true if toggle successful, false otherwise
     */
    boolean toggleProjectVisibility(String projectId, boolean visible);
    
    /**
     * Gets a project by ID
     * @param projectId Project ID
     * @return Project object if found, null otherwise
     */
    Project getProjectById(String projectId);
    
    /**
     * Gets all projects
     * @return List of all projects
     */
    List<Project> getAllProjects();
    
    /**
     * Gets all visible projects
     * @return List of visible projects
     */
    List<Project> getVisibleProjects();
    
    /**
     * Gets projects eligible for the current user
     * @return List of eligible projects
     */
    List<Project> getEligibleProjects();
    
    /**
     * Gets projects managed by the current user
     * @return List of managed projects
     */
    List<Project> getProjectsByCurrentManager();
    
    /**
     * Registers a user as an HDB Officer for a project
     * @param projectId ID of the project
     * @return true if registration successful, false otherwise
     */
    boolean registerAsOfficer(String projectId);
    
    /**
     * Approves or rejects an HDB Officer registration
     * @param registrationId Registration ID
     * @param approve true to approve, false to reject
     * @return true if process successful, false otherwise
     */
    boolean processOfficerRegistration(String registrationId, boolean approve);
    
    /**
     * Gets pending officer registrations for projects managed by the current user
     * @return List of pending registrations
     */
    List<Registration> getPendingOfficerRegistrations();
    
    /**
     * Gets registrations by status
     * @param status Status to filter by
     * @return List of registrations with the specified status
     */
    List<Registration> getRegistrationsByStatus(RegistrationStatus status);
}