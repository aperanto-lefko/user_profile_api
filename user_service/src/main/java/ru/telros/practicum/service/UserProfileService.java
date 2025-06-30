package ru.telros.practicum.service;

import java.util.UUID;

public interface UserProfileService {
    void uploadPhoto(UUID userId, byte[] photoBytes);
}
