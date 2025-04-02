package view;

import controller.LoginController;
import stores.AuthStore;

public class MainView {
    private LoginController loginController;
    private LoginView loginView;
    private MainMenuView mainMenuView;

    public MainView() {
        this.loginController = new LoginController(new LoginView(null));
        this.loginView = new LoginView(loginController);
        this.mainMenuView = new MainMenuView(null);
    }

    public void start() {
        while (true) {
            if (!AuthStore.isLoggedIn()) {
                loginView.displayLoginMenu();
                loginView.handleUserInput();
            } else {
                mainMenuView.displayMainMenu();
                mainMenuView.handleUserInput();
            }
        }
    }
}