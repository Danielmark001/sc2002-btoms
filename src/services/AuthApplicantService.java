package services;

import java.util.Map;

import models.Applicant;
import stores.AuthStore;
import stores.DataStore;

/**
 * The {@link AuthApplicantService} class extends {@link AuthService} and
 * provides the login functionality for applicants.
 */
public class AuthApplicantService extends AuthService {
    /**
     * Constructs an instance of the {@link AuthApplicantService} class.
     */
    public AuthApplicantService() {
    };

    @Override
    public boolean login(String nric, String password) {
        Map<String, Applicant> applicantData = DataStore.getApplicantsData();

        Applicant applicant = applicantData.get(nric);

        if (authenticate(applicant, password) == false)
            return false;

        AuthStore.setCurrentUser(applicant);
        return true;
    }

    /**
     * Registers a new applicant with the given details.
     * 
     * @param name The name of the applicant
     * @param nric The NRIC of the applicant
     * @param age The age of the applicant
     * @param maritalStatus The marital status of the applicant
     * @param password The password for the applicant
     * @return true if registration was successful, false otherwise
     */
    public boolean register(String name, String nric, int age, enumeration.MaritalStatus maritalStatus, String password) {
        Map<String, Applicant> applicantData = DataStore.getApplicantsData();
        
        // Check if applicant already exists
        if (applicantData.containsKey(nric)) {
            return false;
        }
        
        // Create new applicant with provided values
        Applicant newApplicant = new Applicant(
            name,
            nric,
            age,
            maritalStatus,
            password
        );
        
        // Add to data store
        applicantData.put(nric, newApplicant);
        
        // Save changes
        DataStore.saveData();
        
        return true;
    }

}