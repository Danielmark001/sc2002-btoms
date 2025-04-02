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
    OFFICER("Officer"),
    
    /**
     * HDB Manager user
     */
    MANAGER("Manager");
    
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



