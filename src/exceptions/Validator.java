/**
 * Comprehensive Validator Utility
 */
package exceptions;
import java.time.LocalDate;
import enumeration.MaritalStatus;
public class Validator {
    /**
     * Validate NRIC format
     * @param nric NRIC to validate
     * @throws UserValidationException if NRIC is invalid
     */
    public static void validateNRIC(String nric) {
        if (nric == null || !nric.matches("^[ST]\\d{7}[A-Z]$")) {
            throw new UserValidationException(
                "Invalid NRIC format. Must start with S or T, followed by 7 digits, and end with a letter.",
                BTOSystemException.ErrorCode.VALIDATION_ERROR
            );
        }
    }

    /**
     * Validate password strength
     * @param password Password to validate
     * @throws UserValidationException if password is weak
     */
    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new UserValidationException(
                "Password must be at least 8 characters long.",
                BTOSystemException.ErrorCode.VALIDATION_ERROR
            );
        }

        if (!password.matches("^(?=.*[A-Z])(?=.*\\d).*$")) {
            throw new UserValidationException(
                "Password must contain at least one uppercase letter and one digit.",
                BTOSystemException.ErrorCode.VALIDATION_ERROR
            );
        }
    }

    /**
     * Validate project dates
     * @param openingDate Project opening date
     * @param closingDate Project closing date
     * @throws ProjectValidationException if dates are invalid
     */
    public static void validateProjectDates(LocalDate openingDate, LocalDate closingDate) {
        if (openingDate == null || closingDate == null) {
            throw new ProjectValidationException(
                "Project dates cannot be null.",
                BTOSystemException.ErrorCode.INVALID_PROJECT_DATES
            );
        }

        if (openingDate.isAfter(closingDate)) {
            throw new ProjectValidationException(
                "Project opening date must be before closing date.",
                BTOSystemException.ErrorCode.INVALID_PROJECT_DATES
            );
        }

        if (openingDate.isBefore(LocalDate.now())) {
            throw new ProjectValidationException(
                "Project opening date cannot be in the past.",
                BTOSystemException.ErrorCode.INVALID_PROJECT_DATES
            );
        }
    }

    /**
     * Validate applicant age and marital status
     * @param age Applicant's age
     * @param maritalStatus Applicant's marital status
     * @throws UserValidationException if age or marital status is invalid
     */
    public static void validateApplicantEligibility(int age, MaritalStatus maritalStatus) {
        if (maritalStatus == MaritalStatus.SINGLE && age < 35) {
            throw new UserValidationException(
                "Single applicants must be at least 35 years old.",
                BTOSystemException.ErrorCode.INELIGIBLE_FOR_APPLICATION
            );
        }

        if (maritalStatus == MaritalStatus.MARRIED && age < 21) {
            throw new UserValidationException(
                "Married applicants must be at least 21 years old.",
                BTOSystemException.ErrorCode.INELIGIBLE_FOR_APPLICATION
            );
        }
    }
}