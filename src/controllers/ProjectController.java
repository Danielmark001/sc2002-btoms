package controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import enumeration.RegistrationStatus;
import models.Applicant;
import models.BTOApplication;
import models.BTOProject;
import models.HDBManager;
import models.HDBOfficer;
import models.User;
import services.BTOApplicationService;
import services.EnquiryService;
import services.ProjectService;
import services.UserService;
import view.ProjectView;
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

    /**
    * Gets projects that an HDB Officer is handling or eligible to handle
    * 
    * @param officerNric NRIC of the officer
    * @return List of projects for the officer
    */
    public List<BTOProject> getProjectsByOfficer(String officerNric) {
        if (officerNric == null || officerNric.trim().isEmpty()) {
            if (projectView != null) {
                projectView.displayError("Officer NRIC cannot be empty");
            }
            return new ArrayList<>();
        }

        User officer = userService.getUserByNRIC(officerNric);
        if (!(officer instanceof HDBOfficer)) {
            if (projectView != null) {
                projectView.displayError("User is not an HDB Officer");
            }
            return new ArrayList<>();
        }

        HDBOfficer hdbOfficer = (HDBOfficer) officer;

        List<BTOProject> result = new ArrayList<>();

        // Add the project the officer is currently handling
        if (hdbOfficer.getHandlingProject() != null) {
            result.add(hdbOfficer.getHandlingProject());
        }

        // Get all projects and filter for those where officer has approved registration
        List<BTOProject> allProjects = projectService.getAllProjects();

        for (BTOProject project : allProjects) {
            // Check if officer has an approved registration for this project
            boolean hasApprovedRegistration = project.getRegistrations().stream()
                    .anyMatch(reg -> reg.getOfficer().getNric().equals(officerNric) &&
                            reg.getStatus() == RegistrationStatus.APPROVED);

            if (hasApprovedRegistration && !result.contains(project)) {
                result.add(project);
            }
        }

        return result;
    }

    /**
     * Gets the project that the current user (if an HDB Officer) is handling
     * 
     * @return Project being handled or null if user is not an officer or not handling any project
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
     * Gets enquiries for a specific project
     * 
     * @param project Project to get enquiries for
     * @return List of enquiries for the project
     */
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        if (project == null) {
            if (projectView != null) {
                projectView.displayError("Project cannot be null");
            }
            return new ArrayList<>();
        }

        try {
            // Use the enquiry service to get enquiries
            EnquiryService enquiryService = EnquiryService.getInstance();
            return enquiryService.getEnquiriesByProject(project);
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error retrieving enquiries: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }

    /**
     * Registers the current user as an HDB Officer for a project
     * 
     * @param projectId ID of the project to register for
     * @return true if registration succeeds
     */
    public boolean registerAsOfficer(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            if (projectView != null) {
                projectView.displayError("Project ID cannot be empty");
            }
            return false;
        }

        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            if (projectView != null) {
                projectView.displayError("Only HDB Officers can register for projects");
            }
            return false;
        }

        try {
            boolean success = projectService.registerAsOfficer(projectId);

            if (success) {
                if (projectView != null) {
                    projectView.displaySuccess("Registration submitted successfully");
                }
            } else {
                if (projectView != null) {
                    projectView.displayError("Failed to register. Please check eligibility criteria");
                }
            }

            return success;
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error during registration: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Registers the current user as an HDB Manager
     * 
     * @param projectId ID of the project to manage
     * @return true if registration succeeds
     */
    public boolean registerAsManager(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            if (projectView != null) {
                projectView.displayError("Project ID cannot be empty");
            }
            return false;
        }

        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            if (projectView != null) {
                projectView.displayError("Only HDB Managers can manage projects");
            }
            return false;
        }

        try {
            // Get the project by ID
            BTOProject project = projectService.getProjectById(projectId);
            if (project == null) {
                if (projectView != null) {
                    projectView.displayError("Project not found");
                }
                return false;
            }

            // Set the manager for the project
            HDBManager manager = (HDBManager) currentUser;
            project.setHdbManager(manager);

            // Add to manager's projects
            manager.addManagedProject(project);

            // Update the project
            boolean success = projectService.updateProject(project);

            if (success) {
                if (projectView != null) {
                    projectView.displaySuccess("Successfully registered as manager for the project");
                }
            } else {
                if (projectView != null) {
                    projectView.displayError("Failed to register as manager");
                }
            }

            return success;
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error during registration: " + e.getMessage());
            }
            return false;
        }
    }

    /**
     * Creates an application for a project on behalf of an applicant
     * 
     * @param projectId ID of the project to apply for
     * @return true if application succeeds
     */
    public boolean registerAsApplicant(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            if (projectView != null) {
                projectView.displayError("Project ID cannot be empty");
            }
            return false;
        }

        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof Applicant)) {
            if (projectView != null) {
                projectView.displayError("Only applicants can apply for projects");
            }
            return false;
        }

        try {
            // Get the project by ID
            BTOProject project = projectService.getProjectById(projectId);
            if (project == null) {
                if (projectView != null) {
                    projectView.displayError("Project not found");
                }
                return false;
            }

            // Check eligibility
            if (!project.isEligibleForApplicant(currentUser)) {
                if (projectView != null) {
                    projectView.displayError("You are not eligible for this project");
                }
                return false;
            }

            // Use application service to create application
            BTOApplicationService applicationService = BTOApplicationService.getInstance();
            Applicant applicant = (Applicant) currentUser;

            BTOApplication application = applicationService.createApplication(applicant, project);

            if (application != null) {
                if (projectView != null) {
                    projectView.displaySuccess("Application submitted successfully");
                }
                return true;
            } else {
                if (projectView != null) {
                    projectView.displayError("Failed to submit application");
                }
                return false;
            }
        } catch (Exception e) {
            if (projectView != null) {
                projectView.displayError("Error during application: " + e.getMessage());
            }
            return false;
        }
    }

    public List<Registration> getOfficerRegistrationsByUser(String officerNric) {
        if (officerNric == null || officerNric.trim().isEmpty()) {
            if (projectView != null) {
                projectView.displayError("Officer NRIC cannot be empty");
            }
            return new ArrayList<>();
        }

        User officer = userService.getUserByNRIC(officerNric);
        if (!(officer instanceof HDBOfficer)) {
            if (projectView != null) {
                projectView.displayError("User is not an HDB Officer");
            }
            return new ArrayList<>();
        }

        HDBOfficer hdbOfficer = (HDBOfficer) officer;
        return hdbOfficer.getRegistrations();
    }
}