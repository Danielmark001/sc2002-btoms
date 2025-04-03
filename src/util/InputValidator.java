package util;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Utility class for common input validation
 */
public class InputValidator {
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Z]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{8}$"); // Singapore phone number format
    
    /**
     * Validate NRIC format
     * @param nric NRIC to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidNRIC(String nric) {
        return nric != null && NRIC_PATTERN.matcher(nric).matches();
    }
    
    /**
     * Validate non-empty string
     * @param value String to validate
     * @param message Error message if invalid
     * @throws IllegalArgumentException if value is null or empty
     */
    public static void validateNonEmpty(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Validate string length
     * @param value String to validate
     * @param minLength Minimum allowed length
     * @param maxLength Maximum allowed length
     * @param message Error message if invalid
     * @throws IllegalArgumentException if value length is outside the allowed range
     */
    public static void validateLength(String value, int minLength, int maxLength, String message) {
        if (value == null || value.length() < minLength || value.length() > maxLength) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Validate numeric range
     * @param value Value to validate
     * @param minValue Minimum allowed value
     * @param maxValue Maximum allowed value
     * @param message Error message if invalid
     * @throws IllegalArgumentException if value is outside the allowed range
     */
    public static void validateRange(int value, int minValue, int maxValue, String message) {
        if (value < minValue || value > maxValue) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number format
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validate date range
     * @param startDate Start date
     * @param endDate End date
     * @param message Error message if invalid
     * @throws IllegalArgumentException if end date is before start date
     */
    public static void validateDateRange(LocalDate startDate, LocalDate endDate, String message) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Validate future date
     * @param date Date to validate
     * @param message Error message if invalid
     * @throws IllegalArgumentException if date is in the past
     */
    public static void validateFutureDate(LocalDate date, String message) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(message);
        }
    }
    
    /**
     * Sanitize input string by removing potentially dangerous characters
     * @param input Input string to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove HTML tags and characters that could be used for injection
        String sanitized = input.replaceAll("<[^>]*>", "")
                               .replaceAll("&", "&amp;")
                               .replaceAll("<", "&lt;")
                               .replaceAll(">", "&gt;")
                               .replaceAll("\"", "&quot;")
                               .replaceAll("'", "&#x27;")
                               .replaceAll("/", "&#x2F;");
        
        return sanitized.trim();
    }
}
