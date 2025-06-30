package ru.telros.practicum.dto.auth_service;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AccountDto {
    private UUID id;
    private String login;
}
