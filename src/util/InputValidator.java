package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Comprehensive input validation utility
 * Provides robust validation methods for various input types
 */
public final class InputValidator {
    // Regex patterns
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Z]$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+?6?0?)[1-9]\\d{7,9}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L} .'-]+$");

    // Private constructor to prevent instantiation
    private InputValidator() {
        throw new AssertionError("Cannot be instantiated");
    }

    /**
     * Validate NRIC number
     * @param nric NRIC to validate
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validateNRIC(String nric) {
        if (nric == null || !NRIC_PATTERN.matcher(nric).matches()) {
            throw new IllegalArgumentException("Invalid NRIC format. Must start with S or T, followed by 7 digits, and end with a letter.");
        }
        return true;
    }

    /**
     * Validate email address
     * @param email Email to validate
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return true;
    }

    /**
     * Validate phone number
     * @param phoneNumber Phone number to validate
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        return true;
    }

    /**
     * Validate password
     * @param password Password to validate
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validatePassword(String password) {
        if (password == null || !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new IllegalArgumentException("Password must be at least 8 characters long, contain uppercase, lowercase, number, and special character");
        }
        return true;
    }

    /**
     * Validate name
     * @param name Name to validate
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validateName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 100 || !NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name format");
        }
        return true;
    }

    /**
     * Validate date is not in the future
     * @param date Date to validate
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validatePastOrPresentDate(LocalDate date) {
        if (date == null || date.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
        return true;
    }

    /**
     * Validate date range
     * @param startDate Start date
     * @param endDate End date
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        return true;
    }

    /**
     * Validate that a collection is not null or empty
     * @param collection Collection to validate
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validateCollection(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("Collection cannot be null or empty");
        }
        return true;
    }

    /**
     * Validate that a map is not null or empty
     * @param map Map to validate
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validateMap(Map<?, ?> map) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException("Map cannot be null or empty");
        }
        return true;
    }

    /**
     * Validate that an object is not null
     * @param object Object to validate
     * @param message Custom error message
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validateNotNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message != null ? message : "Object cannot be null");
        }
        return true;
    }

    /**
     * Validate numeric range
     * @param value Numeric value to validate
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return true if valid, throws IllegalArgumentException if invalid
     */
    public static boolean validateNumericRange(Number value, Number min, Number max) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        
        if (value instanceof Integer) {
            int intValue = (Integer) value;
            int minInt = min != null ? (Integer) min : Integer.MIN_VALUE;
            int maxInt = max != null ? (Integer) max : Integer.MAX_VALUE;
            
            if (intValue < minInt || intValue > maxInt) {
                throw new IllegalArgumentException(
                    String.format("Value must be between %d and %d", minInt, maxInt)
                );
            }
        } else if (value instanceof Double) {
            double doubleValue = (Double) value;
            double minDouble = min != null ? (Double) min : Double.MIN_VALUE;
            double maxDouble = max != null ? (Double) max : Double.MAX_VALUE;
            
            if (doubleValue < minDouble || doubleValue > maxDouble) {
                throw new IllegalArgumentException(
                    String.format("Value must be between %f and %f", minDouble, maxDouble)
                );
            }
        }
        
        return true;
    }
}