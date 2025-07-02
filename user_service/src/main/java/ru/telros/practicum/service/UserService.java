package ru.telros.practicum.service;

import ru.telros.practicum.dto.user_service.UserContactsDto;
import ru.telros.practicum.dto.user_service.UserDetailsDto;
import ru.telros.practicum.dto.user_service.UserDto;

import java.util.UUID;

public interface UserService {
    void uploadPhoto(UUID userId, byte[] photoBytes);
    UserDto getUserById(UUID userId, UUID accountId);
    UserDto createUser(UserDto userDto,UUID accountId);
    void deleteUserById(UUID userId, UUID accountId);
    UserDto updateUserDetails(UUID userId, UserDetailsDto userDetailsDto, UUID accountId);
    UserDto updateUserContacts(UUID userId, UserContactsDto userContactsDto, UUID accountId);
    byte[] getPhoto(UUID userId, UUID accountId);
    void deletePhoto(UUID userId, UUID accountId);
}
