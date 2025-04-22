package models;

import enumeration.MaritalStatus;
import enumeration.UserType;

/**
 * Base class representing a user in the BTO system.
 * 
 * This abstract base class models the common attributes and behaviors for all user types
 * in the system, including applicants, HDB officers, and HDB managers. It stores personal
 * information, authentication details, and user type designation.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class User {
    private String name;
    private String nric;
    private int age;
    private MaritalStatus maritalStatus;
    private String password;
    private UserType userType;

    /**
     * Constructs a new User with the specified attributes.
     * 
     * @param name The user's full name
     * @param nric The user's National Registration Identity Card number (unique identifier)
     * @param age The user's age
     * @param maritalStatus The user's marital status (SINGLE or MARRIED)
     * @param password The user's account password
     * @param userType The type of user (APPLICANT, HDB_OFFICER, or HDB_MANAGER)
     */
    public User(String name, String nric, int age, MaritalStatus maritalStatus, String password, UserType userType) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = password;
        this.userType = userType;
    }

    /**
     * Gets the user's name.
     * 
     * @return The user's full name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the user's NRIC.
     * 
     * @return The user's National Registration Identity Card number
     */
    public String getNric() {
        return nric;
    }

    /**
     * Gets the user's age.
     * 
     * @return The user's age
     */
    public int getAge() {
        return age;
    }

    /**
     * Gets the user's marital status.
     * 
     * @return The user's marital status (SINGLE or MARRIED)
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Gets the user's password.
     * 
     * @return The user's account password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user's type.
     * 
     * @return The type of user (APPLICANT, HDB_OFFICER, or HDB_MANAGER)
     */
    public UserType getUserType() {
        return userType;
    }

    /**
     * Sets the user's name.
     * 
     * @param name The new name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the user's NRIC.
     * 
     * @param nric The new NRIC to set
     */
    public void setNric(String nric) {
        this.nric = nric;
    }

    /**
     * Sets the user's age.
     * 
     * @param age The new age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets the user's marital status.
     * 
     * @param maritalStatus The new marital status to set
     */
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * Changes the user's password.
     * 
     * @param oldPassword The current password (for verification)
     * @param newPassword The new password to set
     * @return true if the password was successfully changed, false if the old password is incorrect
     */
    public boolean setPassword(String oldPassword, String newPassword) {
        if (oldPassword.equals(password)) {
            password = newPassword;
            return true;
        }
        return false;
    }

    /**
     * Sets the user's type.
     * 
     * @param userType The new user type to set
     */
    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    /**
     * Returns a string representation of the user.
     * 
     * @return A string containing all user attributes
     */
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", nric='" + nric + '\'' +
                ", age=" + age +
                ", maritalStatus=" + maritalStatus +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                '}';
    }
}   
