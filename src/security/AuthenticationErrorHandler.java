package security;

import exceptions.BTOSystemException;
import models.User;
import util.LoggerUtil;
import util.LoggerUtil.LogLevel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive Authentication Error Handler
 */
public class AuthenticationErrorHandler {
    // Track login attempts
    private static final Map<String, LoginAttempt> loginAttempts = new HashMap<>();

    // Maximum allowed failed login attempts
    private static final int MAX_LOGIN_ATTEMPTS = 5;

    // Account lockout duration (in minutes)
    private static final int LOCKOUT_DURATION = 15;

    /**
     * Inner class to track login attempts
     */
    private static class LoginAttempt {
        int failedAttempts;
        LocalDateTime lastAttempt;
        boolean isLocked;

        LoginAttempt() {
            this.failedAttempts = 0;
            this.lastAttempt = LocalDateTime.now();
            this.isLocked = false;
        }
    }

    /**
     * Handle login failure
     * @param username Username of failed login attempt
     * @param ipAddress IP address of the attempt
     * @throws BTOSystemException for authentication failures
     */
    public static void handleLoginFailure(String username, String ipAddress) {
        // Synchronize to prevent race conditions
        synchronized (loginAttempts) {
            LoginAttempt attempt = loginAttempts.computeIfAbsent(username, k -> new LoginAttempt());

        // Check if account is already locked
        if (attempt.isLocked && attempt.lastAttempt.plusMinutes(LOCKOUT_DURATION).isAfter(LocalDateTime.now())) {
            throw new BTOSystemException(
                    "Account is locked. Please try again later.",
                    BTOSystemException.ErrorCode.INSUFFICIENT_USER_PERMISSIONS);
        }

            // Increment failed attempts
            attempt.failedAttempts++;
            attempt.lastAttempt = LocalDateTime.now();

            // Log security event
            LoggerUtil.logSecurityEvent(
                    "Login Failure",
                    username,
                    ipAddress);

            // Check if maximum attempts reached
            if (attempt.failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                lockAccount(username, ipAddress);
                throw new BTOSystemException(
                        "Too many failed login attempts. Account locked for " + LOCKOUT_DURATION + " minutes.",
                        BTOSystemException.ErrorCode.INSUFFICIENT_USER_PERMISSIONS);
            }
        }
    }

    /**
     * Handle successful login
     * @param username Username of successful login
     * @param ipAddress IP address of the login
     */
    public static void handleLoginSuccess(String username, String ipAddress) {
        synchronized (loginAttempts) {
            // Reset login attempts on successful login
            resetLoginAttempts(username);

            // Log security event
            LoggerUtil.logSecurityEvent(
                    "Successful Login",
                    username,
                    ipAddress);
        }
    }

    /**
     * Lock the account after maximum login attempts
     * @param username Username to lock
     * @param ipAddress IP address of the attempt
     */
    private static void lockAccount(String username, String ipAddress) {
        LoginAttempt attempt = loginAttempts.get(username);
        attempt.isLocked = true;
        attempt.lastAttempt = LocalDateTime.now();

        LoggerUtil.log(
                LogLevel.SECURITY,
                "Account locked due to multiple failed login attempts: " + username,
                AuthenticationErrorHandler.class.getName());

        LoggerUtil.logSecurityEvent(
                "Account Locked",
                username,
                ipAddress);
    }

    /**
     * Reset login attempts for a user
     * @param username Username to reset
     */
    private static void resetLoginAttempts(String username) {
        LoginAttempt attempt = loginAttempts.get(username);
        if (attempt != null) {
            attempt.failedAttempts = 0;
            attempt.isLocked = false;
        }
    }
}