package exceptions;

public class ApplicationValidationException extends BTOSystemException {
    public ApplicationValidationException(String message) {
        super(message);
    }

    public ApplicationValidationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}