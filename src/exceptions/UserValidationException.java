package exceptions;

public class UserValidationException extends BTOSystemException {
    public UserValidationException(String message) {
        super(message);
    }

    public UserValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}