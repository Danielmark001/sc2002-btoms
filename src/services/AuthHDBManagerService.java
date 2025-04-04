package services;

import java.util.Map;

import models.HDBManager;
import stores.AuthStore;
import stores.DataStore;

/**
 * The {@link AuthHDBManagerService} class extends {@link AuthService} and
 * provides the login functionality for HDB managers.
 */
public class AuthHDBManagerService extends AuthService {
    /**
     * Constructs an instance of the {@link AuthHDBManagerService} class.
     */
    public AuthHDBManagerService() {
    };

    @Override
    public boolean login(String nric, String password) {
        Map<String, HDBManager> hdbManagerData = DataStore.getHDBManagersData();

        HDBManager hdbManager = hdbManagerData.get(nric);

        if (authenticate(hdbManager, password) == false)
            return false;

        AuthStore.setCurrentUser(hdbManager);
        return true;
    }

}