package enumeration;

/**
 * Enumeration representing the possible statuses of an officer registration  
 */
public enum RegistrationStatus {
    /**
     * Initial status of registration
     */
    PENDING("Pending"),    
    
    /**
     * Registration approved by HDB Manager
     */
    APPROVED("Approved"),   
    
    /**
     * Registration rejected by HDB Manager
     */
    REJECTED("Rejected");    
    
    private final String displayName;
    
    /**
     * Constructor
     * @param displayName Display name of the status
     */
    RegistrationStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of the status
     * @return Display name  
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Returns the display name when converted to string
     * @return Display name
     */  
    @Override
    public String toString() {
        return displayName;  
    }
}