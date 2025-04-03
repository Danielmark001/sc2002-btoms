package models;

import enumeration.FlatType;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import enumeration.MaritalStatus; // Ensure MaritalStatus is imported
import enumeration.ApplicationStatus; // Ensure ApplicationStatus is imported
import enumeration.RegistrationStatus; // Ensure RegistrationStatus is imported
import enumeration.UserStatus; // Ensure UserStatus is imported

public class BTOProject {
    // Unique identifier for the project
    private String projectId;

    // Project details
    private String projectName;
    private String neighborhood;

    // Project dates
    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;

    // Flat type and unit information
    private Map<FlatType, Integer> flatTypes;

    // Project management details
    private HDBManager hdbManager;
    private int availableHDBOfficerSlots;
    private boolean visibility;

    // Lists to track applications and registrations
    private List<Application> applications;
    private List<Registration> registrations;

    // Constructors
    public BTOProject(String projectName, String neighborhood, 
                      LocalDate applicationOpeningDate, LocalDate applicationClosingDate) {
        // Validate inputs
        validateProjectName(projectName);
        validateNeighborhood(neighborhood);
        validateDates(applicationOpeningDate, applicationClosingDate);

        // Generate unique project ID
        this.projectId = generateUniqueProjectId();
        
        // Initialize project details
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;

        // Initialize collections
        this.flatTypes = new ConcurrentHashMap<>();
        this.applications = Collections.synchronizedList(new ArrayList<>());
        this.registrations = Collections.synchronizedList(new ArrayList<>());

        // Default initializations
        this.availableHDBOfficerSlots = 10; // Default max officer slots
        this.visibility = true; // Default to visible
    }

    // Validation methods
    private void validateProjectName(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty");
        }
    }

    private void validateNeighborhood(String neighborhood) {
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be empty");
        }
    }

    private void validateDates(LocalDate openingDate, LocalDate closingDate) {
        if (openingDate == null || closingDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        if (openingDate.isAfter(closingDate)) {
            throw new IllegalArgumentException("Opening date must be before closing date");
        }
    }

    // Generate unique project ID
    private String generateUniqueProjectId() {
        return "PROJ-" + System.currentTimeMillis() + 
               "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // Getters and Setters
    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        validateProjectName(projectName);
        this.projectName = projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        validateNeighborhood(neighborhood);
        this.neighborhood = neighborhood;
    }

    public LocalDate getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    public void setApplicationOpeningDate(LocalDate applicationOpeningDate) {
        validateDates(applicationOpeningDate, this.applicationClosingDate);
        this.applicationOpeningDate = applicationOpeningDate;
    }

    public LocalDate getApplicationClosingDate() {
        return applicationClosingDate;
    }

    public void setApplicationClosingDate(LocalDate applicationClosingDate) {
        validateDates(this.applicationOpeningDate, applicationClosingDate);
        this.applicationClosingDate = applicationClosingDate;
    }

    // Flat Types Management
    public Map<FlatType, Integer> getFlatTypes() {
        return new HashMap<>(flatTypes);
    }

    public void setFlatTypes(Map<FlatType, Integer> flatTypes) {
        // Validate input
        if (flatTypes == null) {
            throw new IllegalArgumentException("Flat types cannot be null");
        }

        // Validate unit counts
        flatTypes.forEach((type, count) -> {
            if (count < 0) {
                throw new IllegalArgumentException("Unit count cannot be negative for " + type);
            }
        });

        // Replace existing flat types
        this.flatTypes.clear();
        this.flatTypes.putAll(flatTypes);
    }

    // Available Units Management
    public int getAvailableUnits(FlatType flatType) {
        // Calculate available units by subtracting booked applications
        Integer totalUnits = flatTypes.getOrDefault(flatType, 0);
        
        long bookedUnits = applications.stream()
            .filter(app -> app.getFlatType() == flatType && 
                           app.getStatus() == ApplicationStatus.BOOKED)
            .count();
        
        return Math.max(0, totalUnits - (int)bookedUnits);
    }

    // HDB Manager Management
    public HDBManager getHdbManager() {
        return hdbManager;
    }

    public void setHdbManager(HDBManager hdbManager) {
        if (hdbManager == null) {
            throw new IllegalArgumentException("HDB Manager cannot be null");
        }
        this.hdbManager = hdbManager;
    }

    // Officer Slots Management
    public int getAvailableHDBOfficerSlots() {
        return availableHDBOfficerSlots;
    }

    public void setAvailableHDBOfficerSlots(int availableHDBOfficerSlots) {
        if (availableHDBOfficerSlots < 0 || availableHDBOfficerSlots > 10) {
            throw new IllegalArgumentException("Officer slots must be between 0 and 10");
        }
        this.availableHDBOfficerSlots = availableHDBOfficerSlots;
    }

    // Visibility Management
    

    // Applications Management
    public List<Application> getApplications() {
        return new ArrayList<>(applications);
    }

    public void addApplication(Application application) {
        if (application != null && !applications.contains(application)) {
            applications.add(application);
        }
    }

    // Registrations Management
    public List<Registration> getRegistrations() {
        return new ArrayList<>(registrations);
    }

    public void addRegistration(Registration registration) {
        if (registration != null && !registrations.contains(registration)) {
            registrations.add(registration);
        }
    }

    // Applicant Eligibility Check
    public boolean isEligibleForApplicant(User applicant) {
        // Check project visibility
        if (!visibility) {
            return false;
        }

        // Check current date is within application period
        LocalDate now = LocalDate.now();
        if (now.isBefore(applicationOpeningDate) || now.isAfter(applicationClosingDate)) {
            return false;
        }

        // Check age and marital status
        int age = applicant.calculateAge();
        MaritalStatus maritalStatus = applicant.getMaritalStatus();

        if (maritalStatus == MaritalStatus.SINGLE) {
            return age >= 35 && flatTypes.containsKey(FlatType.TWO_ROOM);
        } else if (maritalStatus == MaritalStatus.MARRIED) {
            return age >= 21 && 
                   (flatTypes.containsKey(FlatType.TWO_ROOM) || 
                    flatTypes.containsKey(FlatType.THREE_ROOM));
        }

        return false;
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BTOProject that = (BTOProject) o;
        return Objects.equals(projectId, that.projectId) || 
               Objects.equals(projectName, that.projectName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, projectName);
    }

    // ToString
    @Override
    public String toString() {
        return "BTOProject{" +
                "projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", applicationPeriod=" + applicationOpeningDate + " to " + applicationClosingDate +
                ", availableHDBOfficerSlots=" + availableHDBOfficerSlots +
                ", visibility=" + visibility +
                '}';
    }
    /**
 * Gets the visibility of the project
 * @return true if project is visible, false otherwise
 */
public boolean isVisible() {
    return visibility;
}

/**
 * Sets the visibility of the project
 * @param visible true to set project visible, false otherwise
 */
public void setVisible(boolean visible) {
    this.visibility = visible;
}

/**
 * Alias for isVisible() to maintain compatibility
 * @return true if project is visible, false otherwise
 */
public boolean isVisibility() {
    return isVisible();
}

/**
 * Alias for setVisible() to maintain compatibility
 * @param visibility true to set project visible, false otherwise
 */
public void setVisibility(boolean visibility) {
    setVisible(visibility);
}
/**
 * Gets the total number of units for a specific flat type
 * @param flatType Flat type to check
 * @return Total number of units
 */
public int getTotalUnits(FlatType flatType) {
    return flatTypes.getOrDefault(flatType, 0);
}

/**
 * Gets the manager in charge of the project
 * @return HDB Manager in charge
 */
public HDBManager getManagerInCharge() {
    return hdbManager;
}

/**
 * Gets the list of HDB Officer IDs assigned to the project
 * @return List of officer IDs
 */
public List<String> getOfficerIds() {
    List<String> officerIds = new ArrayList<>();
    for (Registration reg : registrations) {
        if (reg.getStatus() == RegistrationStatus.APPROVED) {
            officerIds.add(reg.getOfficer().getNric());
        }
    }
    return officerIds;
}

/**
 * Gets the officer slots available for the project
 * @return Number of officer slots
 */
public int getOfficerSlots() {
    return availableHDBOfficerSlots;
}

/**
 * Gets the opening date for applications
 * @return Opening date
 */
public LocalDate getOpeningDate() {
    return applicationOpeningDate;
}

/**
 * Gets the closing date for applications
 * @return Closing date
 */
public LocalDate getClosingDate() {
    return applicationClosingDate;
}

    
    

}