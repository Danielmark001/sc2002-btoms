package models;

import java.time.LocalDateTime;
import java.util.UUID;
import enumeration.BTOApplicationStatus;

/**
 * Represents a request to withdraw a BTO application.
 * 
 * This class models the process of an applicant requesting to withdraw their
 * BTO application. It tracks the request status, timestamps, and processing
 * information. When a withdrawal request is approved, the associated BTO
 * application status is updated automatically.
 * 
 * @author BTOMS Team
 * @version 1.0
 */
public class WithdrawalRequest {
    private final String requestId;
    private final BTOApplication application;
    private final LocalDateTime requestedAt;
    private boolean isApproved;
    private LocalDateTime processedAt;
    private String processedBy;

    /**
     * Constructs a new withdrawal request for a BTO application.
     * 
     * @param application The BTO application to be withdrawn
     */
    public WithdrawalRequest(BTOApplication application) {
        this.requestId = generateRequestId();
        this.application = application;
        this.requestedAt = LocalDateTime.now();
        this.isApproved = false;
    }

    /**
     * Generates a unique withdrawal request ID combining the current date and a UUID.
     * 
     * @return A unique withdrawal request identifier
     */
    private static String generateRequestId() {
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "WDR-" + date + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Gets the withdrawal request ID.
     * 
     * @return The unique identifier for this withdrawal request
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Gets the BTO application associated with this withdrawal request.
     * 
     * @return The BTO application to be withdrawn
     */
    public BTOApplication getApplication() {
        return application;
    }

    /**
     * Gets the timestamp when the withdrawal request was submitted.
     * 
     * @return The date and time when the request was created
     */
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    /**
     * Checks if the withdrawal request has been approved.
     * 
     * @return true if the request has been approved, false if it's pending or rejected
     */
    public boolean isApproved() {
        return isApproved;
    }

    /**
     * Gets the timestamp when the withdrawal request was processed.
     * 
     * @return The date and time when the request was processed, or null if not yet processed
     */
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    /**
     * Gets the name of the person who processed this withdrawal request.
     * 
     * @return The name of the HDB manager who approved or rejected the request,
     *         or null if not yet processed
     */
    public String getProcessedBy() {
        return processedBy;
    }

    /**
     * Approves the withdrawal request and updates the associated application status.
     * 
     * @param processedBy The name of the HDB manager approving the request
     */
    public void approve(String processedBy) {
        this.isApproved = true;
        this.processedAt = LocalDateTime.now();
        this.processedBy = processedBy;
        // Update application status to UNSUCCESSFUL
        this.application.setStatus(BTOApplicationStatus.UNSUCCESSFUL);
    }

    /**
     * Rejects the withdrawal request.
     * 
     * @param processedBy The name of the HDB manager rejecting the request
     */
    public void reject(String processedBy) {
        this.isApproved = false;
        this.processedAt = LocalDateTime.now();
        this.processedBy = processedBy;
    }
} 