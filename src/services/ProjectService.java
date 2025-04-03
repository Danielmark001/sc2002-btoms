package services;

import interfaces.IProjectService;
import models.*;
import enumeration.*;
import exceptions.ProjectValidationException;
import stores.AuthStore;
import stores.DataStore;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing BTO projects
 */
public class ProjectService implements IProjectService {
    private static ProjectService instance;
    private DataStore dataStore;
    private AuthStore authStore;

    private ProjectService() {
        this.dataStore = DataStore.getInstance();
        this.authStore = AuthStore.getInstance();
    }

    /**
     * Gets the singleton instance of ProjectService
     * @return ProjectService instance
     */
    public static synchronized ProjectService getInstance() {
        if (instance == null) {
            instance = new ProjectService();
        }
        return instance;
    }

    @Override
    public BTOProject createProject(String projectName, String neighborhood, 
                               int twoRoomUnits, int threeRoomUnits,
                               LocalDate openingDate, LocalDate closingDate, 
                               int officerSlots) {
        // Validate manager
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            throw new SecurityException("Only HDB Managers can create projects");
        }
        
        // Validate inputs
        validateProjectInputs(projectName, neighborhood, openingDate, closingDate, officerSlots);
        
        // Validate unit counts
        if (twoRoomUnits < 0 || threeRoomUnits < 0) {
            throw new IllegalArgumentException("Unit counts cannot be negative");
        }
        
        if (twoRoomUnits == 0 && threeRoomUnits == 0) {
            throw new IllegalArgumentException("At least one flat type must have units");
        }
        
        // Check if manager already has an active project during this period
        HDBManager manager = (HDBManager) currentUser;
        if (hasActiveProjectDuring(manager, openingDate, closingDate)) {
            throw new IllegalStateException("Manager already has an active project during this period");
        }
        
        // Check for duplicate project name
        if (getProjectByName(projectName) != null) {
            throw new IllegalArgumentException("Project with name '" + projectName + "' already exists");
        }
        
        // Create the project
        BTOProject project = new BTOProject(projectName, neighborhood, openingDate, closingDate);
        
        // Set manager
        project.setHdbManager(manager);
        
        // Set flat types
        Map<FlatType, Integer> flatTypes = new HashMap<>();
        flatTypes.put(FlatType.TWO_ROOM, twoRoomUnits);
        flatTypes.put(FlatType.THREE_ROOM, threeRoomUnits);
        project.setFlatTypes(flatTypes);
        
        // Set officer slots
        project.setAvailableHDBOfficerSlots(officerSlots);
        
        // Set visibility to true by default
        project.setVisibility(true);
        
        // Add to manager's projects
        manager.addManagedProject(project);
        
        // Save to data store
        dataStore.addProject(project);
        
        return project;
    }

    @Override
    public boolean updateProject(BTOProject project) {
        // Validate
        if (project == null) {
            return false;
        }
        
        // Verify current user is the manager of this project
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager) || 
            !project.getHdbManager().equals(currentUser)) {
            return false;
        }
        
        // Update in data store
        return dataStore.updateProject(project);
    }

    @Override
    public boolean deleteProject(String projectId) {
        // Get project
        BTOProject project = getProjectById(projectId);
        if (project == null) {
            return false;
        }
        
        return deleteProject(project);
    }

    /**
     * Deletes a project
     * @param project Project to delete
     * @return true if deletion successful
     */
    public boolean deleteProject(BTOProject project) {
        // Validate
        if (project == null) {
            return false;
        }
        
        // Verify current user is the manager of this project
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager) || 
            !project.getHdbManager().equals(currentUser)) {
            return false;
        }
        
        // Check for active bookings
        if (hasActiveBookings(project)) {
            return false;
        }
        
        // Remove from manager's projects
        HDBManager manager = project.getHdbManager();
        manager.removeManagedProject(project);
        
        // Delete from data store
        return dataStore.deleteProject(project.getProjectId());
    }

    @Override
    public boolean toggleProjectVisibility(String projectId, boolean visible) {
        // Get project
        BTOProject project = getProjectById(projectId);
        if (project == null) {
            return false;
        }
        
        // Verify current user is the manager of this project
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager) || 
            !project.getHdbManager().equals(currentUser)) {
            return false;
        }
        
        // Set visibility
        project.setVisibility(visible);
        
        // Update in data store
        return dataStore.updateProject(project);
    }

    @Override
    public BTOProject getProjectById(String projectId) {
        if (projectId == null) {
            return null;
        }
        
        return dataStore.getProjectById(projectId);
    }

    @Override
    public List<BTOProject> getAllProjects() {
        return dataStore.getAllProjects();
    }

    @Override
    public List<BTOProject> getVisibleProjects() {
        // Get current user
        User currentUser = authStore.getCurrentUser();
        if (currentUser == null) {
            return Collections.emptyList();
        }
        
        // If user is an HDB Manager, return all projects
        if (currentUser instanceof HDBManager) {
            return getAllProjects();
        }
        
        // Filter for visible projects
        return getAllProjects().stream()
            .filter(BTOProject::isVisible)
            .collect(Collectors.toList());
    }

    @Override
    public List<BTOProject> getEligibleProjects() {
        // Get current user
        User currentUser = authStore.getCurrentUser();
        if (currentUser == null) {
            return Collections.emptyList();
        }
        
        // Filter for visible and eligible projects
        return getAllProjects().stream()
            .filter(BTOProject::isVisible)
            .filter(project -> project.isEligibleForApplicant(currentUser))
            .collect(Collectors.toList());
    }

    @Override
    public List<BTOProject> getProjectsByCurrentManager() {
        // Get current user
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return Collections.emptyList();
        }
        
        // Get projects managed by this manager
        HDBManager manager = (HDBManager) currentUser;
        return manager.getManagedProjects();
    }

    @Override
    public boolean registerAsOfficer(String projectId) {
        // Get project
        BTOProject project = getProjectById(projectId);
        if (project == null) {
            return false;
        }
        
        // Get current user
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBOfficer)) {
            return false;
        }
        
        HDBOfficer officer = (HDBOfficer) currentUser;
        
        // Check if already registered for this project
        if (isOfficerRegisteredForProject(officer, project)) {
            return false;
        }
        
        // Check if officer has applied for this project
        if (hasAppliedForProject(officer, project)) {
            return false;
        }
        
        // Check if already registered for another project in the same period
        if (isRegisteredForOverlappingProject(officer, project)) {
            return false;
        }
        
        // Check if project has available slots
        if (project.getAvailableHDBOfficerSlots() <= 0) {
            return false;
        }
        
        // Create registration
        Registration registration = new Registration(officer, project);
        registration.setStatus(Registration.RegistrationStatus.PENDING);
        registration.setRegistrationDate(LocalDate.now());
        
        // Add to officer's registrations
        officer.addRegistration(registration);
        
        // Add to project's registrations
        project.addRegistration(registration);
        
        // Save to data store
        dataStore.addRegistration(registration);
        
        return true;
    }

    @Override
    public boolean processOfficerRegistration(String registrationId, boolean approve) {
        // Find the registration
        Registration registration = getRegistrationById(registrationId);
        if (registration == null) {
            return false;
        }
        
        // Get current user
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return false;
        }
        
        // Check if current user is the manager of the project
        HDBManager manager = (HDBManager) currentUser;
        if (!registration.getProject().getHdbManager().equals(manager)) {
            return false;
        }
        
        // Check if registration is pending
        if (registration.getStatus() != Registration.RegistrationStatus.PENDING) {
            return false;
        }
        
        // Process based on approval decision
        if (approve) {
            // Check if project still has available slots
            BTOProject project = registration.getProject();
            if (project.getAvailableHDBOfficerSlots() <= 0) {
                return false;
            }
            
            // Approve registration
            registration.setStatus(Registration.RegistrationStatus.APPROVED);
            
            // Assign project to officer
            HDBOfficer officer = (HDBOfficer) registration.getOfficer();
            officer.setHandlingProject(project);
            
            // Decrement available slots
            project.setAvailableHDBOfficerSlots(project.getAvailableHDBOfficerSlots() - 1);
        } else {
            // Reject registration
            registration.setStatus(Registration.RegistrationStatus.REJECTED);
        }
        
        // Update in data store
        dataStore.updateRegistration(registration);
        
        return true;
    }

    @Override
    public List<Registration> getPendingOfficerRegistrations() {
        // Get current user
        User currentUser = authStore.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return Collections.emptyList();
        }
        
        // Get projects managed by this manager
        HDBManager manager = (HDBManager) currentUser;
        List<BTOProject> managedProjects = manager.getManagedProjects();
        
        // Get pending registrations for these projects
        List<Registration> pendingRegistrations = new ArrayList<>();
        for (BTOProject project : managedProjects) {
            pendingRegistrations.addAll(
                project.getRegistrations().stream()
                    .filter(r -> r.getStatus() == Registration.RegistrationStatus.PENDING)
                    .collect(Collectors.toList())
            );
        }
        
        return pendingRegistrations;
    }

    @Override
    public List<Registration> getRegistrationsByStatus(RegistrationStatus status) {
        return getAllRegistrations().stream()
            .filter(r -> r.getStatus() == status)
            .collect(Collectors.toList());
    }

    /**
     * Gets a project by name
     * @param projectName Name of the project
     * @return Project if found, null otherwise
     */
    public BTOProject getProjectByName(String projectName) {
        if (projectName == null) {
            return null;
        }
        
        return getAllProjects().stream()
            .filter(p -> p.getProjectName().equalsIgnoreCase(projectName))
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets projects managed by a specific manager
     * @param manager HDB Manager
     * @return List of projects managed by the manager
     */
    public List<BTOProject> getProjectsByManager(HDBManager manager) {
        if (manager == null) {
            return Collections.emptyList();
        }
        
        return getAllProjects().stream()
            .filter(p -> p.getHdbManager() != null && p.getHdbManager().equals(manager))
            .collect(Collectors.toList());
    }

    /**
     * Gets projects visible to a specific user
     * @param user User to check visibility for
     * @return List of visible projects
     */
    public List<BTOProject> getVisibleProjects(User user) {
        if (user == null) {
            return Collections.emptyList();
        }
        
        // If user is an HDB Manager, return all projects
        if (user instanceof HDBManager) {
            return getAllProjects();
        }
        
        // Filter for visible projects
        return getAllProjects().stream()
            .filter(BTOProject::isVisible)
            .collect(Collectors.toList());
    }

    /**
     * Gets all registrations
     * @return List of all registrations
     */
    private List<Registration> getAllRegistrations() {
        List<Registration> registrations = new ArrayList<>();
        
        // Get registrations from all projects
        for (BTOProject project : getAllProjects()) {
            registrations.addAll(project.getRegistrations());
        }
        
        return registrations;
    }

    /**
     * Gets a registration by ID
     * @param registrationId Registration ID
     * @return Registration if found, null otherwise
     */
    private Registration getRegistrationById(String registrationId) {
        if (registrationId == null) {
            return null;
        }
        
        return getAllRegistrations().stream()
            .filter(r -> r.getRegistrationId().equals(registrationId))
            .findFirst()
            .orElse(null);
    }

    /**
     * Checks if a manager already has an active project during a given period
     * @param manager HDB Manager to check
     * @param openingDate Opening date of the new project
     * @param closingDate Closing date of the new project
     * @return true if manager has an active project in this period
     */
    private boolean hasActiveProjectDuring(HDBManager manager, LocalDate openingDate, LocalDate closingDate) {
        if (manager == null || openingDate == null || closingDate == null) {
            return false;
        }
        
        return manager.getManagedProjects().stream()
            .filter(BTOProject::isVisible)
            .anyMatch(p -> datesOverlap(
                p.getApplicationOpeningDate(), p.getApplicationClosingDate(),
                openingDate, closingDate
            ));
    }

    /**
     * Checks if two date ranges overlap
     * @param start1 Start date of first range
     * @param end1 End date of first range
     * @param start2 Start date of second range
     * @param end2 End date of second range
     * @return true if the ranges overlap
     */
    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !end1.isBefore(start2) && !start1.isAfter(end2);
    }

    /**
     * Checks if a project has any active bookings
     * @param project Project to check
     * @return true if project has active bookings
     */
    private boolean hasActiveBookings(BTOProject project) {
        if (project == null) {
            return false;
        }
        
        return project.getApplications().stream()
            .anyMatch(a -> a.getStatus() == ApplicationStatus.BOOKED);
    }

    /**
     * Checks if an officer is already registered for a project
     * @param officer HDB Officer to check
     * @param project Project to check
     * @return true if officer is registered for the project
     */
    private boolean isOfficerRegisteredForProject(HDBOfficer officer, BTOProject project) {
        if (officer == null || project == null) {
            return false;
        }
        
        return officer.getRegistrations().stream()
            .anyMatch(r -> r.getProject().equals(project) && 
                     (r.getStatus() == Registration.RegistrationStatus.PENDING || 
                      r.getStatus() == Registration.RegistrationStatus.APPROVED));
    }

    /**
     * Checks if an officer has applied for a project as an applicant
     * @param officer HDB Officer to check
     * @param project Project to check
     * @return true if officer has applied for the project
     */
    private boolean hasAppliedForProject(HDBOfficer officer, BTOProject project) {
        if (officer == null || project == null) {
            return false;
        }
        
        // Get application service
        ApplicationService applicationService = ApplicationService.getInstance();
        
        // Get officer's applications
        List<Application> applications = applicationService.getApplicationsByApplicantNric(officer.getNric());
        
        // Check if any application is for this project
        return applications.stream()
            .anyMatch(a -> a.getProject().equals(project));
    }

    /**
     * Checks if an officer is registered for another project with overlapping period
     * @param officer HDB Officer to check
     * @param project New project to register for
     * @return true if officer is registered for an overlapping project
     */
    private boolean isRegisteredForOverlappingProject(HDBOfficer officer, BTOProject project) {
        if (officer == null || project == null) {
            return false;
        }
        
        return officer.getRegistrations().stream()
            .filter(r -> r.getStatus() == Registration.RegistrationStatus.APPROVED)
            .map(Registration::getProject)
            .anyMatch(p -> datesOverlap(
                p.getApplicationOpeningDate(), p.getApplicationClosingDate(),
                project.getApplicationOpeningDate(), project.getApplicationClosingDate()
            ));
    }

    /**
     * Validates project inputs
     * @param projectName Project name
     * @param neighborhood Neighborhood
     * @param openingDate Opening date
     * @param closingDate Closing date
     * @param officerSlots Officer slots
     * @throws IllegalArgumentException if inputs are invalid
     */
    private void validateProjectInputs(String projectName, String neighborhood, 
                                    LocalDate openingDate, LocalDate closingDate, 
            int officerSlots) {
        // Validate project name
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }

        if (projectName.length() > 100) {
            throw new IllegalArgumentException("Project name cannot exceed 100 characters");
        }

        // Validate neighborhood
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be empty");
        }

        if (neighborhood.length() > 50) {
            throw new IllegalArgumentException("Neighborhood cannot exceed 50 characters");
        }

        // Validate dates
        if (openingDate == null || closingDate == null) {
            throw new IllegalArgumentException("Opening and closing dates cannot be null");
        }

        if (openingDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Opening date cannot be in the past");
        }

        if (closingDate.isBefore(openingDate)) {
            throw new IllegalArgumentException("Closing date must be after opening date");
        }

        // Validate officer slots
        if (officerSlots < 0 || officerSlots > 10) {
            throw new IllegalArgumentException("Officer slots must be between 0 and 10");
        }
    }
    
}