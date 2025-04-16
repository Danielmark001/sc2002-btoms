package controllers;

import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import interfaces.IAuthService;

import services.AuthApplicantService;
import services.AuthHDBOfficerService;
import services.AuthHDBManagerService;
import utils.TextDecorationUtils;
import stores.AuthStore;
import stores.DataStore;

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

    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static int failedAttempts = 0;
    private static final String FAILED_LOGIN_LOG = "data/FailedLoginAttempts.csv";

    private static AuthApplicantService authApplicantService = new AuthApplicantService();
    private static AuthHDBOfficerService authHDBOfficerService = new AuthHDBOfficerService();
    private static AuthHDBManagerService authHDBManagerService = new AuthHDBManagerService();

    /**
     * Private constructor to prevent instantiation of the class.
     */
    private AuthController() {
    };

    /**
     * Logs a failed login attempt to the CSV file
     */
    private static void logFailedAttempt(String nric, String roleAttempted) {
        try {
            FileWriter writer = new FileWriter(FAILED_LOGIN_LOG, true);
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String ipAddress = "127.0.0.1"; // In a real system, this would be the actual IP
            
            writer.append(timestamp)
                  .append(",")
                  .append(nric)
                  .append(",")
                  .append(roleAttempted)
                  .append(",")
                  .append(ipAddress)
                  .append("\n");
            
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.err.println("Error logging failed login attempt: " + e.getMessage());
        }
    }

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
                System.out.println("\n==========================================");
                System.out.println(TextDecorationUtils.boldText("BTO Management System"));
                System.out.println("==========================================");
                System.out.println(TextDecorationUtils.underlineText("LOGIN"));
                System.out.println("1. Applicant");
                System.out.println("2. HDB Officer");
                System.out.println("3. HDB Manager");
                System.out.println();
                System.out.println(TextDecorationUtils.underlineText("REGISTER"));
                System.out.println("4. New Applicant");
                System.out.println();
                System.out.println(TextDecorationUtils.underlineText("EXIT"));
                System.out.println("0. Shutdown System");
                System.out.println("==========================================");
                System.out.print("Enter your choice: ");

                String input = sc.nextLine();
                if (input.matches("[0-9]+")) {
                    choice = Integer.parseInt(input);
                    if (choice < 0 || choice > 4) {
                        System.out.println("Invalid input. Please enter 0-4!");
                    } else {
                        break;
                    }
                } else {
                    System.out.println("Invalid input. Please enter an integer.\n");
                }
            }

            if (choice == 0) {
                System.out.println("Shutting down BTOMS...");
                DataStore.saveData();
                System.exit(0);
                return;
            }

            if (choice == 4) {
                registerApplicant();
                continue;
            }

            System.out.println("\n==========================================");
            System.out.println(TextDecorationUtils.boldText("Login"));
            System.out.println("==========================================");
            System.out.print("Enter NRIC: ");
            String nric = sc.nextLine();

            if (!isValidNRIC(nric)) {
                System.out.println("Invalid NRIC format! NRIC must start with S or T, followed by 7 digits and end with a letter.\n");
                failedAttempts++;
                if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                    System.out.println("\n==========================================");
                    System.out.println(TextDecorationUtils.boldText("Maximum login attempts reached!"));
                    System.out.println("System shutting down for security reasons.");
                    System.out.println("==========================================");
                    System.exit(0);
                }
                continue;
            }

            if (!isNRICExists(nric, choice)) {
                System.out.println("Invalid NRIC! This NRIC is not registered in the system.\n");
                failedAttempts++;
                if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                    System.out.println("\n==========================================");
                    System.out.println(TextDecorationUtils.boldText("Maximum login attempts reached!"));
                    System.out.println("System shutting down for security reasons.");
                    System.out.println("==========================================");
                    System.exit(0);
                }
                continue;
            }

            System.out.print("Enter Password: ");
            String password = sc.nextLine();

            boolean loginSuccess = false;
            String roleAttempted = "";
            switch (choice) {
                case 1:
                    loginSuccess = authApplicantService.login(nric, password);
                    roleAttempted = "Applicant";
                    break;
                case 2:
                    loginSuccess = authHDBOfficerService.login(nric, password);
                    roleAttempted = "HDB Officer";
                    break;
                case 3:
                    loginSuccess = authHDBManagerService.login(nric, password);
                    roleAttempted = "HDB Manager";
                    break;
            }

            if (loginSuccess) {
                System.out.println("\n==========================================");
                System.out.println(TextDecorationUtils.boldText("Login successful!"));
                System.out.println("==========================================");
                failedAttempts = 0;
                startUserSession();
                return;
            } else {
                failedAttempts++;
                logFailedAttempt(nric, roleAttempted);
                
                System.out.println("\n==========================================");
                System.out.println(TextDecorationUtils.boldText("Login failed!"));
                System.out.println("Invalid password.");
                
                if (failedAttempts >= MAX_LOGIN_ATTEMPTS) {
                    System.out.println("\nMaximum login attempts reached!");
                    System.out.println("System shutting down for security reasons.");
                    System.out.println("==========================================");
                    System.exit(0);
                } else {
                    System.out.println("Attempts remaining: " + (MAX_LOGIN_ATTEMPTS - failedAttempts));
                }
                System.out.println("==========================================");
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

    /**
     * Validates if the given NRIC follows the correct format:
     * - Starts with S or T
     * - Followed by 7 digits
     * - Ends with a letter
     * 
     * @param nric The NRIC to validate
     * @return true if the NRIC is valid, false otherwise
     */
    private static boolean isValidNRIC(String nric) {
        if (nric == null || nric.length() != 9) {
            return false;
        }
        
        // Check first character is S or T
        char firstChar = nric.charAt(0);
        if (firstChar != 'S' && firstChar != 'T') {
            return false;
        }
        
        // Check middle 7 characters are digits
        for (int i = 1; i < 8; i++) {
            if (!Character.isDigit(nric.charAt(i))) {
                return false;
            }
        }
        
        // Check last character is a letter
        char lastChar = nric.charAt(8);
        if (!Character.isLetter(lastChar)) {
            return false;
        }
        
        return true;
    }

    private static boolean isNRICExists(String nric, int role) {
        switch (role) {
            case 1: // Applicant
                return DataStore.getApplicantsData().containsKey(nric);
            case 2: // HDB Officer
                return DataStore.getHDBOfficersData().containsKey(nric);
            case 3: // HDB Manager
                return DataStore.getHDBManagersData().containsKey(nric);
            default:
                return false;
        }
    }

    /**
     * Handles the registration process for new applicants.
     * Prompts the user for NRIC and password, validates them,
     * and creates a new applicant account if valid.
     */
    private static void registerApplicant() {
        System.out.println("\n=== Applicant Registration ===");
        
        String name, nric, password;
        int age;
        enumeration.MaritalStatus maritalStatus;
        
        System.out.print("Name: ");
        name = sc.nextLine();
        
        System.out.print("NRIC: ");
        nric = sc.nextLine();
        
        // Validate NRIC format
        if (!isValidNRIC(nric)) {
            System.out.println("Invalid NRIC format! NRIC must start with S or T, followed by 7 digits and end with a letter.\n");
            return;
        }
        
        System.out.print("Age: ");
        try {
            age = Integer.parseInt(sc.nextLine());
            if (age < 21) {
                System.out.println("Age must be at least 21 years old.\n");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid age. Please enter a number.\n");
            return;
        }
        
        System.out.println("Marital Status:");
        System.out.println("1. Single");
        System.out.println("2. Married");
        System.out.print("Enter choice (1-2): ");
        String maritalChoice = sc.nextLine();
        
        switch (maritalChoice) {
            case "1":
                maritalStatus = enumeration.MaritalStatus.SINGLE;
                break;
            case "2":
                maritalStatus = enumeration.MaritalStatus.MARRIED;
                break;
            default:
                System.out.println("Invalid choice. Please enter 1 or 2.\n");
                return;
        }
        
        System.out.print("Password: ");
        password = sc.nextLine();
        
        // Validate password
        if (password.length() < 6) {
            System.out.println("Password must be at least 6 characters long.\n");
            return;
        }
        
        // Create new AuthApplicantService instance for registration
        AuthApplicantService applicantService = new AuthApplicantService();
        
        // Attempt to register the new applicant
        if (applicantService.register(name, nric, age, maritalStatus, password)) {
            System.out.println("Registration successful! You can now login with your credentials.\n");
        } else {
            System.out.println("Registration failed. The NRIC might already be registered.\n");
        }
    }

    private static void startUserSession() {
        // Start the appropriate controller based on user type
        if (AuthStore.getCurrentUser() instanceof models.Applicant) {
            new ApplicantController().start();
        } else if (AuthStore.getCurrentUser() instanceof models.HDBOfficer) {
            new HDBOfficerController().start();
        } else if (AuthStore.getCurrentUser() instanceof models.HDBManager) {
            new HDBManagerController((models.HDBManager) AuthStore.getCurrentUser()).start();
        }
    }
}