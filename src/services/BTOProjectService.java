package services;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;
import enumeration.MaritalStatus;
import interfaces.IBTOProjectService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.Applicant;
import models.BTOApplication;
import models.BTOProject;
import models.FlatTypeDetails;
import models.HDBOfficer;
import models.ProjectFilter;
import models.User;
import stores.DataStore;

/**
 * Service class for BTO project operations that can be used by both applicants and HDB officers
 */
public class BTOProjectService implements IBTOProjectService {

    /**
     * Checks if a user is eligible to apply for a project
     * @param user The user to check
     * @param project The project to check against
     * @return true if the user is eligible, false otherwise
     */
    public boolean isEligible(User user, BTOProject project) {
        // Check if project is visible
        if (!project.isVisible()) {
            return false;
        }

        // Check if project is within application period
        LocalDate today = LocalDate.now();
        if (today.isBefore(project.getApplicationOpeningDate()) || 
            today.isAfter(project.getApplicationClosingDate())) {
            return false;
        }

        // Check if user already has an existing application
        if (user instanceof Applicant && hasExistingApplication((Applicant) user)) {
            return false;
        }
        if (user instanceof HDBOfficer && hasExistingApplication((HDBOfficer) user)) {
            return false;
        }

        // Check if HDB officer is already assigned to the project
        if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
            // Check if officer is already handling this project
            if (officer.getHandledProjects().contains(project)) {
                return false;
            }
            // Check if officer is already assigned to this project
            if (!canOfficerApplyForProject(project, officer)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets available BTO projects for a user
     * @param user The user to get projects for
     * @return List of available BTO projects
     */
    @Override
    public List<BTOProject> getAvailableProjects(User user) {
        return DataStore.getBTOProjectsData().values().stream()
                .filter(project -> isEligible(user, project))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets available BTO projects for a user with filtering
     * @param user The user to get projects for
     * @param filter The filter to apply to the projects
     * @return List of filtered available BTO projects
     */
    public List<BTOProject> getAvailableProjects(User user, ProjectFilter filter) {
        List<BTOProject> availableProjects = getAvailableProjects(user);
        return filter.applyFilter(availableProjects);
    }
    
    /**
     * Gets all visible BTO projects regardless of application status
     * @param user The user requesting projects
     * @return List of all visible BTO projects
     */
    public List<BTOProject> getEnquirableProjects(User user) {
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.isVisible() || 
                              DataStore.getBTOApplicationsData().values().stream()
                                  .anyMatch(app -> app.getApplicant().equals(user) && app.getProject().equals(project)))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all visible BTO projects with filtering
     * @param user The user requesting projects
     * @param filter The filter to apply
     * @return List of filtered visible BTO projects
     */
    public List<BTOProject> getEnquirableProjects(User user, ProjectFilter filter) {
        List<BTOProject> enquirableProjects = getEnquirableProjects(user);
        return filter.applyFilter(enquirableProjects);
    }
    
    /**
     * Gets all BTO projects with filtering
     * @param filter The filter to apply
     * @return List of filtered BTO projects
     */
    public List<BTOProject> getAllProjects(ProjectFilter filter) {
        List<BTOProject> allProjects = new ArrayList<>(DataStore.getBTOProjectsData().values());
        return filter.applyFilter(allProjects);
    }

    /**
     * Gets a filtered map of flat types that the applicant is eligible for
     * @param project The BTO project
     * @param applicant The applicant
     * @return Map of eligible flat types and their details
     */
    public Map<FlatType, FlatTypeDetails> getEligibleFlatTypes(BTOProject project, Applicant applicant) {
        Map<FlatType, FlatTypeDetails> allFlatTypes = project.getFlatTypes();
        Map<FlatType, FlatTypeDetails> eligibleFlatTypes = new HashMap<>();
        
        int age = applicant.getAge();
        boolean isMarried = applicant.getMaritalStatus() == MaritalStatus.MARRIED;
        
        // Married applicants 21 and above can apply for any flat type
        if (isMarried && age >= 21) {
            return allFlatTypes;
        }
        
        // Single applicants 35 and above can only apply for 2-room
        if (!isMarried && age >= 35) {
            if (allFlatTypes.containsKey(FlatType.TWO_ROOM)) {
                eligibleFlatTypes.put(FlatType.TWO_ROOM, allFlatTypes.get(FlatType.TWO_ROOM));
            }
        }
        
        return eligibleFlatTypes;
    }

    /**
     * Gets a filtered map of flat types that the HDB officer is eligible for
     * @param project The BTO project
     * @param hdbOfficer The HDB officer
     * @return Map of eligible flat types and their details
     */
    public Map<FlatType, FlatTypeDetails> getEligibleFlatTypes(BTOProject project, HDBOfficer hdbOfficer) {
        Map<FlatType, FlatTypeDetails> allFlatTypes = project.getFlatTypes();
        Map<FlatType, FlatTypeDetails> eligibleFlatTypes = new HashMap<>();
        
        int age = hdbOfficer.getAge();
        boolean isMarried = hdbOfficer.getMaritalStatus() == MaritalStatus.MARRIED;
        
        // Married applicants 21 and above can apply for any flat type
        if (isMarried && age >= 21) {
            return allFlatTypes;
        }
        
        // Single applicants 35 and above can only apply for 2-room
        if (!isMarried && age >= 35) {
            if (allFlatTypes.containsKey(FlatType.TWO_ROOM)) {
                eligibleFlatTypes.put(FlatType.TWO_ROOM, allFlatTypes.get(FlatType.TWO_ROOM));
            }
        }
        
        return eligibleFlatTypes;
    }

    /**
     * Submits a BTO application
     * @param application The BTO application to submit
     */
    public void applyForBTOProject(BTOApplication application) {
        DataStore.getBTOApplicationsData().put(application.getApplicationId(), application);
        DataStore.saveData();
    }

    /**
     * Checks if an applicant already has an existing application
     * @param applicant The applicant to check
     * @return true if the applicant has an existing application, false otherwise
     */
    public boolean hasExistingApplication(Applicant applicant) {
        return DataStore.getBTOApplicationsData().values().stream()
            .anyMatch(application -> application.getApplicant().equals(applicant) && application.getStatus() != BTOApplicationStatus.UNSUCCESSFUL);
    }

    /**
     * Checks if an HDB officer already has an existing application
     * @param hdbOfficer The HDB officer to check
     * @return true if the HDB officer has an existing application, false otherwise
     */
    public boolean hasExistingApplication(HDBOfficer hdbOfficer) {
        return DataStore.getBTOApplicationsData().values().stream()
            .anyMatch(application -> application.getApplicant().equals(hdbOfficer));
    }
    
    /**
     * Gets all BTO applications for a specific applicant
     * @param applicant The applicant to get applications for
     * @return List of BTO applications for the applicant
     */
    public List<BTOApplication> getApplicationsByApplicant(Applicant applicant) {
        return DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> application.getApplicant().equals(applicant))
            .collect(Collectors.toList());
    }

    /**
     * Gets all BTO applications for a specific HDB officer
     * @param hdbOfficer The HDB officer to get applications for
     * @return List of BTO applications for the HDB officer
     */
    public List<BTOApplication> getApplicationsByApplicant(HDBOfficer hdbOfficer) {
        return DataStore.getBTOApplicationsData().values().stream()
            .filter(application -> application.getApplicant().equals(hdbOfficer))
            .collect(Collectors.toList());
    }

    /**
     * Gets all BTO projects that an HDB officer can join
     * @param hdbOfficer The HDB officer
     * @return List of joinable BTO projects
     */
    public List<BTOProject> getJoinableProjects(HDBOfficer hdbOfficer) {
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.isVisible() && 
                    project.getHDBOfficers().size() < project.getHDBOfficerSlots() &&
                    !project.getHDBOfficers().contains(hdbOfficer))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all BTO projects that an HDB officer can join with filtering
     * @param hdbOfficer The HDB officer
     * @param filter The filter to apply
     * @return List of filtered joinable BTO projects
     */
    public List<BTOProject> getJoinableProjects(HDBOfficer hdbOfficer, ProjectFilter filter) {
        List<BTOProject> joinableProjects = getJoinableProjects(hdbOfficer);
        return filter.applyFilter(joinableProjects);
    }

    /**
     * Gets all BTO projects that an HDB officer has joined
     * @param hdbOfficer The HDB officer
     * @return List of joined BTO projects
     */
    public List<BTOProject> getJoinedProjects(HDBOfficer hdbOfficer) {
        return DataStore.getBTOProjectsData().values().stream()
            .filter(project -> project.getHDBOfficers().contains(hdbOfficer))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all BTO projects that an HDB officer has joined with filtering
     * @param hdbOfficer The HDB officer
     * @param filter The filter to apply
     * @return List of filtered joined BTO projects
     */
    public List<BTOProject> getJoinedProjects(HDBOfficer hdbOfficer, ProjectFilter filter) {
        List<BTOProject> joinedProjects = getJoinedProjects(hdbOfficer);
        return filter.applyFilter(joinedProjects);
    }

    /**
     * Adds an HDB officer to a BTO project
     * @param project The BTO project
     * @param hdbOfficer The HDB officer to add
     */
    public void joinProjectAsOfficer(BTOProject project, HDBOfficer hdbOfficer) {
        project.addHDBOfficer(hdbOfficer);
        DataStore.saveData();
    }

    /**
     * Removes an HDB officer from a BTO project
     * @param project The BTO project
     * @param hdbOfficer The HDB officer to remove
     */
    public void leaveProjectAsOfficer(BTOProject project, HDBOfficer hdbOfficer) {
        project.removeHDBOfficer(hdbOfficer);
        DataStore.saveData();
    }

    /**
     * Checks if an HDB officer can apply for a BTO project
     * @param project The BTO project
     * @param hdbOfficer The HDB officer
     * @return true if the HDB officer can apply for the project, false otherwise
     */
    public boolean canOfficerApplyForProject(BTOProject project, HDBOfficer hdbOfficer) {
        return !project.getHDBOfficers().contains(hdbOfficer);
    }
} 