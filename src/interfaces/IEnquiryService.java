package interfaces;

import models.Enquiry;
import models.BTOProject;
import models.User;

import java.util.List;

/**
 * Interface for the Enquiry Service
 * Defines methods for enquiry-related operations  
 */
public interface IEnquiryService {
    Enquiry createEnquiry(User user, BTOProject project, String content);
    boolean editEnquiry(Enquiry enquiry, User user, String newContent);
    boolean deleteEnquiry(Enquiry enquiry, User user);
    boolean replyToEnquiry(Enquiry enquiry, User user, String replyContent);
    List<Enquiry> getEnquiriesByProject(BTOProject project);
    List<Enquiry> getEnquiriesByUser(User user); 
    List<Enquiry> getAllEnquiries();
    List<Enquiry> getUnansweredEnquiries();
}
