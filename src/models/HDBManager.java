package models;

import enumeration.MaritalStatus;
import enumeration.UserType;

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
    }
}
