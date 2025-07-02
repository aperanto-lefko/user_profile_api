package ru.telros.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.telros.practicum.dto.auth_service.AccountDto;

import java.util.UUID;

@FeignClient(name = "auth-service",path = "/internal/auth/account", fallback = AuthServiceFallback.class)
public interface AuthServiceClient {
    @GetMapping("/{accountId}")
    ResponseEntity<AccountDto> getAccount(@PathVariable("accountId") UUID accountId);
    @DeleteMapping("/{accountId}")
    ResponseEntity<Void> deleteAccount(@PathVariable("accountId") UUID accountId);
}
