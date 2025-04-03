package interfaces;

import models.BTOApplication;
import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.BTOProject;
import enumeration.ApplicationStatus;
import enumeration.FlatType;

import java.util.List;

/**
 * Interface for the BTOApplication Service 
 * Defines methods for application-related operations
 */
public interface IBTOApplicationService {
    BTOApplication createBTOApplication(Applicant applicant, BTOProject project);
    boolean approveBTOApplication(BTOApplication application, HDBManager manager);
    boolean rejectBTOApplication(BTOApplication application, HDBManager manager);
    boolean processWithdrawal(BTOApplication application, HDBManager manager, boolean approve);
    boolean bookFlat(BTOApplication application, HDBOfficer officer, FlatType flatType);
    List<BTOApplication> getBTOApplicationsByProject(BTOProject project);
    List<BTOApplication> getBTOApplicationsByStatus(BTOProject project, ApplicationStatus status);
    String generateBookingReceipt(String applicantNric);
}