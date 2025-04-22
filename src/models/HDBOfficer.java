package models;

import enumeration.MaritalStatus;
import enumeration.UserType;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HDB Officer in the BTO system.
 * 
 * This class extends the User class to represent HDB Officers who assist with
 * BTO project operations, helping applicants with flat bookings and handling
 * project-specific tasks. HDB Officers can be assigned to multiple BTO projects
 * and are designated with the HDB_OFFICER user type.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class HDBOfficer extends User {
    private List<BTOProject> handledProjects;

    /**
     * Constructs a new HDB Officer with the specified personal information.
     * 
     * @param name The name of the HDB Officer
     * @param nric The National Registration Identity Card number of the HDB Officer
     * @param age The age of the HDB Officer
     * @param maritalStatus The marital status of the HDB Officer
     * @param password The password for the HDB Officer's account
     */
    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password, UserType.HDB_OFFICER);
        this.handledProjects = new ArrayList<>();
    }

    /**
     * Gets the list of BTO projects handled by this HDB Officer.
     * 
     * @return A list of BTO projects assigned to this officer
     */
    public List<BTOProject> getHandledProjects() {
        return handledProjects;
    }

    /**
     * Adds a BTO project to the list of projects handled by this HDB Officer.
     * 
     * @param project The BTO project to add to the handled projects list
     */
    public void addHandledProject(BTOProject project) {
        handledProjects.add(project);
    }

    /**
     * Removes a BTO project from the list of projects handled by this HDB Officer.
     * 
     * @param project The BTO project to remove from the handled projects list
     */
    public void removeHandledProject(BTOProject project) {
        handledProjects.remove(project);
    }
}
