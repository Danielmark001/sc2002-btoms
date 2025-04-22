package services;

import java.util.List;
import java.util.stream.Collectors;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;
import enumeration.MaritalStatus;
import models.BTOApplication;
import models.BTOProject;
import stores.DataStore;

/**
 * Service class for generating reports and statistical analysis of BTO applications.
 * 
 * This class provides functionality to retrieve and filter BTO applications based on
 * various criteria such as project, flat type, applicant marital status, and age range.
 * It serves as the business logic layer for report generation features in the system.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class ReportService {
    
    /**
     * Get all successful applications
     * @return List of successful applications
     */
    public List<BTOApplication> getAllSuccessfulApplications() {
        return DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> application.getStatus() == BTOApplicationStatus.SUCCESSFUL)
            .collect(Collectors.toList());
    }
    
    /**
     * Filter applications by project
     * @param applications List of applications to filter
     * @param project Project to filter by
     * @return Filtered list of applications
     */
    public List<BTOApplication> filterByProject(List<BTOApplication> applications, BTOProject project) {
        return applications.stream()
            .filter(application -> application.getProject().equals(project))
            .collect(Collectors.toList());
    }
    
    /**
     * Filter applications by flat type
     * @param applications List of applications to filter
     * @param flatType Flat type to filter by
     * @return Filtered list of applications
     */
    public List<BTOApplication> filterByFlatType(List<BTOApplication> applications, FlatType flatType) {
        return applications.stream()
            .filter(application -> application.getFlatType() == flatType)
            .collect(Collectors.toList());
    }
    
    /**
     * Filter applications by marital status
     * @param applications List of applications to filter
     * @param maritalStatus Marital status to filter by
     * @return Filtered list of applications
     */
    public List<BTOApplication> filterByMaritalStatus(List<BTOApplication> applications, MaritalStatus maritalStatus) {
        return applications.stream()
            .filter(application -> application.getApplicant().getMaritalStatus() == maritalStatus)
            .collect(Collectors.toList());
    }
    
    /**
     * Filter applications by age range
     * @param applications List of applications to filter
     * @param minAge Minimum age
     * @param maxAge Maximum age
     * @return Filtered list of applications
     */
    public List<BTOApplication> filterByAgeRange(List<BTOApplication> applications, int minAge, int maxAge) {
        return applications.stream()
            .filter(application -> {
                int age = application.getApplicant().getAge();
                return age >= minAge && age <= maxAge;
            })
            .collect(Collectors.toList());
    }
} 