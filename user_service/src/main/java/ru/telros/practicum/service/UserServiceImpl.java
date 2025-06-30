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
import ru.telros.practicum.mapper.UserMapper;
import ru.telros.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper mapper;


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
                .name(request.getName())
                .build();

        return mapper.toDto(userRepository.save(user));
    }
}
