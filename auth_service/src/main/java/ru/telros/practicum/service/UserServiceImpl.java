package ru.telros.practicum.service;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.telros.practicum.dto.auth_service.RegisterRequest;
import ru.telros.practicum.dto.auth_service.UserDto;
import ru.telros.practicum.entity.User;
import ru.telros.practicum.exception.UserAlreadyExistsException;
import ru.telros.practicum.exception.UserNotFoundException;
import ru.telros.practicum.mapper.UserMapper;
import ru.telros.practicum.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper mapper;


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
