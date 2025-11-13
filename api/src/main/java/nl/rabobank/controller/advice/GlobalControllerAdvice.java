package nl.rabobank.controller.advice;

import nl.rabobank.controller.model.ErrorResponse;
import nl.rabobank.exception.AccountNotFoundException;
import nl.rabobank.exception.UnsupportedUserOperationException;
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

    @ExceptionHandler(UnsupportedUserOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedUserOperation(UnsupportedUserOperationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
    }
}
