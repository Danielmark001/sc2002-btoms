package models;

import enumeration.RegistrationStatus;

/**
 * Represents a registration request from an HDB Officer to join a BTO project.
 * 
 * This class models the relationship between an HDB Officer and a BTO project
 * during the registration process. It tracks the status of the registration request
 * (pending, approved, or rejected) and stores information about both the officer
 * and the project they wish to join.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class HDBOfficerRegistration {
    private String registrationId;
    private final HDBOfficer hdbOfficer;
    private final BTOProject project;
    private RegistrationStatus status;

    /**
     * Constructs a new HDB Officer Registration with all attributes specified.
     * 
     * @param registrationId The unique identifier for this registration
     * @param hdbOfficer The HDB Officer making the registration request
     * @param project The BTO project the officer is requesting to join
     * @param status The current status of the registration request
     */
    public HDBOfficerRegistration(String registrationId, HDBOfficer hdbOfficer, BTOProject project, RegistrationStatus status) {
        this.registrationId = registrationId;
        this.hdbOfficer = hdbOfficer;
        this.project = project;
        this.status = status;
    }

    /**
     * Constructs a new pending HDB Officer Registration.
     * 
     * @param hdbOfficer The HDB Officer making the registration request
     * @param project The BTO project the officer is requesting to join
     */
    public HDBOfficerRegistration(HDBOfficer hdbOfficer, BTOProject project) {
        this(generateRegistrationId(), hdbOfficer, project, RegistrationStatus.PENDING);
    }

    /**
     * Generates a unique registration ID combining the current date and a UUID.
     * 
     * @return A unique registration identifier
     */
    private static String generateRegistrationId() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "REG-" + date + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Gets the registration ID.
     * 
     * @return The registration's unique identifier
     */
    public String getRegistrationId() {
        return registrationId;
    }

    /**
     * Gets the HDB Officer making the registration request.
     * 
     * @return The HDB Officer
     */
    public HDBOfficer getHDBOfficer() {
        return hdbOfficer;
    }

    /**
     * Gets the BTO project the officer is requesting to join.
     * 
     * @return The BTO project
     */
    public BTOProject getProject() {
        return project;
    }

    /**
     * Gets the current status of the registration request.
     * 
     * @return The registration status
     */
    public RegistrationStatus getStatus() {
        return status;
    }

    /**
     * Updates the status of the registration request.
     * 
     * @param status The new registration status to set
     */
    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }
} 