package nl.rabobank.exception;

public class PowerOfAttorneyAlreadyExistException extends RuntimeException {
    public PowerOfAttorneyAlreadyExistException() {
        super("Power of attorney already exist.");
    }
}
