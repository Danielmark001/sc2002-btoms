// File: bto_management_system/controller/EnquiryController.java
package controllers;

import models.BTOProject;
import models.Enquiry;
import models.HDBManager;
import models.User;
import services.EnquiryService;
import services.UserService;
import view.EnquiryView;

import java.util.List;

/**
 * Controller for handling enquiry-related operations
 */
public class EnquiryController {
    private EnquiryView enquiryView;
    private EnquiryService enquiryService;
    private UserService userService;
    
    /**
     * Constructor for EnquiryController
     * 
     * @param enquiryView View for enquiry operations
     */
    public EnquiryController(EnquiryView enquiryView) {
        this.enquiryView = enquiryView;
        this.enquiryService = new EnquiryService();
        this.userService = new UserService();
    }
    
    /**
     * Creates a new enquiry
     * 
     * @param project Project the enquiry is about
     * @param content Content of the enquiry  
     * @return true if creation succeeds
     */
    public boolean createEnquiry(BTOProject project, String content) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        Enquiry enquiry = enquiryService.createEnquiry(currentUser, project, content);
        return enquiry != null;
    }
    
    /**
     * Edits an existing enquiry
     * 
     * @param enquiry Enquiry to edit
     * @param newContent New content for the enquiry
     * @return true if edit succeeds
     */
    public boolean editEnquiry(Enquiry enquiry, String newContent) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return enquiryService.editEnquiry(enquiry, currentUser, newContent);  
    }
    
    /**
     * Deletes an enquiry
     * 
     * @param enquiry Enquiry to delete
     * @return true if deletion succeeds  
     */
    public boolean deleteEnquiry(Enquiry enquiry) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return enquiryService.deleteEnquiry(enquiry, currentUser);
    }
    
    /**
     * Replies to an enquiry
     * 
     * @param enquiry Enquiry to reply to
     * @param replyContent Content of the reply
     * @return true if reply succeeds
     */
    public boolean replyToEnquiry(Enquiry enquiry, String replyContent) {  
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return false;  
        }
        
        return enquiryService.replyToEnquiry(enquiry, currentUser, replyContent);
    }
    
    /**
     * Gets enquiries for a specific project
     * 
     * @param project Project to get enquiries for
     * @return List of enquiries
     */
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        return enquiryService.getEnquiriesByProject(project);
    }
    
    /**
     * Gets enquiries created by the current user
     * 
     * @return List of enquiries  
     */
    public List<Enquiry> getCurrentUserEnquiries() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        
        return enquiryService.getEnquiriesByUser(currentUser);
    }
    
    /**
     * Gets all enquiries (for HDB Manager)
     * 
     * @return List of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        User currentUser = userService.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return List.of();
        }
        
        return enquiryService.getAllEnquiries();
    }
    
    /**
     * Gets all unanswered enquiries
     * 
     * @return List of unanswered enquiries
     */  
    public List<Enquiry> getUnansweredEnquiries() {
        return enquiryService.getUnansweredEnquiries();  
    }
}