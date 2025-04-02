package interfaces;

import models.*;
import enumeration.*;

import java.util.List;

public interface IRegistrationService {
    /**
     * Register an HDB Officer for a project
     * 
     * @param officer The HDB Officer to register
     * @param project The project to register for
     * @return Created Registration object
     * @throws IllegalArgumentException if inputs are invalid
     * @throws IllegalStateException if registration is not eligible
     */
    Registration registerOfficer(HDBOfficer officer, BTOProject project);

    /**
     * Approve a pending registration by an HDB Manager
     * 
     * @param registration The registration to approve
     * @param manager The HDB Manager approving the registration
     * @return true if approval is successful, false otherwise
     */
    boolean approveRegistration(Registration registration, HDBManager manager);

    /**
     * Reject a pending registration by an HDB Manager
     * 
     * @param registration The registration to reject
     * @param manager The HDB Manager rejecting the registration
     * @return true if rejection is successful, false otherwise
     */
    boolean rejectRegistration(Registration registration, HDBManager manager);

    /**
     * Get all registrations for a specific project
     * 
     * @param project The project to get registrations for
     * @return List of registrations for the project
     */
    List<Registration> getRegistrationsByProject(BTOProject project);

    /**
     * Get registrations by their status
     * 
     * @param status The registration status to filter by
     * @return List of registrations with the specified status
     */
    List<Registration> getRegistrationsByStatus(Registration.RegistrationStatus status);

    /**
     * Get all registrations for a specific HDB Officer
     * 
     * @param officer The HDB Officer to get registrations for
     * @return List of registrations for the officer
     */
    List<Registration> getRegistrationsByOfficer(HDBOfficer officer);
}