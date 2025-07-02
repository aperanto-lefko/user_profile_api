package ru.telros.practicum.service;

import ru.telros.practicum.dto.auth_service.AccountDto;
import ru.telros.practicum.dto.auth_service.AuthRequest;
import ru.telros.practicum.dto.auth_service.AuthResponse;
import ru.telros.practicum.dto.auth_service.RegisterRequest;

public interface AuthService {
    AuthResponse authenticate(AuthRequest request);
    AccountDto register(RegisterRequest request);
}
