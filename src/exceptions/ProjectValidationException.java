package exceptions;

/**
 * Exception for project-related validation errors
 */
public class ProjectValidationException extends BTOSystemException {
    // Project-specific error codes
    public enum ProjectErrorCode  {
        INVALID_PROJECT_NAME(1000, "Invalid project name"),
        INVALID_NEIGHBORHOOD(1001, "Invalid neighborhood"),
        INVALID_DATES(1002, "Invalid project dates"),
        DUPLICATE_PROJECT(1003, "Project already exists"),
        INSUFFICIENT_UNITS(1004, "Insufficient project units"),
        OFFICER_SLOTS_EXCEEDED(1005, "Maximum officer slots exceeded"),
        UNAUTHORIZED_ACTION(1006, "Unauthorized project action"),
        PROJECT_NOT_VISIBLE(1007, "Project is not visible");

        private final int code;
        private final String description;

        ProjectErrorCode(int code, String description) {
            this.code = code;
            this.description = description;
        }

    }

    // Field to store the error code
    private final ProjectErrorCode errorCode;

    // Constructors
    public ProjectValidationException(String message) {
        super(message);
        this.errorCode = null; // Default to null if no error code is provided
    }

    public ProjectValidationException(String message, ProjectErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }


    // Static factory methods for common validation scenarios
    public static ProjectValidationException invalidProjectName(String projectName) {
        String message = "Invalid project name: '" + projectName + "'. Project name must be non-empty, between 3-100 characters, and contain only alphanumeric characters, spaces, and hyphens.";
        return new ProjectValidationException(message, ProjectErrorCode.INVALID_PROJECT_NAME);
    }

    public static ProjectValidationException invalidNeighborhood(String neighborhood) {
        String message = "Invalid neighborhood: '" + neighborhood + "'. Neighborhood must be non-empty and less than 100 characters.";
        return new ProjectValidationException(message, ProjectErrorCode.INVALID_NEIGHBORHOOD);
    }

    public static ProjectValidationException invalidDates() {
        String message = "Invalid project dates. Opening date must be before closing date and not in the past.";
        return new ProjectValidationException(message, ProjectErrorCode.INVALID_DATES);
    }

    public static ProjectValidationException duplicateProject(String projectName) {
        String message = "A project with the name '" + projectName + "' already exists.";
        return new ProjectValidationException(message, ProjectErrorCode.DUPLICATE_PROJECT);
    }

    public static ProjectValidationException insufficientUnits() {
        String message = "Project must have at least one unit of any flat type.";
        return new ProjectValidationException(message, ProjectErrorCode.INSUFFICIENT_UNITS);
    }

    public static ProjectValidationException officerSlotLimitExceeded() {
        String message = "Number of officer slots must be between 0 and 10.";
        return new ProjectValidationException(message, ProjectErrorCode.OFFICER_SLOTS_EXCEEDED);
    }

    public static ProjectValidationException unauthorizedAction() {
        String message = "You are not authorized to perform this action on the project.";
        return new ProjectValidationException(message, ProjectErrorCode.UNAUTHORIZED_ACTION);
    }

    public static ProjectValidationException projectNotVisible(String projectName) {
        String message = "Project '" + projectName + "' is not currently visible.";
        return new ProjectValidationException(message, ProjectErrorCode.PROJECT_NOT_VISIBLE);
    }

    // Override toString for more detailed error information
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProjectValidationException: ").append(getMessage());

        // Add error code details if available
        if (getErrorCode() != null) {
            sb.append("\nError Code: ").append(getErrorCode().getCode());

        }

        return sb.toString();
    }
    /**
 * Gets the project-specific error code
 * @return Error code
 */
@Override
public BTOSystemException.ErrorCode getErrorCode() {
    return null;
}
}