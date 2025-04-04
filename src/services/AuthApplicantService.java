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

}