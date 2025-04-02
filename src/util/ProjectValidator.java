package util;

import exceptions.ProjectValidationException;
import models.BTOProject;
import models.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Comprehensive validator for BTO Project-related validations
 */
public class ProjectValidator {
    // Constants for validation
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_NEIGHBORHOOD_LENGTH = 100;
    private static final int MAX_OFFICER_SLOTS = 10;
    private static final int MIN_TOTAL_UNITS = 1;

    // Regex for project name validation
    private static final Pattern PROJECT_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s-]{3,100}$");

    /**
     * Validate project name
     * @param projectName Name to validate
     * @throws ProjectValidationException if name is invalid
     */
    public static void validateProjectName(String projectName) {
        if (projectName == null ||
                projectName.trim().isEmpty() ||
                projectName.length() > MAX_NAME_LENGTH ||
                !PROJECT_NAME_PATTERN.matcher(projectName).matches()) {
            throw ProjectValidationException.invalidProjectName(projectName);
        }
    }

    /**
     * Validate neighborhood
     * @param neighborhood Neighborhood to validate
     * @throws ProjectValidationException if neighborhood is invalid
     */
    public static void validateNeighborhood(String neighborhood) {
        if (neighborhood == null ||
                neighborhood.trim().isEmpty() ||
                neighborhood.length() > MAX_NEIGHBORHOOD_LENGTH) {
            throw ProjectValidationException.invalidNeighborhood(neighborhood);
        }
    }

    /**
     * Validate project dates
     * @param openingDate Project opening date
     * @param closingDate Project closing date
     * @throws ProjectValidationException if dates are invalid
     */
    public static void validateProjectDates(LocalDate openingDate, LocalDate closingDate) {
        if (openingDate == null ||
                closingDate == null ||
                openingDate.isAfter(closingDate) ||
                openingDate.isBefore(LocalDate.now())) {
            throw ProjectValidationException.invalidDates();
        }
    }

    /**
     * Validate project units
     * @param units Map of units for different flat types
     * @throws ProjectValidationException if units are invalid
     */
    public static void validateUnits(Map<String, Integer> units) {
        if (units == null || units.isEmpty()) {
            throw ProjectValidationException.insufficientUnits();
        }

        int totalUnits = units.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        if (totalUnits < MIN_TOTAL_UNITS) {
            throw ProjectValidationException.insufficientUnits();
        }
    }

    /**
     * Validate officer slots
     * @param officerSlots Number of officer slots
     * @throws ProjectValidationException if slots are invalid
     */
    public static void validateOfficerSlots(int officerSlots) {
        if (officerSlots < 0 || officerSlots > MAX_OFFICER_SLOTS) {
            throw ProjectValidationException.officerSlotLimitExceeded();
        }
    }

    /**
     * Check for duplicate project name
     * @param projectName Name to check
     * @param existingProjects List of existing projects
     * @throws ProjectValidationException if project name already exists
     */
    public static void checkDuplicateProjectName(
            String projectName,
            List<BTOProject> existingProjects) {
        if (existingProjects.stream()
                .anyMatch(p -> p.getProjectName().equalsIgnoreCase(projectName))) {
            throw ProjectValidationException.duplicateProject(projectName);
        }
    }

    /**
     * Validate user's authorization to perform project action
     * @param user User attempting the action
     * @param requiredRole Required user role
     * @throws ProjectValidationException if user is not authorized
     */
    public static void validateUserAuthorization(User user, String requiredRole) {
        // Implement role-based authorization check
        // This is a placeholder and should be implemented based on your specific authorization logic
        if (user == null || !hasRequiredRole(user, requiredRole)) {
            throw ProjectValidationException.unauthorizedAction();
        }
    }

    /**
     * Check if user has the required role
     * @param user User to check
     * @param requiredRole Required role
     * @return true if user has the required role, false otherwise
     */
    private static boolean hasRequiredRole(User user, String requiredRole) {
        // Implement actual role checking logic
        return true; // Placeholder
    }

    /**
     * Comprehensive project validation method
     * @param project Project to validate
     * @throws ProjectValidationException if any validation fails
     */
    public static void validateProject(BTOProject project) {
        validateProjectName(project.getProjectName());
        validateNeighborhood(project.getNeighborhood());
        validateProjectDates(
                project.getApplicationOpeningDate(),
                project.getApplicationClosingDate());
        validateOfficerSlots(project.getAvailableHDBOfficerSlots());
    }

    /**
     * Validate project visibility
     * @param project Project to validate
     * @throws ProjectValidationException if project is not visible
     */
    public static void validateProjectVisibility(BTOProject project) {
        if (!project.isVisible()) {
            throw ProjectValidationException.projectNotVisible(project.getProjectName());
        }

    }
}