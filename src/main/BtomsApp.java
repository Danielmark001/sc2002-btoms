package main;

import controllers.AuthController;
import controllers.ProjectController;
import controllers.UserController;
import models.User;
import services.CsvDataService;
import stores.AuthStore;
import stores.DataStore;
import util.FilePathsUtils;
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
				switch (user.getRole()) {
					case APPLICANT:
						new UserController().start();
						break;
					case OFFICER:
						new ProjectController().start();
						break;
					case MANAGER:
						new ProjectController().start();
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