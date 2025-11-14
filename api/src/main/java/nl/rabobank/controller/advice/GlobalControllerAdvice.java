package nl.rabobank.controller.advice;

import nl.rabobank.controller.model.ErrorResponse;
import nl.rabobank.exception.AccountNotFoundException;
import nl.rabobank.exception.ForbiddenOperationException;
import nl.rabobank.exception.PowerOfAttorneyAlreadyExistException;
import nl.rabobank.exception.PowerOfAttorneyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PowerOfAttorneyAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handlePoaAlreadyExists(PowerOfAttorneyAlreadyExistException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(PowerOfAttorneyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePoaNotFound(PowerOfAttorneyNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenOperationException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ex.getMessage()));
    }
}
