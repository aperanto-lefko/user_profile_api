package ru.telros.practicum.service;

import ru.telros.practicum.dto.auth_service.AuthRequest;
import ru.telros.practicum.dto.auth_service.AuthResponse;
import ru.telros.practicum.dto.auth_service.RegisterRequest;
import ru.telros.practicum.dto.auth_service.UserDto;
import ru.telros.practicum.entity.User;

import java.util.UUID;

public interface AuthService {
    AuthResponse authenticate(AuthRequest request);
    UserDto register(RegisterRequest request);
}
