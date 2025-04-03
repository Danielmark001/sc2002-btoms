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

        // Validate that the user can create an enquiry for this project
        if (!canCreateEnquiry(user, project)) {
            throw new SecurityException("User is not authorized to create an enquiry for this project");
        }

        // Create new enquiry
        Enquiry enquiry = new Enquiry(user, project, content);
        
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

        // Check if enquiry can be modified
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
        enquiry.setStatus(Enquiry.EnquiryStatus.RESPONDED);

        // Save updated enquiry
        saveEnquiry(enquiry);

        return true;
    }

    @Override
    public List<Enquiry> getEnquiriesByProject(BTOProject project) {
        if (project == null) {
            return new ArrayList<>();
        }
        
        // In a real implementation, this would come from DataStore
        return getAllEnquiries().stream()
            .filter(enquiry -> enquiry.getProject().equals(project))
            .collect(Collectors.toList());
    }

    @Override
    public List<Enquiry> getEnquiriesByUser(User user) {
        if (user == null) {
            return new ArrayList<>();
        }
        
        // In a real implementation, this would come from DataStore
        return getAllEnquiries().stream()
            .filter(enquiry -> enquiry.getSubmitter().equals(user))
            .collect(Collectors.toList());
    }

    @Override
    public List<Enquiry> getAllEnquiries() {
        // In a real implementation, this would retrieve from DataStore
        List<String[]> enquiryData = dataStore.getEnquiryData();
        List<Enquiry> enquiries = new ArrayList<>();
        
        for (String[] row : enquiryData) {
            if (row.length >= 6) {
                String id = row[0];
                String userNRIC = row[1];
                String projectName = row[2];
                String enquiryText = row[3];
                String response = row[4];
                String dateStr = row[5];
                
                // Get user and project from datastore
                User user = dataStore.getUserByNRIC(userNRIC);
                BTOProject project = getProjectByName(projectName);
                
                if (user != null && project != null) {
                    Enquiry enquiry = new Enquiry(user, project, enquiryText);
                    
                    // Set response if available
                    if (response != null && !response.isEmpty()) {
                        enquiry.setResponse(response);
                    }
                    
                    enquiries.add(enquiry);
                }
            }
        }
        
        return enquiries;
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
        return project.isVisible() && project.isEligibleForApplicant(user);
    }

    private boolean isAuthorizedToReply(User user, Enquiry enquiry) {
        // Only HDB Officers or Managers can reply
        if (!(user instanceof HDBOfficer || user instanceof HDBManager)) {
            return false;
        }

        // If user is an HDB Officer, they must be handling the project
        if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
            return officer.getHandlingProject() != null && 
                   officer.getHandlingProject().equals(enquiry.getProject());
        }

        // HDB Manager can reply to enquiries for any project
        return true;
    }

    private void saveEnquiry(Enquiry enquiry) {
        // In a real implementation, this would interact with DataStore
        List<String[]> enquiryData = dataStore.getEnquiryData();
        
        // Check if enquiry already exists
        boolean updated = false;
        for (int i = 0; i < enquiryData.size(); i++) {
            String[] row = enquiryData.get(i);
            if (row.length > 0 && row[0].equals(enquiry.getId())) {
                // Update existing enquiry
                String[] updatedRow = createEnquiryDataRow(enquiry);
                enquiryData.set(i, updatedRow);
                updated = true;
                break;
            }
        }
        
        // Add new enquiry if not found
        if (!updated) {
            String[] newRow = createEnquiryDataRow(enquiry);
            enquiryData.add(newRow);
        }
        
        DataStore.saveData();
    }

    private String[] createEnquiryDataRow(Enquiry enquiry) {
        String[] row = new String[6];
        row[0] = enquiry.getId();
        row[1] = enquiry.getSubmitter().getNric();
        row[2] = enquiry.getProject().getProjectName();
        row[3] = enquiry.getEnquiryText();
        row[4] = enquiry.getResponse() != null ? enquiry.getResponse() : "";
        row[5] = enquiry.getSubmissionDate().toString();
        return row;
    }

    private boolean removeEnquiry(Enquiry enquiry) {
        // In a real implementation, this would remove from DataStore
        List<String[]> enquiryData = dataStore.getEnquiryData();
        
        boolean removed = false;
        Iterator<String[]> iterator = enquiryData.iterator();
        while (iterator.hasNext()) {
            String[] row = iterator.next();
            if (row.length > 0 && row[0].equals(enquiry.getId())) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        
        if (removed) {
            DataStore.saveData();
        }
        
        return removed;
    }
    
    private BTOProject getProjectByName(String projectName) {
        // Get project service and find project by name
        ProjectService projectService = ProjectService.getInstance();
        return projectService.getProjectByName(projectName);
    }
}