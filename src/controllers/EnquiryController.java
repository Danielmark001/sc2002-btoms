package controllers;

import models.BTOProject;
import models.Enquiry;
import models.HDBManager;
import models.HDBOfficer;
import models.User;
import services.EnquiryService;
import services.ProjectService;
import services.UserService;
import view.EnquiryView;
import util.InputValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for handling enquiry-related operations
 */
public class EnquiryController {
    private EnquiryView enquiryView;
    private EnquiryService enquiryService;
    private UserService userService;
    private ProjectService projectService;

    /**
     * Constructor for EnquiryController
     * 
     * @param enquiryView View for enquiry operations
     */
    public EnquiryController(EnquiryView enquiryView) {
        this.enquiryView = enquiryView;
        this.enquiryService = EnquiryService.getInstance();
        this.userService = UserService.getInstance();
        this.projectService = ProjectService.getInstance();
    }
    
    /**
     * Default constructor
     */
    public EnquiryController() {
        this.enquiryService = EnquiryService.getInstance();
        this.userService = UserService.getInstance();
        this.projectService = ProjectService.getInstance();
    }

    /**
     * Creates a new enquiry
     * 
     * @param project Project the enquiry is about
     * @param content Content of the enquiry  
     * @return true if creation succeeds
     */
    public boolean createEnquiry(BTOProject project, String content) {
        try {
            // Validate project
            if (project == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("Project cannot be null");
                }
                return false;
            }
            
            // Validate content
            if (content == null || content.trim().isEmpty()) {
                if (enquiryView != null) {
                    enquiryView.displayError("Enquiry content cannot be empty");
                }
                return false;
            }
            
            // Sanitize input
            String sanitizedContent = InputValidator.sanitizeInput(content);
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("You must be logged in to create an enquiry");
                }
                return false;
            }
            
            // Create enquiry
            Enquiry enquiry = enquiryService.createEnquiry(currentUser, project, sanitizedContent);
            
            if (enquiryView != null) {
                enquiryView.displaySuccess("Enquiry created successfully");
            }
            
            return true;
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error creating enquiry: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Edits an existing enquiry
     * 
     * @param enquiry Enquiry to edit
     * @param newContent New content for the enquiry
     * @return true if edit succeeds
     */
    public boolean editEnquiry(Enquiry enquiry, String newContent) {
        try {
            // Validate enquiry
            if (enquiry == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("Enquiry cannot be null");
                }
                return false;
            }
            
            // Validate content
            if (newContent == null || newContent.trim().isEmpty()) {
                if (enquiryView != null) {
                    enquiryView.displayError("Enquiry content cannot be empty");
                }
                return false;
            }
            
            // Sanitize input
            String sanitizedContent = InputValidator.sanitizeInput(newContent);
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("You must be logged in to edit an enquiry");
                }
                return false;
            }
            
            // Edit enquiry
            boolean success = enquiryService.editEnquiry(enquiry, currentUser, sanitizedContent);
            
            if (success) {
                if (enquiryView != null) {
                    enquiryView.displaySuccess("Enquiry updated successfully");
                }
            } else {
                if (enquiryView != null) {
                    enquiryView.displayError("Failed to update enquiry");
                }
            }
            
            return success;
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error editing enquiry: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Deletes an enquiry
     * 
     * @param enquiry Enquiry to delete
     * @return true if deletion succeeds  
     */
    public boolean deleteEnquiry(Enquiry enquiry) {
        try {
            // Validate enquiry
            if (enquiry == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("Enquiry cannot be null");
                }
                return false;
            }
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("You must be logged in to delete an enquiry");
                }
                return false;
            }
            
            // Delete enquiry
            boolean success = enquiryService.deleteEnquiry(enquiry, currentUser);
            
            if (success) {
                if (enquiryView != null) {
                    enquiryView.displaySuccess("Enquiry deleted successfully");
                }
            } else {
                if (enquiryView != null) {
                    enquiryView.displayError("Failed to delete enquiry");
                }
            }
            
            return success;
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error deleting enquiry: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Replies to an enquiry
     * 
     * @param enquiry Enquiry to reply to
     * @param replyContent Content of the reply
     * @return true if reply succeeds
     */
    public boolean replyToEnquiry(Enquiry enquiry, String replyContent) {  
        try {
            // Validate enquiry
            if (enquiry == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("Enquiry cannot be null");
                }
                return false;
            }
            
            // Validate content
            if (replyContent == null || replyContent.trim().isEmpty()) {
                if (enquiryView != null) {
                    enquiryView.displayError("Reply content cannot be empty");
                }
                return false;
            }
            
            // Sanitize input
            String sanitizedContent = InputValidator.sanitizeInput(replyContent);
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("You must be logged in to reply to an enquiry");
                }
                return false;
            }
            
            // Check if user is authorized to reply
            if (!(currentUser instanceof HDBOfficer || currentUser instanceof HDBManager)) {
                if (enquiryView != null) {
                    enquiryView.displayError("Only HDB Officers and Managers can reply to enquiries");
                }
                return false;
            }
            
            // If user is an officer, check if they are assigned to the project
            if (currentUser instanceof HDBOfficer) {
                HDBOfficer officer = (HDBOfficer) currentUser;
                if (officer.getHandlingProject() == null || 
                    !officer.getHandlingProject().equals(enquiry.getProject())) {
                    if (enquiryView != null) {
                        enquiryView.displayError("You can only reply to enquiries for projects you are handling");
                    }
                    return false;
                }
            }
            
            // Reply to enquiry
            boolean success = enquiryService.replyToEnquiry(enquiry, currentUser, sanitizedContent);
            
            if (success) {
                if (enquiryView != null) {
                    enquiryView.displaySuccess("Reply submitted successfully");
                }
            } else {
                if (enquiryView != null) {
                    enquiryView.displayError("Failed to submit reply");
                }
            }
            
            return success;
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error replying to enquiry: " + e.getMessage());
            }
            return false;
        }
    }
    
    /**
     * Gets enquiries for a specific project
     * 
     * @param project Project to get enquiries for
     * @return List of enquiries
     */
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        try {
            if (project == null) {
                return new ArrayList<>();
            }
            
            return enquiryService.getEnquiriesByProject(project);
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error retrieving enquiries: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets enquiries created by the current user
     * 
     * @return List of enquiries  
     */
    public List<Enquiry> getCurrentUserEnquiries() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return new ArrayList<>();
            }
            
            return enquiryService.getEnquiriesByUser(currentUser);
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error retrieving enquiries: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets all enquiries (for HDB Manager)
     * 
     * @return List of all enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        try {
            User currentUser = userService.getCurrentUser();
            if (!(currentUser instanceof HDBManager)) {
                if (enquiryView != null) {
                    enquiryView.displayError("Only HDB Managers can view all enquiries");
                }
                return new ArrayList<>();
            }
            
            return enquiryService.getAllEnquiries();
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error retrieving enquiries: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets all unanswered enquiries
     * 
     * @return List of unanswered enquiries
     */  
    public List<Enquiry> getUnansweredEnquiries() {
        try {
            User currentUser = userService.getCurrentUser();
            
            // For officers, only show unanswered enquiries for their projects
            if (currentUser instanceof HDBOfficer) {
                HDBOfficer officer = (HDBOfficer) currentUser;
                if (officer.getHandlingProject() == null) {
                    return new ArrayList<>();
                }
                
                return enquiryService.getEnquiriesByProject(officer.getHandlingProject()).stream()
                    .filter(e -> e.getStatus() == Enquiry.EnquiryStatus.SUBMITTED)
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // For managers, show all unanswered enquiries
            if (currentUser instanceof HDBManager) {
                return enquiryService.getUnansweredEnquiries();
            }
            
            // For other users, show nothing
            return new ArrayList<>();
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error retrieving unanswered enquiries: " + e.getMessage());
            }
            return new ArrayList<>();
        }
    }
    
    /**
     * Gets an enquiry by ID
     * 
     * @param enquiryId ID of the enquiry
     * @return Enquiry if found, null otherwise
     */
    public Enquiry getEnquiryById(String enquiryId) {
        try {
            if (enquiryId == null || enquiryId.trim().isEmpty()) {
                return null;
            }
            
            // Get all enquiries
            List<Enquiry> allEnquiries = new ArrayList<>();
            User currentUser = userService.getCurrentUser();
            
            if (currentUser instanceof HDBManager) {
                // Managers can see all enquiries
                allEnquiries = enquiryService.getAllEnquiries();
            } else if (currentUser instanceof HDBOfficer) {
                // Officers can see enquiries for their projects
                HDBOfficer officer = (HDBOfficer) currentUser;
                if (officer.getHandlingProject() != null) {
                    allEnquiries = enquiryService.getEnquiriesByProject(officer.getHandlingProject());
                }
            } else {
                // Regular users can see their own enquiries
                allEnquiries = enquiryService.getEnquiriesByUser(currentUser);
            }
            
            // Find the enquiry with the given ID
            return allEnquiries.stream()
                .filter(e -> e.getId().equals(enquiryId))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error retrieving enquiry: " + e.getMessage());
            }
            return null;
        }
    }
    
    /**
     * Closes an enquiry
     * 
     * @param enquiry Enquiry to close
     * @return true if closure succeeds
     */
    public boolean closeEnquiry(Enquiry enquiry) {
        try {
            // Validate enquiry
            if (enquiry == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("Enquiry cannot be null");
                }
                return false;
            }
            
            // Get current user
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                if (enquiryView != null) {
                    enquiryView.displayError("You must be logged in to close an enquiry");
                }
                return false;
            }
            
            // Check if user is authorized to close
            boolean isAuthorized = false;
            
            // The submitter can close their own enquiry
            if (enquiry.getSubmitter().equals(currentUser)) {
                isAuthorized = true;
            }
            
            // HDB Officers handling the project can close enquiries
            if (currentUser instanceof HDBOfficer) {
                HDBOfficer officer = (HDBOfficer) currentUser;
                if (officer.getHandlingProject() != null && 
                    officer.getHandlingProject().equals(enquiry.getProject())) {
                    isAuthorized = true;
                }
            }
            
            // HDB Managers can close any enquiry
            if (currentUser instanceof HDBManager) {
                isAuthorized = true;
            }
            
            if (!isAuthorized) {
                if (enquiryView != null) {
                    enquiryView.displayError("You are not authorized to close this enquiry");
                }
                return false;
            }
            
            // Close the enquiry
            enquiry.closeEnquiry();
            
            // Save the enquiry
            // This would typically call a method to save the enquiry to the data store
            
            if (enquiryView != null) {
                enquiryView.displaySuccess("Enquiry closed successfully");
            }
            
            return true;
        } catch (Exception e) {
            if (enquiryView != null) {
                enquiryView.displayError("Error closing enquiry: " + e.getMessage());
            }
            return false;
        }
    }
}