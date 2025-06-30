package ru.telros.practicum.feign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.telros.practicum.dto.auth_service.UserDto;
import ru.telros.practicum.exception.ServiceUnavailableException;

import java.util.UUID;
@Component
@Slf4j
public class AuthServiceFallback implements AuthServiceClient {
    @Override
    public ResponseEntity<UserDto> getUser(UUID userId){
        log.warn("Активирован резервный вариант для getUser с id: {}", userId);
        throw new ServiceUnavailableException("Auth-service недоступен");
    }
}
