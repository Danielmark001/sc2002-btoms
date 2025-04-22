package services;

import models.Enquiry;
import models.BTOProject;
import models.Applicant;
import stores.DataStore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling enquiry-related operations in the BTO system.
 * 
 * This class provides methods for creating, retrieving, updating, and deleting enquiries
 * between applicants and BTO projects. It serves as the business logic layer for
 * all enquiry-related functionality in the application.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class EnquiryService {

    /**
     * Gets all enquiries in the system
     * @return List of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        return DataStore.getEnquiriesData().values().stream()
            .collect(Collectors.toList());
    }

    /**
     * Gets all enquiries for a specific project
     * @param project The project to get enquiries for
     * @return List of enquiries for the project
     */
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        return DataStore.getEnquiriesData().values().stream()
            .filter(enquiry -> enquiry.getProject().equals(project))
            .collect(Collectors.toList());
    }

    /**
     * Gets all enquiries for a specific applicant
     * @param applicant The applicant to get enquiries for
     * @return List of enquiries for the applicant
     */
    public List<Enquiry> getEnquiriesByApplicant(Applicant applicant) {
        return DataStore.getEnquiriesData().values().stream()
                .filter(enquiry -> enquiry.getApplicant().equals(applicant))
                .collect(Collectors.toList());
    }
    
    /**
     * Edits an existing enquiry's message
     * 
     * @param enquiry The enquiry to be edited
     * @param newMessage The new message content
     * @return true if the edit was successful, false if the enquiry already has a reply
     */
    public boolean editEnquiry(Enquiry enquiry, String newMessage) {
        // Cannot edit if already replied to
        if (enquiry.hasReply()) {
            return false;
        }
        
        // Remove commas from the message to prevent CSV parsing issues
        newMessage = newMessage.replace(",", " ");
        
        enquiry.setMessage(newMessage);
        DataStore.saveData();
        
        return true;
    }

    /**
     * Deletes an enquiry from the system
     * 
     * @param enquiry The enquiry to delete
     * @return true if deletion was successful, false if the enquiry has a reply or couldn't be found
     */
    public boolean deleteEnquiry(Enquiry enquiry) {
        // Cannot delete if already replied to
        if (enquiry.hasReply()) {
            return false;
        }
        
        if (DataStore.getEnquiriesData().remove(enquiry.getEnquiryId()) != null) {
            DataStore.saveData();
            return true;
        }
        return false;
    }

    /**
     * Gets all pending (unreplied) enquiries
     * @return List of pending enquiries
     */
    public List<Enquiry> getPendingEnquiries() {
        return DataStore.getEnquiriesData().values().stream()
            .filter(enquiry -> !enquiry.hasReply())
            .collect(Collectors.toList());
    }

    /**
     * Creates a new enquiry
     * @param applicant The applicant making the enquiry
     * @param project The project being enquired about
     * @param message The enquiry message
     * @return The created enquiry
     */
    public Enquiry createEnquiry(Applicant applicant, BTOProject project, String message) {
        String enquiryId = "ENQ" + System.currentTimeMillis();
        Enquiry enquiry = new Enquiry(
            enquiryId,
            applicant,
            project,
            message,
            null,
            LocalDateTime.now(),
            null
        );
        
        DataStore.getEnquiriesData().put(enquiryId, enquiry);
        DataStore.saveData();
        
        return enquiry;
    }

    /**
     * Replies to an enquiry
     * @param enquiry The enquiry to reply to
     * @param reply The reply message
     * @return true if reply was successful, false otherwise
     */
    public boolean replyToEnquiry(Enquiry enquiry, String reply) {
        if (enquiry.hasReply()) {
            return false;
        }
        
        // Remove commas from the reply to prevent CSV parsing issues
        reply = reply.replace(",", " ");
        
        enquiry.setReply(reply);
        DataStore.saveData();
        
        return true;
    }
} 