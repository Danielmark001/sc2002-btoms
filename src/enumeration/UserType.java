// Fix for enumeration/UserType.java
package enumeration;

/**
 * Enumeration representing the types of users in the system
 */
public enum UserType {
    /**
     * BTO Applicant user  
     */
    APPLICANT("Applicant"),
    
    /**
     * HDB Officer user
     */  
    HDB_OFFICER("HDB Officer"),
    
    /**
     * HDB Manager user
     */
    HDB_MANAGER("HDB Manager");
    
    private final String displayName;
    
    UserType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}



