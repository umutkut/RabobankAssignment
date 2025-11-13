package nl.rabobank.exception;

public class UnsupportedUserOperationException extends RuntimeException {
    public UnsupportedUserOperationException(String message) {
        super("User cannot operate." + message);
    }
}
