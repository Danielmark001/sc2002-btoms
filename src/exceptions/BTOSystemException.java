package exceptions;

import enumeration.MaritalStatus;
import java.time.LocalDate;
/**
 * Base custom exception for BTO Management System
 */
public class BTOSystemException extends RuntimeException {
    private ErrorCode errorCode;

    public BTOSystemException(String message) {
        super(message);
    }

    public BTOSystemException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Enum for standardized error codes
     */
    public enum ErrorCode {
        // User-related errors
        INVALID_USER_CREDENTIALS(1000),
        USER_ALREADY_EXISTS(1001),
        INSUFFICIENT_USER_PERMISSIONS(1002),

        // Project-related errors
        PROJECT_ALREADY_EXISTS(2000),
        PROJECT_NOT_FOUND(2001),
        INVALID_PROJECT_DATES(2002),

        // Application-related errors
        INVALID_APPLICATION_STATUS(3000),
        APPLICATION_ALREADY_EXISTS(3001),
        INELIGIBLE_FOR_APPLICATION(3002),

        // Registration-related errors
        REGISTRATION_ALREADY_EXISTS(4000),
        INVALID_REGISTRATION_STATUS(4001),

        // Enquiry-related errors
        ENQUIRY_NOT_MODIFIABLE(5000),

        // General system errors
        INTERNAL_SYSTEM_ERROR(9000),
        VALIDATION_ERROR(9001);

        private final int code;

        ErrorCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    
    public String getDescription() {
        return "Error Code: " + errorCode.getCode() + ", Message: " + getMessage();
    }

    public String getDescriptionWithStackTrace() {
        return "Error Code: " + errorCode.getCode() + ", Message: " + getMessage() + ", Stack Trace: "
                + getStackTrace();
    }

    public String getDescriptionWithStackTraceAndCause() {
        return "Error Code: " + errorCode.getCode() + ", Message: " + getMessage() + ", Cause: " + getCause()
                + ", Stack Trace: " + getStackTrace();
    }
    
}

/**
 * Exception for user-related validation errors
 */




/**
 * Exception for application-related validation errors
 */


