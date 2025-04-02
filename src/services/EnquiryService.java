package services;

import models.Enquiry;
import models.BTOProject;
import models.User;
import stores.DataStore;

import java.util.List;

public class EnquiryService implements IEnquiryService {
    private DataStore dataStore;

    public EnquiryService() {
        this.dataStore = DataStore.getInstance();
    }

    public Enquiry createEnquiry(User user, BTOProject project, String content) {
        Enquiry enquiry = new Enquiry(user, project, content);
        dataStore.addEnquiry(enquiry);
        return enquiry;
    }

    public boolean editEnquiry(Enquiry enquiry, User user, String newContent) {
        // Only allow submitter to edit  
        if (!enquiry.getSubmitter().equals(user)) {
            return false;
        }
        
        if (!enquiry.canModify()) {
            return false;  // Cannot modify if already responded to
        }
        
        enquiry.setEnquiryText(newContent);
        dataStore.updateEnquiry(enquiry);
        return true;
    }

    public boolean deleteEnquiry(Enquiry enquiry, User user) {
        // Only allow submitter to delete
        if (!enquiry.getSubmitter().equals(user)) {
            return false;
        }
        
        return dataStore.deleteEnquiry(enquiry.getId());
    }

    public boolean replyToEnquiry(Enquiry enquiry, User user, String replyContent) {
        // Only allow project officer/manager to reply
        if (!(user instanceof HDBOfficer || user instanceof HDBManager)) {
            return false;
        }
        
        if (!enquiry.getProject().equals(((HDBOfficer) user).getHandlingProject()) &&
            !enquiry.getProject().getHdbManager().equals(user)) {
            return false;  // User not authorized for this project
        }
        
        enquiry.setResponse(replyContent);
        enquiry.setRespondent(user);
        dataStore.updateEnquiry(enquiry);
        return true;
    }

    public List<Enquiry> getEnquiriesByProject(Project project) {
        return dataStore.getEnquiriesByProject(project.getProjectId());  
    }

    public List<Enquiry> getEnquiriesByUser(User user) {
        return dataStore.getEnquiriesByUser(user.getNric());
    }

    public List<Enquiry> getAllEnquiries() {
        return dataStore.getAllEnquiries();
    }

    public List<Enquiry> getUnansweredEnquiries() {
        return dataStore.getAllEnquiries().stream()
                .filter(enq -> enq.getStatus() == Enquiry.EnquiryStatus.SUBMITTED)
                .toList();
    }
}