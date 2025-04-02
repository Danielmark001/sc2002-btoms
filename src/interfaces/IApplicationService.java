package interfaces;

import models.Application;
import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.BTOProject;
import enumeration.ApplicationStatus;
import enumeration.FlatType;

import java.util.List;

/**
 * Interface for the Application Service 
 * Defines methods for application-related operations
 */
public interface IApplicationService {
    Application createApplication(Applicant applicant, BTOProject project);
    boolean approveApplication(Application application, HDBManager manager);
    boolean rejectApplication(Application application, HDBManager manager);
    boolean processWithdrawal(Application application, HDBManager manager, boolean approve);
    boolean bookFlat(Application application, HDBOfficer officer, FlatType flatType);
    List<Application> getApplicationsByProject(BTOProject project);
    List<Application> getApplicationsByStatus(BTOProject project, ApplicationStatus status);
    String generateBookingReceipt(String applicantNric);
}