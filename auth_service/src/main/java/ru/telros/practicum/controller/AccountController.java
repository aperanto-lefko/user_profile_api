package ru.telros.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.telros.practicum.dto.auth_service.AccountDto;
import ru.telros.practicum.service.AccountService;

import java.util.UUID;

@RestController
@RequestMapping("/internal/auth/account")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountController {
    AccountService accountService;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable("accountId") UUID accountId) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(accountService.getAccountById(accountId));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("accountId") UUID accountId) {
        accountService.deleteAccountById(accountId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
