package models.entity;

import java.time.LocalDate;
import java.util.List;
import models.enumeration.FlatType;

public class BTOProject {
    private String projectName;
    private String neighborhood;
    private List<FlatType> flatTypes;
    private LocalDate applicationOpeningDate;
    private LocalDate applicationClosingDate;
    private User hdbManager;
    private int availableHDBOfficerSlots;
    private boolean visibility;

    // Constructors
    public BTOProject() {}

    public BTOProject(String projectName, String neighborhood, LocalDate applicationOpeningDate, LocalDate applicationClosingDate) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.visibility = true; // default to visible
        this.availableHDBOfficerSlots = 10; // max 10 slots as per requirement
    }

    // Getters and Setters
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public List<FlatType> getFlatTypes() {
        return flatTypes;
    }

    public void setFlatTypes(List<FlatType> flatTypes) {
        this.flatTypes = flatTypes;
    }

    public LocalDate getApplicationOpeningDate() {
        return applicationOpeningDate;
    }

    public void setApplicationOpeningDate(LocalDate applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }

    public LocalDate getApplicationClosingDate() {
        return applicationClosingDate;
    }

    public void setApplicationClosingDate(LocalDate applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }

    public User getHdbManager() {
        return hdbManager;
    }

    public void setHdbManager(User hdbManager) {
        this.hdbManager = hdbManager;
    }

    public int getAvailableHDBOfficerSlots() {
        return availableHDBOfficerSlots;
    }

    public void setAvailableHDBOfficerSlots(int availableHDBOfficerSlots) {
        this.availableHDBOfficerSlots = availableHDBOfficerSlots;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    // Method to check project eligibility
    public boolean isEligibleForApplicant(User applicant) {
        // Check visibility
        if (!this.visibility) {
            return false;
        }

        // Check applicant's age and marital status
        int age = applicant.calculateAge();
        
        // Check age and flat type eligibility
        if (applicant.getMaritalStatus() == User.MaritalStatus.SINGLE) {
            return age >= 35 && flatTypes.contains(FlatType.TWO_ROOM);
        } else if (applicant.getMaritalStatus() == User.MaritalStatus.MARRIED) {
            return age >= 21 && (flatTypes.contains(FlatType.TWO_ROOM) || flatTypes.contains(FlatType.THREE_ROOM));
        }

        return false;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectName='" + projectName + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", applicationOpeningDate=" + applicationOpeningDate +
                ", applicationClosingDate=" + applicationClosingDate +
                ", visibility=" + visibility +
                '}';
    }
}