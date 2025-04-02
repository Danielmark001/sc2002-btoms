// File: bto_management_system/model/manager/EnquiryManager.java
package bto_management_system.model.manager;

import bto_management_system.model.entity.BTOProject;
import bto_management_system.model.entity.Enquiry;
import bto_management_system.model.entity.HDBManager;
import bto_management_system.model.entity.HDBOfficer;
import bto_management_system.model.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages enquiry-related operations
 */
public class EnquiryManager {
    private static EnquiryManager instance;
    
    private EnquiryManager() {
    }
    
    /**
     * Gets the singleton instance
     * 
     * @return EnquiryManager instance
     */
    public static EnquiryManager getInstance() {
        if (instance == null) {
            instance = new EnquiryManager();
        }
        return instance;
    }
    
    /**
     * Creates a new enquiry
     * 
     * @param creator User creating the enquiry
     * @param project Project the enquiry is about
     * @param content Content of the enquiry
     * @return Created enquiry if successful, null otherwise
     */
    public Enquiry createEnquiry(User creator, BTOProject project, String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        
        Enquiry enquiry = creator.createEnquiry(content, project);
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        UserManager.getInstance().saveUsers();
        
        return enquiry;
    }
    
    /**
     * Edits an existing enquiry
     * 
     * @param enquiry Enquiry to edit
     * @param user User editing the enquiry
     * @param newContent New content for the enquiry
     * @return true if edit succeeds
     */
    public boolean editEnquiry(Enquiry enquiry, User user, String newContent) {
        if (newContent == null || newContent.trim().isEmpty()) {
            return false;
        }
        
        if (!enquiry.getCreator().equals(user)) {
            return false;
        }
        
        boolean result = user.editEnquiry(enquiry, newContent);
        
        if (result) {
            // Save changes
            ProjectManager.getInstance().saveProjects();
            UserManager.getInstance().saveUsers();
        }
        
        return result;
    }
    
    /**
     * Deletes an enquiry
     * 
     * @param enquiry Enquiry to delete
     * @param user User deleting the enquiry
     * @return true if deletion succeeds
     */
    public boolean deleteEnquiry(Enquiry enquiry, User user) {
        if (!enquiry.getCreator().equals(user)) {
            return false;
        }
        
        boolean result = user.deleteEnquiry(enquiry);
        
        if (result) {
            // Save changes
            ProjectManager.getInstance().saveProjects();
            UserManager.getInstance().saveUsers();
        }
        
        return result;
    }
    
    /**
     * Replies to an enquiry
     * 
     * @param enquiry Enquiry to reply to
     * @param replier User replying to the enquiry
     * @param replyContent Content of the reply
     * @return true if reply succeeds
     */
    public boolean replyToEnquiry(Enquiry enquiry, User replier, String replyContent) {
        if (replyContent == null || replyContent.trim().isEmpty()) {
            return false;
        }
        
        if (!(replier instanceof HDBOfficer || replier instanceof HDBManager)) {
            return false;
        }
        
        // Check if officer is handling the project
        if (replier instanceof HDBOfficer && 
                !((HDBOfficer) replier).getHandlingProject().equals(enquiry.getProject())) {
            return false;
        }
        
        // Add the reply
        enquiry.setReply(replyContent, replier);
        
        // Save changes
        ProjectManager.getInstance().saveProjects();
        
        return true;
    }
    
    /**
     * Gets all enquiries for a specific project
     * 
     * @param project Project to get enquiries for
     * @return List of enquiries
     */
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        return project.getEnquiries();
    }
    
    /**
     * Gets all enquiries created by a specific user
     * 
     * @param user User to get enquiries for
     * @return List of enquiries
     */
    public List<Enquiry> getEnquiriesByUser(User user) {
        return user.getEnquiries();
    }
    
    /**
     * Gets all enquiries for all projects
     * 
     * @return List of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        List<Enquiry> allEnquiries = new ArrayList<>();
        
        for (BTOProject project : ProjectManager.getInstance().getAllProjects()) {
            allEnquiries.addAll(project.getEnquiries());
        }
        
        return allEnquiries;
    }
    
    /**
     * Gets all unanswered enquiries
     * 
     * @return List of unanswered enquiries
     */
    public List<Enquiry> getUnansweredEnquiries() {
        return getAllEnquiries().stream()
                .filter(e -> e.getReply() == null)
                .collect(Collectors.toList());
    }
}