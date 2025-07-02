package ru.telros.practicum.error_handler;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.telros.practicum.exception.AccountNotFoundException;
import ru.telros.practicum.exception.UserNotFoundException;
import ru.telros.practicum.exception.ValidationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends BaseErrorHandler {

    @ExceptionHandler({UserNotFoundException.class,
            AccountNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleNotImplementedException(FeignException ex) {
        return handleException(ex, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleNotImplementedException(RuntimeException ex) {
        return handleException(ex, HttpStatus.CONFLICT);
    }

    @Override
    protected String getFriendlyMessage(RuntimeException ex) {
        String className = ex.getClass().getSimpleName();
        return switch (className) {
            case "UserNotFoundException" -> "User not found";
            case "AccountNotFoundException" -> "Account not found";
            case "FeignException" -> "Service call failed";
            case "ValidationException" -> "Verification error";
            default -> "An unexpected error occurred";
        };
    }
}
