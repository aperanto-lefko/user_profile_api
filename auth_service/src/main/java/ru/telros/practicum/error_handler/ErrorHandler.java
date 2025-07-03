package ru.telros.practicum.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.telros.practicum.exception.InvalidCredentialsException;
import ru.telros.practicum.exception.AccountAlreadyExistsException;
import ru.telros.practicum.exception.AccountNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler extends BaseErrorHandler {
    // 400 Bad Request - Ошибки валидации
    @ExceptionHandler({
            InvalidCredentialsException.class,
            AccountAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex) {
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }

    // 404 Not Found - Ресурсы не найдены
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }



    @Override
    protected String getFriendlyMessage(RuntimeException ex) {
        String className = ex.getClass().getSimpleName();
        return switch (className) {
            case "InvalidCredentialsException" -> "Invalid login or password";
            case "AccountAlreadyExistsException" -> "Account already exists";
            case "AccountNotFoundException" -> "Account not found";
            default -> "An unexpected error occurred";
        };
    }
}
