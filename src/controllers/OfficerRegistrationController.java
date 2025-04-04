package controllers;

import models.BTOProject;
import models.HDBManager;
import models.HDBOfficer;
import models.User;
import enumeration.UserStatus;
import services.ProjectService;
import services.UserService;
import view.OfficerProjectView;

import java.util.ArrayList;
import java.util.List;
import models.Registration;
import enumeration.RegistrationStatus;


import java.time.LocalDate;

public class OfficerRegistrationController {
    private UserService userService;
    private ProjectService projectService;
    private OfficerProjectView view;

    public OfficerRegistrationController(OfficerProjectView view) {
        this.userService = UserService.getInstance();
        this.projectService = ProjectService.getInstance();
        this.view = view;
    }

    /**
     * Registers an HDB Officer for a project
     * 
     * @param nric Officer's NRIC
     * @param name Officer's name
     * @param projectName Project to register for
     * @return Registered HDB Officer
     */
    public HDBOfficer registerOfficer(String nric, String name, String projectName) {
        // Validate project exists
        BTOProject project = projectService.getProjectByName(projectName);
        if (project == null) {
            view.displayError("Project not found");
            return null;
        }

        // Check current user is a manager
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            view.displayError("Only HDB Managers can register officers");
            return null;
        }
        HDBManager manager = (HDBManager) currentUser;

        // Verify project is managed by current manager
        if (!project.getHdbManager().equals(manager)) {
            view.displayError("You can only register officers for your own projects");
            return null;
        }

        // Check officer slots availability
        if (project.getAvailableHDBOfficerSlots() <= 0) {
            view.displayError("No available officer slots for this project");
            return null;
        }

        // Create officer
        HDBOfficer officer = (HDBOfficer) userService.createUser(
            nric, 
            name, 
            LocalDate.now(), 
            null, 
            UserStatus.OFFICER
        );

        // Set handling project
        officer.setHandlingProject(project);

        // Decrement available officer slots
        project.setAvailableHDBOfficerSlots(project.getAvailableHDBOfficerSlots() - 1);

        view.displaySuccess("Officer registered successfully");
        return officer;
    }

    /**
     * Approves or rejects an officer registration
     * 
     * @param officer Officer to process
     * @param approve Whether to approve or reject
     * @return true if processing successful
     */
    public boolean processOfficerRegistration(HDBOfficer officer, boolean approve) {
        // Check current user is a manager
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            view.displayError("Only HDB Managers can process registrations");
            return false;
        }
        HDBManager manager = (HDBManager) currentUser;

        // Validate officer and project
        if (officer.getHandlingProject() == null) {
            view.displayError("Officer is not associated with a project");
            return false;
        }

        // Verify project is managed by current manager
        if (!officer.getHandlingProject().getHdbManager().equals(manager)) {
            view.displayError("You can only process registrations for your own projects");
            return false;
        }

        if (approve) {
            // Additional logic for approval if needed
            view.displaySuccess("Officer registration approved");
            return true;
        } else {
            // Return officer slot and remove officer
            BTOProject project = officer.getHandlingProject();
            project.setAvailableHDBOfficerSlots(project.getAvailableHDBOfficerSlots() + 1);
            userService.deleteUser(officer.getNric());
            
            view.displaySuccess("Officer registration rejected");
            return true;
        }
    }

    /**
     * Checks if an officer can be registered for a project
     * 
     * @param officer Officer to check
     * @param project Project to check against
     * @return true if officer can be registered
     */
    public boolean canRegisterForProject(HDBOfficer officer, BTOProject project) {
        // Check if officer is already registered for another project
        return project.getAvailableHDBOfficerSlots() > 0 &&
                officer.getHandlingProject() == null;
    }
    
    /**
     * Gets all registrations by status
     * 
     * @param status Registration status to filter by
     * @return List of registrations with the specified status
     */
    public List<Registration> getRegistrationByStatus(RegistrationStatus status) {
        return projectService.getRegistrationsByStatus(status);
    }

    public boolean removeUser(User user) {
        if (user == null) {
            return false;
        }
        return userService.deleteUser(user.getNric());
    }

    public RegistrationStatus getCurrentRegistrationStatus() {
        User currentUser = userService.getCurrentUser();
        if (currentUser instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) currentUser;
            return officer.getHandlingProject().getRegistrationStatus();
        }
        return null;
    }
    
 /**
 * Registers the current user as an HDB Officer for a project
 * 
 * @param project Project to register for
 * @return true if registration succeeds
 */
public boolean registerForProject(BTOProject project) {
    // Validate project
    if (project == null) {
        if (view != null) {
            view.displayError("Project cannot be null");
        }
        return false;
    }
    
    // Get current user
    User currentUser = userService.getCurrentUser();
    if (!(currentUser instanceof HDBOfficer)) {
        if (view != null) {
            view.displayError("Only HDB Officers can register for projects");
        }
        return false;
    }
    
    HDBOfficer officer = (HDBOfficer) currentUser;
    
    // Check officer eligibility
    if (officer.getHandlingProject() != null) {
        if (view != null) {
            view.displayError("You are already handling another project");
        }
        return false;
    }
    
    // Check if officer has applied for this project as an applicant
    boolean hasApplied = officer.getApplications().stream()
        .anyMatch(app -> app.getProject().equals(project));
    
    if (hasApplied) {
        if (view != null) {
            view.displayError("You cannot register for a project you have applied for");
        }
        return false;
    }
    
    // Check if project has available slots
    if (project.getAvailableHDBOfficerSlots() <= 0) {
        if (view != null) {
            view.displayError("No available officer slots for this project");
        }
        return false;
    }
    
    // Create registration
    try {
        Registration registration = new Registration(officer, project);
        registration.setStatus(RegistrationStatus.PENDING);
        
        // Add to officer's registrations
        officer.addRegistration(registration);
        
        // Add to project's registrations
        project.addRegistration(registration);
        
        if (view != null) {
            view.displaySuccess("Registration submitted successfully. Awaiting approval.");
        }
        
        return true;
    } catch (Exception e) {
        if (view != null) {
            view.displayError("Error during registration: " + e.getMessage());
        }
        return false;
    }
}

/**
 * Rejects an officer registration
 * 
 * @param registration Registration to reject
 * @return true if rejection succeeds
 */
public boolean rejectRegistration(Registration registration) {
    // Validate registration
    if (registration == null) {
        if (view != null) {
            view.displayError("Registration cannot be null");
        }
        return false;
    }
    
    // Get current user
    User currentUser = userService.getCurrentUser();
    if (!(currentUser instanceof HDBManager)) {
        if (view != null) {
            view.displayError("Only HDB Managers can approve or reject registrations");
        }
        return false;
    }
    
    HDBManager manager = (HDBManager) currentUser;
    
    // Check if manager is in charge of this project
    if (!registration.getProject().getHdbManager().equals(manager)) {
        if (view != null) {
            view.displayError("You can only manage registrations for your own projects");
        }
        return false;
    }
    
    // Check if registration is pending
    if (registration.getStatus() != RegistrationStatus.PENDING) {
        if (view != null) {
            view.displayError("Only pending registrations can be rejected");
        }
        return false;
    }
    
    // Update registration status
    try {
        registration.setStatus(RegistrationStatus.REJECTED);
        
        if (view != null) {
            view.displaySuccess("Registration rejected successfully");
        }
        
        return true;
    } catch (Exception e) {
        if (view != null) {
            view.displayError("Error rejecting registration: " + e.getMessage());
        }
        return false;
    }
}

/**
 * Approves an officer registration
 * 
 * @param registration Registration to approve
 * @return true if approval succeeds
 */
public boolean approveRegistration(Registration registration) {
    // Validate registration
    if (registration == null) {
        if (view != null) {
            view.displayError("Registration cannot be null");
        }
        return false;
    }
    
    // Get current user
    User currentUser = userService.getCurrentUser();
    if (!(currentUser instanceof HDBManager)) {
        if (view != null) {
            view.displayError("Only HDB Managers can approve or reject registrations");
        }
        return false;
    }
    
    HDBManager manager = (HDBManager) currentUser;
    
    // Check if manager is in charge of this project
    if (!registration.getProject().getHdbManager().equals(manager)) {
        if (view != null) {
            view.displayError("You can only manage registrations for your own projects");
        }
        return false;
    }
    
    // Check if registration is pending
    if (registration.getStatus() != RegistrationStatus.PENDING) {
        if (view != null) {
            view.displayError("Only pending registrations can be approved");
        }
        return false;
    }
    
    // Check if project still has available slots
    BTOProject project = registration.getProject();
    if (project.getAvailableHDBOfficerSlots() <= 0) {
        if (view != null) {
            view.displayError("No available officer slots for this project");
        }
        return false;
    }
    
    // Update registration status
    try {
        registration.setStatus(RegistrationStatus.APPROVED);
        
        // Assign the project to the officer
        HDBOfficer officer = (HDBOfficer) registration.getOfficer();
        officer.setHandlingProject(project);
        
        // Decrement available slots
        project.setAvailableHDBOfficerSlots(project.getAvailableHDBOfficerSlots() - 1);
        
        if (view != null) {
            view.displaySuccess("Registration approved successfully");
        }
        
        return true;
    } catch (Exception e) {
        if (view != null) {
            view.displayError("Error approving registration: " + e.getMessage());
        }
        return false;
    }
}

/**
 * Gets registrations by status for a specific project
 * 
 * @param project Project to filter by
 * @param status Status to filter by
 * @return List of registrations with the specified project and status
 */
public List<Registration> getRegistrationsByStatus(BTOProject project, RegistrationStatus status) {
    // Validate inputs
    if (project == null || status == null) {
        if (view != null) {
            view.displayError("Project and status cannot be null");
        }
        return new ArrayList<>();
    }
    
    // Get current user
    User currentUser = userService.getCurrentUser();
    if (!(currentUser instanceof HDBManager)) {
        if (view != null) {
            view.displayError("Only HDB Managers can view registration details");
        }
        return new ArrayList<>();
    }
    
    HDBManager manager = (HDBManager) currentUser;
    
    // Check if manager is in charge of this project
    if (!project.getHdbManager().equals(manager)) {
        if (view != null) {
            view.displayError("You can only view registrations for your own projects");
        }
        return new ArrayList<>();
    }
    
    // Filter registrations by status
    try {
        return project.getRegistrations().stream()
            .filter(reg -> reg.getStatus() == status)
            .collect(java.util.stream.Collectors.toList());
    } catch (Exception e) {
        if (view != null) {
            view.displayError("Error retrieving registrations: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}


    
}