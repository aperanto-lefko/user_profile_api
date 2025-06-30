package ru.telros.practicum.service;

import ru.telros.practicum.dto.auth_service.RegisterRequest;
import ru.telros.practicum.dto.auth_service.UserDto;

import java.util.UUID;


public interface UserService {

    UserDto getUserById(UUID userId);
}
