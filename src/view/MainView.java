// In MainView.java
package view;

import controllers.LoginController;
import stores.AuthStore;

public class MainView {
    private LoginController loginController;
    private LoginView loginView;
    private MainMenuView mainMenuView;

    public MainView() {
        this.loginController = LoginController.getInstance();
        this.loginView = new LoginView(loginController);
        this.mainMenuView = new MainMenuView(null);
    }

    public void start() {
        // Display splash screen
        CommonView.printSplashScreen();
        
        boolean continueRunning = true;
        
        while (continueRunning) {
            if (!AuthStore.isLoggedIn()) {
                // Display login/register options and handle user selection
                continueRunning = loginView.handleMainMenu();
            } else {
                // User is logged in, show main menu
                mainMenuView.displayMainMenu();
                mainMenuView.handleUserInput();
            }
        }
        
        // Clean up resources
        CommonView.closeScanner();
    }
}