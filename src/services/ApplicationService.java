package services;

import models.entity.Application;
import models.entity.Applicant;
import models.entity.HDBManager;
import models.entity.HDBOfficer;
import models.entity.Project;
import models.enumeration.ApplicationStatus;
import models.enumeration.FlatType;
import stores.DataStore;

import java.util.List;

public class ApplicationService implements IApplicationService {
    private DataStore dataStore;

    public ApplicationService() {
        this.dataStore = DataStore.getInstance();
    }

    public Application createApplication(Applicant applicant, Project project) {
        // Validate eligibility and business rules
        if (!project.isEligibleForApplicant(applicant)) {
            throw new IllegalStateException("Applicant not eligible for this project");
        }

        // Create new application
        Application application = new Application(applicant, project);  
        applicant.addApplication(application);
        project.addApplication(application);

        dataStore.saveApplication(application);  // Persist application
        return application;
    }

    public boolean approveApplication(Application application, HDBManager manager) { 
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return false;  // Can only approve pending applications  
        }
        
        // Check flat availability  
        FlatType flatType = application.getFlatType();
        if (application.getProject().getAvailableUnits(flatType) > 0) {
            application.setStatus(ApplicationStatus.SUCCESSFUL);
            dataStore.saveApplication(application);
            return true;
        }
        return false;
    }

    public boolean rejectApplication(Application application, HDBManager manager) {
        if (application.getStatus() != ApplicationStatus.PENDING) {
            return false;  // Can only reject pending applications
        }

        application.setStatus(ApplicationStatus.UNSUCCESSFUL); 
        dataStore.saveApplication(application);
        return true;
    }

    public boolean processWithdrawal(Application application, HDBManager manager, boolean approve) {
        if (!application.isWithdrawalRequested()) {
            return false;  // No withdrawal request
        }
        
        if (approve) {
            application.setStatus(ApplicationStatus.WITHDRAWN);
        } else {
            application.setWithdrawalRequested(false); // Reset withdrawal request  
        }
        dataStore.saveApplication(application);
        return true;
    }

    public boolean bookFlat(Application application, HDBOfficer officer, FlatType flatType) {
        if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            return false;  // Can only book for approved applications
        }
        
        // Update flat availability
        Project project = application.getProject();
        if (project.getAvailableUnits(flatType) <= 0) {
            return false;  // No flats available
        }
        project.decrementAvailableUnits(flatType);
        dataStore.saveProject(project);

        // Update application status 
        application.setStatus(ApplicationStatus.BOOKED);
        application.setBookedFlatType(flatType);
        dataStore.saveApplication(application);
        return true;
    }

    public List<Application> getApplicationsByProject(Project project) {
        return dataStore.getApplicationsByProject(project.getProjectId());
    }

    public List<Application> getApplicationsByStatus(Project project, ApplicationStatus status) {
        return dataStore.getApplicationsByProjectAndStatus(project.getProjectId(), status);
    }

    public String generateBookingReceipt(String applicantNric) {
        return dataStore.generateBookingReceipt(applicantNric);
    }
}