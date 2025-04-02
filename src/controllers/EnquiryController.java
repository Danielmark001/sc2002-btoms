// File: bto_management_system/controller/EnquiryController.java
package bto_management_system.controller;

import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.Enquiry;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.User;
import bto_management_system.model.manager.EnquiryManager;
import bto_management_system.model.manager.UserManager;
import bto_management_system.view.EnquiryView;

import java.util.List;

/**
 * Controller for handling enquiry-related operations
 */
public class EnquiryController {
    private EnquiryView enquiryView;
    private EnquiryManager enquiryManager;
    private UserManager userManager;
    
    /**
     * Constructor for EnquiryController
     * 
     * @param enquiryView View for enquiry operations
     */
    public EnquiryController(EnquiryView enquiryView) {
        this.enquiryView = enquiryView;
        this.enquiryManager = EnquiryManager.getInstance();
        this.userManager = UserManager.getInstance();
    }
    
    /**
     * Creates a new enquiry
     * 
     * @param project Project the enquiry is about
     * @param content Content of the enquiry
     * @return true if creation succeeds
     */
    public boolean createEnquiry(BTOProject project, String content) {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        Enquiry enquiry = enquiryManager.createEnquiry(currentUser, project, content);
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
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return enquiryManager.editEnquiry(enquiry, currentUser, newContent);
    }
    
    /**
     * Deletes an enquiry
     * 
     * @param enquiry Enquiry to delete
     * @return true if deletion succeeds
     */
    public boolean deleteEnquiry(Enquiry enquiry) {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return enquiryManager.deleteEnquiry(enquiry, currentUser);
    }
    
    /**
     * Replies to an enquiry
     * 
     * @param enquiry Enquiry to reply to
     * @param replyContent Content of the reply
     * @return true if reply succeeds
     */
    public boolean replyToEnquiry(Enquiry enquiry, String replyContent) {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return enquiryManager.replyToEnquiry(enquiry, currentUser, replyContent);
    }
    
    /**
     * Gets enquiries for a specific project
     * 
     * @param project Project to get enquiries for
     * @return List of enquiries
     */
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        return enquiryManager.getEnquiriesByProject(project);
    }
    
    /**
     * Gets enquiries created by the current user
     * 
     * @return List of enquiries
     */
    public List<Enquiry> getCurrentUserEnquiries() {
        User currentUser = userManager.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        
        return enquiryManager.getEnquiriesByUser(currentUser);
    }
    
    /**
     * Gets all enquiries (for HDB Manager)
     * 
     * @return List of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        User currentUser = userManager.getCurrentUser();
        if (!(currentUser instanceof HDBManager)) {
            return List.of();
        }
        
        return enquiryManager.getAllEnquiries();
    }
    
    /**
     * Gets all unanswered enquiries
     * 
     * @return List of unanswered enquiries
     */
    public List<Enquiry> getUnansweredEnquiries() {
        return enquiryManager.getUnansweredEnquiries();
    }
}

