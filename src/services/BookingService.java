package services;

import models.Application;
import models.BTOProject;
import models.User;
import enumeration.ApplicationStatus;
import enumeration.FlatType;
import stores.DataStore;

import java.time.LocalDate;
import java.util.Optional;

public class BookingService {
    private static BookingService instance;

    private BookingService() {}

    public static synchronized BookingService getInstance() {
        if (instance == null) {
            instance = new BookingService();
        }
        return instance;
    }

    /**
     * Process flat booking for an applicant
     * @param application Application to book flat for
     * @param flatType Flat type to book
     * @param unitNumber Unit number for the flat
     * @return Booked application
     */
    public Application processBooking(Application application, FlatType flatType, String unitNumber) {
        // Validate booking eligibility
        if (!isEligibleForBooking(application)) {
            throw new IllegalStateException("Application is not eligible for booking");
        }

        // Update application status
        application.setStatus(ApplicationStatus.BOOKED);

        // Update project flat availability (placeholder - actual implementation would depend on project tracking)
        updateProjectFlatAvailability(application.getProject(), flatType);

        // Update user profile with booked flat details
        updateUserProfileWithBooking(application.getApplicant(), application, flatType, unitNumber);

        return application;
    }

    /**
     * Check if an application is eligible for booking
     * @param application Application to check
     * @return True if eligible for booking
     */
    public boolean isEligibleForBooking(Application application) {
        return application.getStatus() == ApplicationStatus.SUCCESSFUL;
    }

    /**
     * Update project flat availability
     * @param project Project
     * @param flatType Flat type booked
     */
    private void updateProjectFlatAvailability(BTOProject project, FlatType flatType) {
        // Placeholder method to update flat availability
        // In a real system, this would decrement the count of available flats
    }

    /**
     * Update user profile with booking details
     * @param user User who booked the flat
     * @param application Booked application
     * @param flatType Booked flat type
     * @param unitNumber Booked unit number
     */
    private void updateUserProfileWithBooking(User user, Application application, FlatType flatType, String unitNumber) {
        // Placeholder method to update user profile
        // Could involve setting booked project, flat type, etc.
    }

    /**
     * Generate booking receipt
     * @param application Booked application
     * @return Booking receipt details as a string
     */
    public String generateBookingReceipt(Application application) {
        if (application.getStatus() != ApplicationStatus.BOOKED) {
            throw new IllegalStateException("Cannot generate receipt for non-booked application");
        }

        StringBuilder receipt = new StringBuilder();
        receipt.append("BTO FLAT BOOKING RECEIPT\n");
        receipt.append("------------------------\n");
        receipt.append("Applicant Name: ").append(application.getApplicant().getName()).append("\n");
        receipt.append("NRIC: ").append(application.getApplicant().getNric()).append("\n");
        receipt.append("Age: ").append(application.getApplicant().calculateAge()).append("\n");
        receipt.append("Marital Status: ").append(application.getApplicant().getMaritalStatus()).append("\n");
        receipt.append("Project: ").append(application.getProject().getProjectName()).append("\n");
        receipt.append("Flat Type: ").append(application.getFlatType()).append("\n");
        receipt.append("Booking Date: ").append(LocalDate.now()).append("\n");

        return receipt.toString();
    }

    /**
     * Check if user has already booked a flat
     * @param user User to check
     * @return True if user has booked a flat
     */
}