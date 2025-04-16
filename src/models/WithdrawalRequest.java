package models;

import java.time.LocalDateTime;
import java.util.UUID;
import enumeration.BTOApplicationStatus;

/**
 * Model representing a withdrawal request for a BTO application
 */
public class WithdrawalRequest {
    private final String requestId;
    private final BTOApplication application;
    private final LocalDateTime requestedAt;
    private boolean isApproved;
    private LocalDateTime processedAt;
    private String processedBy;

    public WithdrawalRequest(BTOApplication application) {
        this.requestId = generateRequestId();
        this.application = application;
        this.requestedAt = LocalDateTime.now();
        this.isApproved = false;
    }

    private static String generateRequestId() {
        String date = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "WDR-" + date + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public String getRequestId() {
        return requestId;
    }

    public BTOApplication getApplication() {
        return application;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public String getProcessedBy() {
        return processedBy;
    }

    public void approve(String processedBy) {
        this.isApproved = true;
        this.processedAt = LocalDateTime.now();
        this.processedBy = processedBy;
        // Update application status to UNSUCCESSFUL
        this.application.setStatus(BTOApplicationStatus.UNSUCCESSFUL);
    }

    public void reject(String processedBy) {
        this.isApproved = false;
        this.processedAt = LocalDateTime.now();
        this.processedBy = processedBy;
    }
} 