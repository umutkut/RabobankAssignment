package nl.rabobank.exception;

import lombok.Getter;

@Getter
public class ForbiddenOperationException extends RuntimeException {

    public ForbiddenOperationException(String message) {
        super(message);
    }
}
