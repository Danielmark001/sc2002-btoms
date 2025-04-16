package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Model representing an enquiry from an applicant about a BTO project
 */
public class Enquiry {
    private String enquiryId;
    private final Applicant applicant;
    private final BTOProject project;
    private String message;
    private String reply;
    private final LocalDateTime createdAt;
    private LocalDateTime repliedAt;

    /**
     * Constructor for creating a new enquiry
     * @param applicant The applicant making the enquiry
     * @param project The project the enquiry is about
     * @param message The enquiry message
     */
    public Enquiry(Applicant applicant, BTOProject project, String message) {
        this.enquiryId = generateEnquiryId();
        this.applicant = applicant;
        this.project = project;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.reply = null;
        this.repliedAt = null;
    }

    /**
     * Constructor for creating an enquiry with all fields
     * @param enquiryId The unique ID of the enquiry
     * @param applicant The applicant making the enquiry
     * @param project The project the enquiry is about
     * @param message The enquiry message
     * @param reply The reply to the enquiry
     * @param createdAt The date and time the enquiry was created
     * @param repliedAt The date and time the enquiry was replied to
     */
    public Enquiry(String enquiryId, Applicant applicant, BTOProject project, String message, 
                  String reply, LocalDateTime createdAt, LocalDateTime repliedAt) {
        this.enquiryId = enquiryId;
        this.applicant = applicant;
        this.project = project;
        this.message = message;
        this.reply = reply;
        this.createdAt = createdAt;
        this.repliedAt = repliedAt;
    }

    /**
     * Generates a unique ID for the enquiry
     * @return A unique ID
     */
    private static String generateEnquiryId() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "ENQ-" + date + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Gets the enquiry ID
     * @return The enquiry ID
     */
    public String getEnquiryId() {
        return enquiryId;
    }

    /**
     * Gets the applicant who made the enquiry
     * @return The applicant
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Gets the project the enquiry is about
     * @return The project
     */
    public BTOProject getProject() {
        return project;
    }

    /**
     * Gets the enquiry message
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the enquiry message
     * @param message The new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the reply to the enquiry
     * @return The reply
     */
    public String getReply() {
        return reply;
    }

    /**
     * Sets the reply to the enquiry
     * @param reply The reply
     */
    public void setReply(String reply) {
        this.reply = reply;
        this.repliedAt = LocalDateTime.now();
    }

    /**
     * Gets the date and time the enquiry was created
     * @return The creation date and time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Gets the date and time the enquiry was replied to
     * @return The reply date and time
     */
    public LocalDateTime getRepliedAt() {
        return repliedAt;
    }

    /**
     * Checks if the enquiry has been replied to
     * @return true if the enquiry has been replied to, false otherwise
     */
    public boolean hasReply() {
        return reply != null && !reply.isEmpty();
    }
} 