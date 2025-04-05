package models;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;

import java.util.UUID;

public class BTOApplication {
    // Unique ID for the application
    private String applicationId;

    private final Applicant applicant;
    private final BTOProject project;
    private FlatType flatType;  // Now optional

    private BTOApplicationStatus status;

    public BTOApplication(Applicant applicant, BTOProject project) {
        this.applicationId = generateApplicationId();
        this.applicant = applicant;
        this.project = project;
        this.flatType = null;  // Initially null
        this.status = BTOApplicationStatus.PENDING;
    }

    public BTOApplication(Applicant applicant, BTOProject project, FlatType flatType) {
        this.applicationId = generateApplicationId();
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
        this.status = BTOApplicationStatus.PENDING;
    }

    private String generateApplicationId() {
        String date = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return date + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String getApplicationId() {
        return applicationId;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public BTOProject getProject() {
        return project;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public BTOApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(BTOApplicationStatus status) {
        switch (this.status) {
            case PENDING:
                if (!(status == BTOApplicationStatus.SUCCESSFUL ||
                      status == BTOApplicationStatus.UNSUCCESSFUL)) {
                    throw new IllegalArgumentException("Invalid status for pending application");
                }
                break;
        }
        this.status = status;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }
}
