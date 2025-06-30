package ru.telros.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.telros.practicum.dto.auth_service.AuthRequest;
import ru.telros.practicum.dto.auth_service.AuthResponse;
import ru.telros.practicum.dto.auth_service.UserDto;
import ru.telros.practicum.entity.User;
import ru.telros.practicum.exception.InvalidCredentialsException;
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
     * Возвращает пользователя по его идентификатору.
     * <p>
     * Если пользователь с указанным ID не найден, выбрасывается исключение {@link UserNotFoundException}.
     *
     * @param userId уникальный идентификатор пользователя
     * @return найденный пользователь
     * @throws UserNotFoundException если пользователь с таким ID не существует
     */
    public UserDto getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(mapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
