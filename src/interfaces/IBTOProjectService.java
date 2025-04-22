package interfaces;

import java.util.List;
import java.util.Map;
import models.BTOProject;
import models.User;
import models.Applicant;
import models.HDBOfficer;
import models.BTOApplication;
import models.FlatTypeDetails;
import enumeration.FlatType;

/**
 * Interface defining the service operations for BTO project management.
 * 
 * This interface provides the contract for all BTO project-related operations including
 * retrieving available projects, checking eligibility, managing applications,
 * and handling officer assignments. It serves as the business logic layer for
 * BTO project interactions in the system.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public interface IBTOProjectService {
    public List<BTOProject> getAvailableProjects(User user);
    public boolean isEligible(User user, BTOProject project);
    public Map<FlatType, FlatTypeDetails> getEligibleFlatTypes(BTOProject project, Applicant applicant);
    public Map<FlatType, FlatTypeDetails> getEligibleFlatTypes(BTOProject project, HDBOfficer hdbOfficer);
    public void applyForBTOProject(BTOApplication application);
    public boolean hasExistingApplication(Applicant applicant);
    public boolean hasExistingApplication(HDBOfficer hdbOfficer);
    public List<BTOApplication> getApplicationsByApplicant(Applicant applicant);
    public List<BTOApplication> getApplicationsByApplicant(HDBOfficer hdbOfficer);
    public List<BTOProject> getJoinableProjects(HDBOfficer hdbOfficer);
    public List<BTOProject> getJoinedProjects(HDBOfficer hdbOfficer);
    public void joinProjectAsOfficer(BTOProject project, HDBOfficer hdbOfficer);
    public void leaveProjectAsOfficer(BTOProject project, HDBOfficer hdbOfficer);
    public boolean canOfficerApplyForProject(BTOProject project, HDBOfficer hdbOfficer);
}