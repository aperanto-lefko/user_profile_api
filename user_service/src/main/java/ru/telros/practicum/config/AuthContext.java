package ru.telros.practicum.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuthContext {
    /**
     * Метод, извлекающий accountId из SecurityContext (из токена)
     * @return
     */
    public UUID getAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("Нет аутентифицированного пользователя");
        }
        return (UUID) authentication.getPrincipal();
    }
}
