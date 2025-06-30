package ru.telros.practicum.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    public AccountAlreadyExistsException(String login) {
        super("User with login " + login + " already exists");
    }
}
