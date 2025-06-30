package ru.telros.practicum.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID uuid) {
        super("ГUser not found, id "+ uuid);
    }
}
