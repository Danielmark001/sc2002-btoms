package services;

import java.util.Map;

import models.HDBOfficer;
import stores.AuthStore;
import stores.DataStore;

/**
 * The {@link AuthHDBOfficerService} class extends {@link AuthService} and
 * provides the login functionality for HDB officers.
 */
public class AuthHDBOfficerService extends AuthService {
    /**
     * Constructs an instance of the {@link AuthHDBOfficerService} class.
     */
    public AuthHDBOfficerService() {
    };

    @Override
    public boolean login(String nric, String password) {
        Map<String, HDBOfficer> hdbOfficerData = DataStore.getHDBOfficersData();

        HDBOfficer hdbOfficer = hdbOfficerData.get(nric);

        if (authenticate(hdbOfficer, password) == false)
            return false;

        AuthStore.setCurrentUser(hdbOfficer);
        return true;
    }

}