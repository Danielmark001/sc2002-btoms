// File: bto_management_system/util/NRICValidator.java
package util;

import java.util.regex.Pattern;

/**
 * Utility class for validating NRIC numbers
 */
public class NRICValidator {
    // NRIC format: S or T followed by 7 digits and a letter
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Z]$");
    
    /**
     * Validates an NRIC number
     * 
     * @param nric NRIC to validate
     * @return true if NRIC is valid
     */
    public static boolean isValid(String nric) {
        if (nric == null || nric.length() != 9) {
            return false;
        }

        return NRIC_PATTERN.matcher(nric).matches();
    }
    
    public static boolean isValidNRIC(String nric) {
        return isValid(nric);
    }
    
    
}

// File: bto_management_system/util/DataLoader.java
