package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Centralized error handling utility for the BTO Management System
 */
public class ErrorHandler {
    // Logger for system-wide error tracking
    private static final Logger SYSTEM_LOGGER = Logger.getLogger(ErrorHandler.class.getName());

    /**
     * Log a critical error with full stack trace
     * 
     * @param error Exception to log
     * @param context Additional context information
     */
    public static void logError(Throwable error, String context) {
        // Prepare detailed error message
        String errorMessage = prepareErrorMessage(error, context);
        
        // Log to system logger
        SYSTEM_LOGGER.log(Level.SEVERE, errorMessage, error);
        
        // Optional: Print to console for immediate visibility
        System.err.println(errorMessage);
    }

    /**
     * Prepare comprehensive error message
     * 
     * @param error Exception to process
     * @param context Additional context information
     * @return Formatted error message
     */
    private static String prepareErrorMessage(Throwable error, String context) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        pw.println("=== Error Report ===");
        pw.println("Timestamp: " + LocalDateTime.now());
        
        if (context != null && !context.isEmpty()) {
            pw.println("Context: " + context);
        }
        
        pw.println("Error Type: " + error.getClass().getName());
        pw.println("Error Message: " + error.getMessage());
        
        pw.println("\n--- Stack Trace ---");
        error.printStackTrace(pw);
        
        // Include cause if exists
        if (error.getCause() != null) {
            pw.println("\n--- Root Cause ---");
            error.getCause().printStackTrace(pw);
        }
        
        return sw.toString();
    }

    /**
     * Handle unexpected errors with optional recovery
     * 
     * @param error Unexpected error
     * @param recoveryAction Optional recovery action
     * @return true if recovery was successful, false otherwise
     */
    public static boolean handleUnexpectedError(Throwable error, Runnable recoveryAction) {
        // Log the error
        logError(error, "Attempting recovery");
        
        try {
            // Attempt recovery if provided
            if (recoveryAction != null) {
                recoveryAction.run();
                return true;
            }
            
            return false;
        } catch (Exception recoveryError) {
            // Log recovery failure
            logError(recoveryError, "Recovery attempt failed");
            return false;
        }
    }

    /**
     * Safely execute a task with error handling
     * 
     * @param task Task to execute
     * @param errorHandler Custom error handler
     */
    public static void safeExecute(Runnable task, ErrorConsumer errorHandler) {
        try {
            task.run();
        } catch (Exception e) {
            if (errorHandler != null) {
                errorHandler.accept(e);
            } else {
                logError(e, "Unhandled error in safe execution");
            }
        }
    }

    /**
     * Functional interface for error handling
     */
    @FunctionalInterface
    public interface ErrorConsumer {
        /**
         * Handle an error
         * 
         * @param error Error to handle
         */
        void accept(Throwable error);
    }

    /**
     * Validate and sanitize input to prevent potential injection or security risks
     * 
     * @param input Input to sanitize
     * @return Sanitized input
     */
    public static String sanitizeInput(String input) {
        if (input == null) return null;
        
        // Remove potential script tags
        input = input.replaceAll("<script.*?</script>", "", java.util.regex.Pattern.DOTALL | java.util.regex.Pattern.CASE_INSENSITIVE);
        
        // Remove HTML tags
        input = input.replaceAll("<[^>]*>", "");
        
        // Trim and limit length
        input = input.trim();
        
        // Optional: Limit input length
        return input.length() > 1000 ? input.substring(0, 1000) : input;
    }

    /**
     * Generate a user-friendly error message
     * 
     * @param error Exception to process
     * @return User-friendly error message
     */
    public static String getUserFriendlyErrorMessage(Throwable error) {
        if (error == null) {
            return "An unknown error occurred.";
        }
        
        // Provide more specific messages for known exception types
        if (error instanceof IllegalArgumentException) {
            return "Invalid input: " + error.getMessage();
        }
        
        if (error instanceof SecurityException) {
            return "Access denied: " + error.getMessage();
        }
        
        // Generic error message for unexpected errors
        return "An error occurred. Please try again or contact support.";
    }

    /**
     * Private constructor to prevent instantiation
     */
    private ErrorHandler() {
        throw new AssertionError("Cannot be instantiated");
    }
}