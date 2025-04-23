package models;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;

import java.util.UUID;

/**
 * Represents a BTO application in the system.
 * 
 * This class models an application submitted by a user for a specific BTO project.
 * It tracks the application status through its lifecycle, from initial submission
 * to eventual booking or rejection. Each application has a unique identifier and
 * maintains a state machine for valid status transitions.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class BTOApplication {
    // Unique ID for the application
    private String applicationId;

    private final User applicant;
    private final BTOProject project;
    private FlatType flatType;  // Now optional
    private BTOApplicationStatus status;
    private HDBManager handledBy; // The HDB Manager currently handling this application

    /**
     * Constructs a BTO application with all attributes specified.
     * 
     * @param applicationId Unique identifier for the application
     * @param applicant The user applying for the BTO flat
     * @param project The BTO project being applied for
     * @param flatType The type of flat requested (can be null initially)
     * @param status The current status of the application
     */
    public BTOApplication(String applicationId, User applicant, BTOProject project, FlatType flatType, BTOApplicationStatus status) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = status;
        this.handledBy = null;
    }

    /**
     * Constructs a BTO application with all attributes specified including handledBy.
     * 
     * @param applicationId Unique identifier for the application
     * @param applicant The user applying for the BTO flat
     * @param project The BTO project being applied for
     * @param flatType The type of flat requested (can be null initially)
     * @param status The current status of the application
     * @param handledBy The HDB Manager handling this application
     */
    public BTOApplication(String applicationId, User applicant, BTOProject project, FlatType flatType, BTOApplicationStatus status, HDBManager handledBy) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = status;
        this.handledBy = handledBy;
    }

    /**
     * Constructs a new pending BTO application without a specified flat type.
     * 
     * @param applicant The user applying for the BTO flat
     * @param project The BTO project being applied for
     */
    public BTOApplication(User applicant, BTOProject project) {
        this(generateApplicationId(), applicant, project, null, BTOApplicationStatus.PENDING);
    }

    /**
     * Constructs a new pending BTO application with a specified flat type.
     * 
     * @param applicant The user applying for the BTO flat
     * @param project The BTO project being applied for
     * @param flatType The type of flat requested
     */
    public BTOApplication(User applicant, BTOProject project, FlatType flatType) {
        this(generateApplicationId(), applicant, project, flatType, BTOApplicationStatus.PENDING);
    }

    /**
     * Generates a unique application ID combining the current date and a UUID.
     * 
     * @return A unique application identifier
     */
    private static String generateApplicationId() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return date + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Gets the application ID.
     * 
     * @return The application's unique identifier
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Gets the applicant who submitted this application.
     * 
     * @return The user who applied
     */
    public User getApplicant() {
        return applicant;
    }

    /**
     * Gets the BTO project this application is for.
     * 
     * @return The BTO project
     */
    public BTOProject getProject() {
        return project;
    }

    /**
     * Gets the flat type requested in this application.
     * 
     * @return The flat type (may be null if not yet selected)
     */
    public FlatType getFlatType() {
        return flatType;
    }

    /**
     * Gets the current status of this application.
     * 
     * @return The application status
     */
    public BTOApplicationStatus getStatus() {
        return status;
    }

    /**
     * Gets the HDB Manager currently handling this application.
     * 
     * @return The HDB Manager handling the application, or null if none
     */
    public HDBManager getHandledBy() {
        return handledBy;
    }

    /**
     * Sets the HDB Manager who is handling this application.
     * 
     * @param manager The HDB Manager to assign
     */
    public void setHandledBy(HDBManager manager) {
        this.handledBy = manager;
    }

    /**
     * Checks if this application is currently being handled by an HDB Manager.
     * 
     * @return true if an HDB Manager is handling this application, false otherwise
     */
    public boolean isBeingHandled() {
        return handledBy != null;
    }

    /**
     * Updates the application status following valid state transitions.
     * Implements a state machine to ensure only valid status changes are allowed.
     * 
     * @param status The new status to set
     * @throws IllegalArgumentException If the status transition is not allowed
     */
    public void setStatus(BTOApplicationStatus status) {
        switch (this.status) {
            case PENDING:
                if (!(status == BTOApplicationStatus.SUCCESSFUL ||
                      status == BTOApplicationStatus.UNSUCCESSFUL)) {
                    throw new IllegalArgumentException("Invalid status for pending application");
                }
                break;
            case SUCCESSFUL:
                if (!(status == BTOApplicationStatus.UNSUCCESSFUL ||
                      status == BTOApplicationStatus.BOOKED)) {
                    throw new IllegalArgumentException("Invalid status for successful application");
                }
                break;
            case UNSUCCESSFUL:
                throw new IllegalArgumentException("Cannot change status of an unsuccessful application");
            case BOOKED:
                throw new IllegalArgumentException("Cannot change status of a booked application");
        }
        this.status = status;
    }

    /**
     * Sets the flat type for this application.
     * This is typically done after an application is approved.
     * 
     * @param flatType The flat type to set
     */
    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }
}