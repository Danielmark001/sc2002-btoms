package controllers;

import models.BTOProject;
import models.HDBManager;
import models.HDBOfficer;
import models.User;
import enumeration.UserStatus;
import services.ProjectService;
import services.UserService;
import view.OfficerProjectView;
import java.util.List;
import stores.DataStore;
import models.Registration;
import enumeration.RegistrationStatus;
import enumeration.UserType;
import enumeration.MaritalStatus;


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
    
    public boolean registerForProject(BTOProject project) {
        return true;
    }

    public boolean rejectRegistration(Registration registration) {
        return true;
    };

    public boolean approveRegistration(Registration registration) {
        return true;
    };

    public List<Registration> getRegistrationsByStatus(BTOProject project, RegistrationStatus status) {
        return null;
    }


    
}