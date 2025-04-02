// File: bto_management_system/controller/LoginController.java
package bto_management_system.controller;

import bto_management_system.model.entity.Applicant;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.HDBOfficer;
import bto_management_system.model.entity.User;
import bto_management_system.model.enumeration.MaritalStatus;
import bto_management_system.model.manager.UserManager;
import bto_management_system.view.LoginView;

/**
 * Controller for handling login-related operations
 */
public class LoginController {
    private LoginView loginView;
    private UserManager userManager;
    
    /**
     * Constructor for LoginController
     * 
     * @param loginView View for login operations
     */
    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        this.userManager = UserManager.getInstance();
    }
    
    /**
     * Attempts to log in a user
     * 
     * @param nric NRIC of the user
     * @param password Password of the user
     * @return User object if login succeeds, null otherwise
     */
    public User login(String nric, String password) {
        return userManager.authenticateUser(nric, password);
    }
    
    /**
     * Determines user type and returns appropriate string
     * 
     * @param user User to check
     * @return String representing user type
     */
    public String getUserType(User user) {
        if (user instanceof Applicant) {
            return "APPLICANT";
        } else if (user instanceof HDBOfficer) {
            return "OFFICER";
        } else if (user instanceof HDBManager) {
            return "MANAGER";
        }
        return "UNKNOWN";
    }
    
    /**
     * Registers a new applicant
     * 
     * @param nric NRIC of the applicant
     * @param password Password for the account
     * @param age Age of the applicant
     * @param maritalStatus Marital status of the applicant
     * @return true if registration succeeds
     */
    public boolean registerApplicant(String nric, String password, int age, MaritalStatus maritalStatus) {
        return userManager.registerApplicant(nric, password, age, maritalStatus);
    }
    
    /**
     * Changes a user's password
     * 
     * @param oldPassword Old password to verify
     * @param newPassword New password to set
     * @return true if password change succeeds
     */
    public boolean changePassword(String oldPassword, String newPassword) {
        return userManager.changePassword(oldPassword, newPassword);
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        userManager.logout();
    }
}

// File: bto_management_system/controller/ProjectController.java
package bto_management_system.controller;

import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.User;
import bto_management_system.model.enumeration.FlatType;
import bto_management_system.model.manager.ProjectManager;
import bto_management_system.model.manager.UserManager;
import bto_management_system.view.ProjectView;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling project-related operations
 */
public class ProjectController {
    private ProjectView projectView;
    private ProjectManager projectManager;
    private UserManager userManager;
    
    /**
     * Constructor for ProjectController
     * 
     * @param projectView View for project operations
     */
    public ProjectController(ProjectView projectView) {
        this.projectView = projectView;
        this.