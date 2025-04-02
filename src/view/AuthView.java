package view;

public class AuthView {
    public void displayError(String message) {
        System.out.println("ERROR: " + message);
    }

    public void displaySuccess(String message) {
        System.out.println("SUCCESS: " + message);
    }
}
