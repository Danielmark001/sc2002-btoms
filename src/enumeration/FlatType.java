package enumeration;

/**
 * Enumeration representing the types of BTO flats available
 */
public enum FlatType {
    /**
     * 2-Room flat
     */
    TWO_ROOM("2-Room"),
    
    /**
     * 3-Room flat
     */
    THREE_ROOM("3-Room");

    private final String displayName;
    
    /**
     * Constructor
     * @param displayName Display name of the flat type
     */
    FlatType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of the flat type
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