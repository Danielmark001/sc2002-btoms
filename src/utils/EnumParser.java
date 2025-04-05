package utils;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;
import enumeration.MaritalStatus;
import enumeration.HDBOfficerRegistrationStatus;

/**
 * Utility class for parsing enum values from display names
 */
public class EnumParser {
    /**
     * Parses a string to a FlatType enum value
     * @param displayName The display name of the flat type (e.g. "3-Room")
     * @return The corresponding FlatType enum value
     * @throws IllegalArgumentException if the display name is invalid
     */
    public static FlatType parseFlatType(String displayName) {
        for (FlatType type : FlatType.values()) {
            if (type.getDisplayName().equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid flat type: " + displayName);
    }

    /**
     * Parses a string to a MaritalStatus enum value
     * @param status The marital status string (can be either enum name or display name)
     * @return The corresponding MaritalStatus enum value
     * @throws IllegalArgumentException if the status is invalid
     */
    public static MaritalStatus parseMaritalStatus(String status) {
        // Convert to uppercase to match enum values
        String upperStatus = status.toUpperCase();
        try {
            return MaritalStatus.valueOf(upperStatus);
        } catch (IllegalArgumentException e) {
            // If exact match fails, try to match by display name
            for (MaritalStatus ms : MaritalStatus.values()) {
                if (ms.getDisplayName().equalsIgnoreCase(status)) {
                    return ms;
                }
            }
            throw new IllegalArgumentException("Invalid marital status: " + status);
        }
    }

    /**
     * Parses a string to a BTOApplicationStatus enum value
     * @param status The status string (can be either enum name or display name)
     * @return The corresponding BTOApplicationStatus enum value
     * @throws IllegalArgumentException if the status is invalid
     */
    public static BTOApplicationStatus parseBTOApplicationStatus(String status) {
        for (BTOApplicationStatus s : BTOApplicationStatus.values()) {
            if (s.getDisplayName().equals(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid BTO application status: " + status);
    }

    public static HDBOfficerRegistrationStatus parseHDBOfficerRegistrationStatus(String status) {
        for (HDBOfficerRegistrationStatus s : HDBOfficerRegistrationStatus.values()) {
            if (s.getDisplayName().equals(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid HDB officer registration status: " + status);
    }
} 