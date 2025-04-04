package interfaces;

/**
 * The {@link IAuthService} interface defines a contract for managing
 * authentication services, such as login and logout.
 */
public interface IAuthService {
    /**
     * Logs in a user with the specified user ID and password.
     *
     * @param nric   the ID of the user
     * @param password the user's password
     * @return true if the login was successful, false otherwise
     */
    public boolean login(String nric, String password);

    /**
     * Logs out the currently logged in user.
     *
     * @return true if the logout was successful, false otherwise
     */
    public boolean logout();
}