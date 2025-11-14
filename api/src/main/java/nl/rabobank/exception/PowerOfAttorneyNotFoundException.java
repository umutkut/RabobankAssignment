package nl.rabobank.exception;

public class PowerOfAttorneyNotFoundException extends RuntimeException {
    public PowerOfAttorneyNotFoundException(String message) {
        super("Power of attorney not found. " + message);
    }
}
