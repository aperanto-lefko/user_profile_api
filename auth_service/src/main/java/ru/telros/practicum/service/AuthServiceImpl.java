package ru.telros.practicum.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.telros.practicum.dto.auth_service.AccountDto;
import ru.telros.practicum.dto.auth_service.AuthRequest;
import ru.telros.practicum.dto.auth_service.AuthResponse;
import ru.telros.practicum.dto.auth_service.RegisterRequest;
import ru.telros.practicum.entity.Account;
import ru.telros.practicum.exception.InvalidCredentialsException;
import ru.telros.practicum.exception.AccountAlreadyExistsException;
import ru.telros.practicum.mapper.AccountMapper;
import ru.telros.practicum.repository.AccountRepository;
import ru.telros.practicum.security.AuthServiceJwtProvider;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    AuthenticationManager authenticationManager;
    AuthServiceJwtProvider jwtProvider;
    AccountRepository accountRepository;
    AccountMapper mapper;
    PasswordEncoder passwordEncoder;

    /**
     * Аутентифицирует пользователя по логину и паролю.
     * <p>
     * При успешной аутентификации генерирует JWT токен и возвращает его клиенту.
     * В случае неверных данных выбрасывает {@link InvalidCredentialsException}.
     *
     * @param request запрос с логином и паролем
     * @return объект {@link AuthResponse} с JWT токеном
     * @throws InvalidCredentialsException если логин или пароль неверные
     */

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        log.info("Попытка аутентификации пользователя: {}", request.getLogin());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtProvider.generateToken(authentication);
            log.info("Аутентификация пользователя прошла успешно: {}", request.getLogin());
            return new AuthResponse(token);
        } catch (AuthenticationException e) {
            log.error("Ошибка аутентификации пользователя: {}", request.getLogin(), e);
            throw new InvalidCredentialsException();
        } catch (Exception e) {
            log.error("Неожиданная ошибка во время аутентификации", e);
            throw new InvalidCredentialsException();
        }
    }
    /**
     * Регистрирует нового пользователя.
     * <p>
     * Проверяет уникальность логина, хеширует пароль и сохраняет пользователя в базу данных.
     * В случае, если пользователь с таким логином уже существует, выбрасывается исключение {@link AccountAlreadyExistsException}.
     *
     * @param request {@link RegisterRequest} данные для регистрации пользователя
     * @return сохранённый пользователь
     * @throws AccountAlreadyExistsException если пользователь с таким логином уже существует
     */
    @Transactional
    public AccountDto register(RegisterRequest request) {
        if (accountRepository.existsByLogin(request.getLogin())) {
            throw new AccountAlreadyExistsException(request.getLogin());
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Account account = Account.builder()
                .login(request.getLogin())
                .password(encodedPassword)
                .build();

        return mapper.toDto(accountRepository.save(account));
    }
}
