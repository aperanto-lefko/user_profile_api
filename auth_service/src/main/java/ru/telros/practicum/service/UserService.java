package ru.telros.practicum.service;

import ru.telros.practicum.dto.auth_service.RegisterRequest;
import ru.telros.practicum.dto.auth_service.UserDto;


public interface UserService {
    UserDto register(RegisterRequest request);
}
