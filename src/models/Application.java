package models;

import enumeration.ApplicationStatus;
import enumeration.FlatType;
public class Application {
    private String applicationId;
    private Applicant applicant;
    private BTOProject project;
    private ApplicationStatus status;
    private String bookingReceipt;
    private FlatType flatType; // Added flatType variable

    public Application(String applicationId, Applicant applicant, BTOProject project) {
        this.applicationId = applicationId;
        this.applicant = applicant;
        this.project = project;
        this.status = ApplicationStatus.PENDING; // Default status
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public BTOProject getProject() {
        return project;
    }

    public void setProject(BTOProject project) {
        this.project = project;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getBookingReceipt() {
        return bookingReceipt;
    }

    public void setBookingReceipt(String bookingReceipt) {
        this.bookingReceipt = bookingReceipt;

    }

    public FlatType getFlatType() {
        return flatType;
    }
    
    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }
    
}
