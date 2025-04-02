package interfaces;

import models.entity.Application;
import models.entity.Applicant;
import models.entity.HDBManager;
import models.entity.HDBOfficer;
import models.entity.Project;
import models.enumeration.ApplicationStatus;
import models.enumeration.FlatType;

import java.util.List;

/**
 * Interface for the Application Service 
 * Defines methods for application-related operations
 */
public interface IApplicationService {
    Application createApplication(Applicant applicant, Project project);
    boolean approveApplication(Application application, HDBManager manager);
    boolean rejectApplication(Application application, HDBManager manager);
    boolean processWithdrawal(Application application, HDBManager manager, boolean approve);
    boolean bookFlat(Application application, HDBOfficer officer, FlatType flatType);
    List<Application> getApplicationsByProject(Project project);
    List<Application> getApplicationsByStatus(Project project, ApplicationStatus status);
    String generateBookingReceipt(String applicantNric);
}