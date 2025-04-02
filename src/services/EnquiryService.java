package services;

import models.*;
import enumeration.*;
import stores.AuthStore;
import stores.DataStore;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EnquiryService implements interfaces.IEnquiryService {
    private static EnquiryService instance;
    private DataStore dataStore;
    private AuthStore authStore;

    private EnquiryService() {
        this.dataStore = DataStore.getInstance();
        this.authStore = AuthStore.getInstance();
    }

    public static synchronized EnquiryService getInstance() {
        if (instance == null) {
            instance = new EnquiryService();
        }
        return instance;
    }

    @Override
    public Enquiry createEnquiry(User user, BTOProject project, String content) {
        // Validate input
        if (user == null || project == null || content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid enquiry details");
        }

        // Create new enquiry
        Enquiry enquiry = new Enquiry(user, project, content);
        
        // Validate that the user can create an enquiry for this project
        if (!canCreateEnquiry(user, project)) {
            throw new SecurityException("User is not authorized to create an enquiry for this project");
        }

        // Save enquiry
        saveEnquiry(enquiry);

        return enquiry;
    }

    @Override
    public boolean editEnquiry(Enquiry enquiry, User user, String newContent) {
        // Validate input
        if (enquiry == null || user == null || newContent == null || newContent.trim().isEmpty()) {
            return false;
        }

        // Check if user is the original submitter
        if (!enquiry.getSubmitter().equals(user)) {
            return false;
        }

        // Check if enquiry can be modified
        if (!enquiry.canModify()) {
            return false;
        }

        // Update enquiry content
        enquiry.setEnquiryText(newContent);
        
        // Save updated enquiry
        saveEnquiry(enquiry);

        return true;
    }

    @Override
    public boolean deleteEnquiry(Enquiry enquiry, User user) {
        // Validate input
        if (enquiry == null || user == null) {
            return false;
        }

        // Check if user is the original submitter
        if (!enquiry.getSubmitter().equals(user)) {
            return false;
        }

        // Check if enquiry can be deleted
        if (!enquiry.canModify()) {
            return false;
        }

        // Remove enquiry from data store
        return removeEnquiry(enquiry);
    }

    @Override
    public boolean replyToEnquiry(Enquiry enquiry, User user, String replyContent) {
        // Validate input
        if (enquiry == null || user == null || replyContent == null || replyContent.trim().isEmpty()) {
            return false;
        }

        // Check user authorization
        if (!isAuthorizedToReply(user, enquiry)) {
            return false;
        }

        // Add reply to enquiry
        enquiry.setResponse(replyContent);
        enquiry.setRespondent(user);

        // Save updated enquiry
        saveEnquiry(enquiry);

        return true;
    }

    @Override
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        // In a real implementation, this would come from DataStore
        return getAllEnquiries().stream()
            .filter(enquiry -> enquiry.getProject().equals(project))
            .collect(Collectors.toList());
    }

    @Override
    public List<Enquiry> getEnquiriesByUser(User user) {
        // In a real implementation, this would come from DataStore
        return getAllEnquiries().stream()
            .filter(enquiry -> enquiry.getSubmitter().equals(user))
            .collect(Collectors.toList());
    }

    @Override
    public List<Enquiry> getAllEnquiries() {
        // In a real implementation, this would retrieve from DataStore
        return new ArrayList<>();
    }

    @Override
    public List<Enquiry> getUnansweredEnquiries() {
        return getAllEnquiries().stream()
            .filter(enquiry -> enquiry.getStatus() == Enquiry.EnquiryStatus.SUBMITTED)
            .collect(Collectors.toList());
    }

    // Helper methods
    private boolean canCreateEnquiry(User user, BTOProject project) {
        // Logic to determine if user can create an enquiry
        // For example, user must be able to view the project
        return project.isVisibility();
    }

    private boolean isAuthorizedToReply(User user, Enquiry enquiry) {
        // Only HDB Officers or Managers can reply
        if (!(user instanceof HDBOfficer || user instanceof HDBManager)) {
            return false;
        }

        // If user is an HDB Officer, they must be handling the project

        // HDB Manager can reply to enquiries for any project
        return true;
    }

    private void saveEnquiry(Enquiry enquiry) {
        // In a real implementation, this would interact with DataStore
        DataStore.saveData();
    }

    private boolean removeEnquiry(Enquiry enquiry) {
        // In a real implementation, this would remove from DataStore
        DataStore.saveData();
        return true;
    }
}