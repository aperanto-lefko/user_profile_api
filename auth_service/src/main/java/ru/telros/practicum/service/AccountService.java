package ru.telros.practicum.service;

import ru.telros.practicum.dto.auth_service.AccountDto;

import java.util.UUID;


public interface AccountService {

    AccountDto getAccountById(UUID accountId);
    void deleteAccountById(UUID accountId);
}
