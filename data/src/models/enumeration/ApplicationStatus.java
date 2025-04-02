// File: bto_management_system/model/enumeration/ApplicationStatus.java
package bto_management_system.model.enumeration;

/**
 * Represents the status of a BTO application or registration
 */
public enum ApplicationStatus {
    PENDING,           // No conclusive decision yet
    SUCCESSFUL,        // Application successful, can book flat
    UNSUCCESSFUL,      // Application unsuccessful
    BOOKED             // Flat has been booked
}

// File: bto_management_system/model/enumeration/MaritalStatus.java
package bto_management_system.model.enumeration;

/**
 * Represents the marital status of a user
 */
public enum MaritalStatus {
    SINGLE,
    MARRIED
}

// File: bto_management_system/model/enumeration/FlatType.java
package bto_management_system.model.enumeration;

/**
 * Represents the type of flat available in BTO projects
 */
public enum FlatType {
    TWO_ROOM,
    THREE_ROOM
}