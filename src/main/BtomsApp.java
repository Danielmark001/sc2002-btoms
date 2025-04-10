package main;

import controllers.AuthController;
import controllers.ApplicantController;
import controllers.HDBOfficerController;
import controllers.HDBManagerController;
import models.User;
import services.CsvDataService;
import stores.AuthStore;
import stores.DataStore;
import utils.FilePathsUtils;
import view.CommonView;

/**
 * The main class responsible for running the BTOMS application.
 * 
 * <p>
 * This class handles initializing of the data store, authentication for users
 * to log in, and starting the appropriate session based on the role of the
 * logged-in user.
 * </p>
 */
public class BtomsApp {
	/**
	 * Private constructor to prevent instantiation of the class.
	 */
	private BtomsApp() {
	}

	/**
	 * The entry point for the BTOMS application. This method is responsible for
	 * running an infinite loop to allow multiple users to operate the application.
	 * 
	 * @param args an array of String arguments passed to this method
	 */
	public static void main(String[] args) {
		// Add shutdown hook to handle Ctrl+C
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("\nBTOMS is shutting down...");
			DataStore.saveData();
			AuthController.endSession();
		}));

		try {
			do {
				// Initialize DataStore
				DataStore.initDataStore(new CsvDataService(), FilePathsUtils.csvFilePaths());

				// Display Splash Screen
				CommonView.printSplashScreen();

				// Authentication - Log In
				AuthController.startSession();
				if (!AuthStore.isLoggedIn())
					return;

				// Start session
				User user = AuthStore.getCurrentUser();
				switch (user.getUserType()) {
					case APPLICANT:
						new ApplicantController().start();
						break;
					case HDB_OFFICER:
						new HDBOfficerController().start();
						break;
					case HDB_MANAGER:
						new HDBManagerController((models.HDBManager)user).start();
						break;
				}
			} while (true);
		} catch (Exception e) {
			// Save Data and Logout user
			DataStore.saveData();
			AuthController.endSession();

			// Print message
			System.out.println("BTOMS crashed. Please restart the system.");
			System.out.println("Error: " + e.getMessage());
		}
	}
}