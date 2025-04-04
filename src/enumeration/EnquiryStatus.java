package enumeration;

/**
 * Enumeration representing enquiry statuses
 */

public enum EnquiryStatus {
    /**
     * Submitted status
     */
    SUBMITTED("Submitted"),
    
    /**
     * Responded status
     */
    RESPONDED("Responded"),

    /**
     * Closed status
     */
    CLOSED("Closed");
    
    private final String displayName;
    
    /**
     * Constructor
     * @param displayName Display name of the enquiry status
     */
    EnquiryStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of the enquiry status
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