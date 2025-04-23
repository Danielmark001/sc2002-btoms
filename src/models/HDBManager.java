package models;

import enumeration.MaritalStatus;
import enumeration.UserType;
import java.time.LocalDate;

/**
 * Represents an HDB Manager in the BTO system.
 * 
 * This class extends the User class to represent HDB Manager users who are
 * responsible for managing BTO projects, approving applications, and overseeing
 * the BTO housing process. HDB Managers have administrative privileges
 * and are designated with the HDB_MANAGER user type.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class HDBManager extends User {
    
    // The current application being handled by this manager within the application period
    private BTOApplication currentApplication;
    
    /**
     * Constructs a new HDB Manager with the specified personal information.
     * 
     * @param name The name of the HDB Manager
     * @param nric The National Registration Identity Card number of the HDB Manager
     * @param age The age of the HDB Manager
     * @param maritalStatus The marital status of the HDB Manager
     * @param password The password for the HDB Manager's account
     */
    public HDBManager(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password, UserType.HDB_MANAGER);
        this.currentApplication = null;
    }
    
    /**
     * Gets the current application being handled by this HDB Manager.
     * 
     * @return The current BTO application, or null if none
     */
    public BTOApplication getCurrentApplication() {
        return currentApplication;
    }
    
    /**
     * Sets the current application being handled by this HDB Manager.
     * 
     * @param application The BTO application to set as current
     */
    public void setCurrentApplication(BTOApplication application) {
        this.currentApplication = application;
    }
    
    /**
     * Clears the current application, indicating the manager is no longer handling it.
     */
    public void clearCurrentApplication() {
        this.currentApplication = null;
    }
    
    /**
     * Checks if this HDB Manager is currently handling an application within an application period.
     * 
     * @return true if the manager is handling an application, false otherwise
     */
    public boolean isHandlingApplication() {
        return currentApplication != null;
    }
    
    /**
     * Checks if this HDB Manager can handle a new application within the application period.
     * HDB Managers are restricted to handling only one application at a time within
     * the application period.
     * 
     * @param project The BTO project to check
     * @return true if the manager can handle a new application, false otherwise
     */
    public boolean canHandleNewApplication(BTOProject project) {
        // If not handling any application, can handle a new one
        if (currentApplication == null) {
            return true;
        }
        
        // If handling an application, check if the project is in its application period
        LocalDate today = LocalDate.now();
        BTOProject currentProject = currentApplication.getProject();
        boolean currentProjectInApplicationPeriod = today.isAfter(currentProject.getApplicationOpeningDate()) && 
                                                   today.isBefore(currentProject.getApplicationClosingDate());
        
        boolean newProjectInApplicationPeriod = today.isAfter(project.getApplicationOpeningDate()) && 
                                               today.isBefore(project.getApplicationClosingDate());
        
        // Can handle a new application if both projects are not in application period
        // or if they are different projects and the current one is not in application period
        return !currentProjectInApplicationPeriod || !newProjectInApplicationPeriod;
    }
}
