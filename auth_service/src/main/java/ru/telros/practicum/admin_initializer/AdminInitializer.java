package ru.telros.practicum.admin_initializer;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.telros.practicum.entity.Account;
import ru.telros.practicum.repository.AccountRepository;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminInitializer {
    AccountRepository accountRepository;
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdmin() {
        if(!accountRepository.existsByLogin("admin")) {
            Account admin = Account.builder()
                    .login("admin")
                    .password(passwordEncoder.encode("admin"))
                    .build();
            accountRepository.save(admin);
        }
    }
}
