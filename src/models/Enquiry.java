package models;

import java.time.LocalDateTime;

public class Enquiry {
    private String id;
    private User submitter;
    private BTOProject project;
    private String enquiryText;
    private String response;
    private User respondent;
    private LocalDateTime submissionDate;
    private LocalDateTime responseDate;
    private EnquiryStatus status;

    // Enum for enquiry status
    public enum EnquiryStatus {
        SUBMITTED,   // Initial status when enquiry is created
        RESPONDED,   // Enquiry has been responded to
        CLOSED       // Enquiry is resolved and closed
    }

    // Constructors
    public Enquiry() {}

    public Enquiry(User submitter, BTOProject project, String enquiryText) {
        this.id = generateUniqueId();
        this.submitter = submitter;
        this.project = project;
        this.enquiryText = enquiryText;
        this.status = EnquiryStatus.SUBMITTED;
        this.submissionDate = LocalDateTime.now();
    }

    // Generate unique ID (simple implementation)
    private String generateUniqueId() {
        return "ENQ-" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public User getSubmitter() {
        return submitter;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    public BTOProject getProject() {
        return project;
    }

    public void setProject(BTOProject project) {
        this.project = project;
    }

    public String getEnquiryText() {
        return enquiryText;
    }

    public void setEnquiryText(String enquiryText) {
        this.enquiryText = enquiryText;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
        this.responseDate = LocalDateTime.now();
        this.status = EnquiryStatus.RESPONDED;
    }

    public User getRespondent() {
        return respondent;
    }

    public void setRespondent(User respondent) {
        this.respondent = respondent;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public EnquiryStatus getStatus() {
        return status;
    }

 

    // Method to close the enquiry
    public void closeEnquiry() {
        this.status = EnquiryStatus.CLOSED;
    }
    private LocalDateTime statusUpdateDate;

public void setStatus(EnquiryStatus status) {
    this.status = status;
    this.statusUpdateDate = LocalDateTime.now();
}

public LocalDateTime getStatusUpdateDate() {
    return statusUpdateDate;
}


  

    @Override
    public String toString() {
        return "Enquiry{" +
                "id='" + id + '\'' +
                ", submitter=" + submitter.getNric() +
                ", project=" + project.getProjectName() +
                ", status=" + status +
                ", submissionDate=" + submissionDate +
                '}';
    }

    // Method to check if enquiry is open
    public boolean isOpen() {
        return this.status == EnquiryStatus.SUBMITTED || this.status == EnquiryStatus.RESPONDED;
    }

    // Method to check if enquiry is closed
    public boolean isClosed() {
        return this.status == EnquiryStatus.CLOSED;
    }
    
    // Method to check if enquiry is responded
    public boolean isResponded() {
        return this.status == EnquiryStatus.RESPONDED;
    }

    public boolean canModify() {
    // Enquiry can only be modified when in SUBMITTED status and no response yet
    return this.status == EnquiryStatus.SUBMITTED && 
           this.response == null && 
           this.responseDate == null &&
           this.respondent == null;
}

}