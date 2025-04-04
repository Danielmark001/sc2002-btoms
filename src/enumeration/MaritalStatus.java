package enumeration;

/**
 * Enumeration representing marital statuses for BTO applicants
 */
public enum MaritalStatus {
    /**
     * Single status
     */
    SINGLE("Single"),
    
    /**
     * Married status
     */
    MARRIED("Married");
    
    private final String displayName;
    
    /**
     * Constructor
     * @param displayName Display name of the marital status
     */
    MaritalStatus(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of the marital status
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