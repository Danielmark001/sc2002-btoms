package controllers;

import java.util.Scanner;

import interfaces.IAuthService;

import services.AuthApplicantService;
import services.AuthHDBOfficerService;
import services.AuthHDBManagerService;

/**
 * The {@link AuthController} class provides utility methods for managing
 * user authentication within the application. It offers methods to start
 * and end user sessions, as well as handle user login and logout. This
 * class utilizes the {@link IAuthService} interface for handling the
 * authentication process.
 */
public class AuthController {
    /**
     * {@link Scanner} object to get input from the user.
     */
    private static final Scanner sc = new Scanner(System.in);

    /**
     * {@link IAuthService} object to authenticate the user.
     */
    private static IAuthService authService;

    /**
     * Private constructor to prevent instantiation of the class.
     */
    private AuthController() {
    };

    /**
     * Starts a user session by prompting the user to select their role and
     * enter their credentials. The method loops until valid credentials are
     * provided or the system is shut down.
     */
    public static void startSession() {
        int choice;
        boolean authenticated = false;

        do {

            while (true) {
                System.out.println("<Enter 0 to shutdown system>\n");
                System.out.println("Login as:");
                System.out.println("1. Applicant");
                System.out.println("2. HDB Officer");
                System.out.println("3. HDB Manager");

                String input = sc.nextLine();

                if (input.matches("[0-9]+")) { // If the input is an integer, proceed with the code
                    choice = Integer.parseInt(input);

                    if (choice < 0 || choice > 3) {
                        System.out.println("Invalid input. Please enter 0-3!");
                    } else {
                        break;
                    }
                } else { // If the input is not an integer, prompt the user to enter again
                    System.out.println("Invalid input. Please enter an integer.\n");
                }

            }

            switch (choice) {
                case 0:
                    System.out.println("Shutting down BTOMS...");
                    return;
                case 1:
                    authService = new AuthApplicantService();
                    break;
                case 2:
                    authService = new AuthHDBOfficerService();
                    break;
                case 3:
                    authService = new AuthHDBManagerService();
                    break;
            }

            String nric, password;

            System.out.print("NRIC: ");
            nric = sc.nextLine();

            System.out.print("Password: ");
            password = sc.nextLine();

            authenticated = authService.login(nric, password);
            if (!authenticated) {
                // We do not specify whether the userID or password is incorrect to make it more
                // secure
                System.out.println("Credentials invalid! Note that NRIC and Password are case-sensitive.\n");
            }
        } while (!authenticated);
    }

    /**
     * Ends the current user session by logging the user out and displaying a
     * logout message.
     */
    public static void endSession() {
        if (authService != null) {
            authService.logout();
        }
        authService = null;
    }
}