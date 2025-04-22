package models;

import enumeration.MaritalStatus;
import enumeration.UserType;

/**
 * Represents an applicant in the BTO system.
 * 
 * This class extends the User class to represent individuals who can apply 
 * for BTO flats. An applicant has all the attributes of a user but 
 * is specifically designated with the APPLICANT user type.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class Applicant extends User {
    /**
     * Constructs a new Applicant with the specified personal information.
     * 
     * @param name The name of the applicant
     * @param nric The National Registration Identity Card number of the applicant
     * @param age The age of the applicant
     * @param maritalStatus The marital status of the applicant
     * @param password The password for the applicant's account
     */
    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password, UserType.APPLICANT);
    }
}
