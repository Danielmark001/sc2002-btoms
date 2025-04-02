package models;

import java.time.LocalDateTime;

public class Enquiry {
    private String id;
    private User submitter;
    private Project project;
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

    public Enquiry(User submitter, Project project, String enquiryText) {
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
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

    public void setStatus(EnquiryStatus status) {
        this.status = status;
    }

    // Method to close the enquiry
    public void closeEnquiry() {
        this.status = EnquiryStatus.CLOSED;
    }

    // Method to check if enquiry can be modified
    public boolean canModify() {
        return this.status == EnquiryStatus.SUBMITTED;
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
}