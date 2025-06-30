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
import ru.telros.practicum.dto.auth_service.AuthRequest;
import ru.telros.practicum.dto.auth_service.AuthResponse;
import ru.telros.practicum.dto.auth_service.RegisterRequest;
import ru.telros.practicum.dto.auth_service.UserDto;
import ru.telros.practicum.entity.User;
import ru.telros.practicum.exception.InvalidCredentialsException;
import ru.telros.practicum.exception.UserAlreadyExistsException;
import ru.telros.practicum.exception.UserNotFoundException;
import ru.telros.practicum.mapper.UserMapper;
import ru.telros.practicum.repository.UserRepository;
import ru.telros.practicum.security.JwtProvider;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {
    AuthenticationManager authenticationManager;
    JwtProvider jwtProvider;
    UserRepository userRepository;
    UserMapper mapper;
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
        log.info("Attempting to authenticate user: {}", request.getLogin());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getLogin(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtProvider.generateToken(authentication);
            log.info("Authentication successful for user: {}", request.getLogin());
            return new AuthResponse(token);
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getLogin(), e);
            throw new InvalidCredentialsException();
        } catch (Exception e) {
            log.error("Unexpected error during authentication", e);
            throw new InvalidCredentialsException();
        }
    }
    /**
     * Регистрирует нового пользователя.
     * <p>
     * Проверяет уникальность логина, хеширует пароль и сохраняет пользователя в базу данных.
     * В случае, если пользователь с таким логином уже существует, выбрасывается исключение {@link UserAlreadyExistsException}.
     *
     * @param request {@link RegisterRequest} данные для регистрации пользователя
     * @return сохранённый пользователь
     * @throws UserAlreadyExistsException если пользователь с таким логином уже существует
     */
    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new UserAlreadyExistsException(request.getLogin());
        }
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        User user = User.builder()
                .login(request.getLogin())
                .password(encodedPassword)
                .build();

        return mapper.toDto(userRepository.save(user));
    }
}
