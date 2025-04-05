package enumeration;

/**
 * Enumeration representing the possible statuses of a BTO application
 */
public enum BTOApplicationStatus {
    /**
     * Initial status upon application submission
     */
    PENDING("Pending"),
    
    /**
     * Application has been approved
     */
    SUCCESSFUL("Successful"),
    
    /**
     * Application has been rejected
     */
    UNSUCCESSFUL("Unsuccessful"),
    
    /**
     * Flat has been booked after successful application
     */
    BOOKED("Booked");
    
    private final String displayName;
    
    /**
     * Constructor
     * @param displayName Display name of the status
     */
    BTOApplicationStatus(String displayName) {
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