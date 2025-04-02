package util;

import java.io.File;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Comprehensive Logging Utility for BTO Management System
 */
public class LoggerUtil {
    private static final String LOG_DIRECTORY = "logs";
    private static final String APPLICATION_LOG_FILE = LOG_DIRECTORY + "/application.log";
    private static final String ERROR_LOG_FILE = LOG_DIRECTORY + "/error.log";
    private static final String SECURITY_LOG_FILE = LOG_DIRECTORY + "/security.log";

    // Log levels
    public enum LogLevel {
        INFO, WARNING, ERROR, DEBUG, SECURITY
    }

    // Ensure log directory exists
    static {
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    /**
     * Log a message with specified log level
     * @param level Log level
     * @param message Log message
     * @param className Originating class name
     */
    public static void log(LogLevel level, String message, String className) {
        String logMessage = formatLogMessage(level, message, className);
        
        switch (level) {
            case ERROR:
                writeToFile(ERROR_LOG_FILE, logMessage);
                break;
            case SECURITY:
                writeToFile(SECURITY_LOG_FILE, logMessage);
                break;
            default:
                writeToFile(APPLICATION_LOG_FILE, logMessage);
        }

        // Optional: Console output
        System.out.println(logMessage);
    }

    /**
     * Log an exception with error details
     * @param e Exception to log
     * @param className Originating class name
     */
    public static void logException(Exception e, String className) {
        String errorMessage = formatExceptionMessage(e, className);
        writeToFile(ERROR_LOG_FILE, errorMessage);
        e.printStackTrace(); // Console error trace
    }

    /**
     * Format log message with timestamp and details
     * @param level Log level
     * @param message Log message
     * @param className Originating class name
     * @return Formatted log message
     */
    private static String formatLogMessage(LogLevel level, String message, String className) {
        return String.format(
            "[%s] %s - %s: %s", 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            level,
            className,
            message
        );
    }

    /**
     * Format exception details for logging
     * @param e Exception to format
     * @param className Originating class name
     * @return Formatted exception message
     */
    private static String formatExceptionMessage(Exception e, String className) {
        return String.format(
            "[%s] ERROR - %s: Exception in %s\nMessage: %s\nStack Trace: %s", 
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            e.getClass().getSimpleName(),
            className,
            e.getMessage(),
            getStackTraceAsString(e)
        );
    }

    /**
     * Convert stack trace to string
     * @param e Exception
     * @return Stack trace as string
     */
    private static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Write log message to file
     * @param filePath Log file path
     * @param message Log message
     */
    private static void writeToFile(String filePath, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(message);
        } catch (IOException ioException) {
            // Fallback logging if file writing fails
            System.err.println("Failed to write to log file: " + filePath);
            ioException.printStackTrace();
        }
    }

    /**
     * Security audit log for critical security events
     * @param event Security event description
     * @param userId User identifier
     * @param ipAddress Source IP address
     */
    public static void logSecurityEvent(String event, String userId, String ipAddress) {
        String securityMessage = String.format(
            "[%s] SECURITY - User: %s, IP: %s, Event: %s",
            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            userId,
            ipAddress,
            event
        );
        writeToFile(SECURITY_LOG_FILE, securityMessage);
    }
}