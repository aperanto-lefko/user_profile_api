package ru.telros.practicum.service;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.telros.practicum.dto.user_service.UserDetailsDto;
import ru.telros.practicum.dto.user_service.UserDto;
import ru.telros.practicum.entity.User;
import ru.telros.practicum.exception.AccountNotFoundException;
import ru.telros.practicum.exception.UserNotFoundException;
import ru.telros.practicum.exception.ValidationException;
import ru.telros.practicum.feign.AuthServiceClient;
import ru.telros.practicum.mapper.UserMapper;
import ru.telros.practicum.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {
    UserRepository userRepository;
    UserMapper mapper;
    AuthServiceClient authServiceClient;


    public void uploadPhoto(UUID userId, byte[] photoBytes) {

    }

    /**
     * Создаёт новый профиль пользователя и связывает его с существующим аккаунтом.
     *
     * <p>Перед созданием выполняется проверка существования аккаунта по указанному идентификатору.
     * Если аккаунт не существует, выбрасывается исключение {@link AccountNotFoundException}.</p>
     *
     * @param userDto   DTO с данными нового пользователя
     * @param accountId идентификатор аккаунта, к которому привязывается пользователь
     * @return созданный пользователь в формате {@link UserDto}
     * @throws AccountNotFoundException если аккаунт с указанным идентификатором не найден
     */
    @Override
    public UserDto createUser(UserDto userDto, UUID accountId) {
        log.info("Создание нового профиля пользователя для accountId {}", accountId);
        checkAccount(accountId);
        User user = mapper.toEntity(userDto);
        user.setAccountId(accountId);
        User savedUser = userRepository.save(user);
        log.info("Пользователь создан {}", savedUser);
        return mapper.toDto(savedUser);
    }

    /**
     * Удаляет профиль пользователя и связанный аккаунт.
     *
     * <p>Выполняет проверку существования аккаунта перед удалением.
     * Если аккаунт не найден, выбрасывается {@link AccountNotFoundException}.
     * После удаления профиля пользователя выполняется запрос на удаление аккаунта через сервис авторизации.</p>
     *
     * @param userId    идентификатор пользователя
     * @param accountId идентификатор аккаунта
     * @throws AccountNotFoundException если аккаунт не существует
     * @throws UserNotFoundException    если пользователь не существует
     * @throws FeignException           если удаление аккаунта завершилось с ошибкой
     */
    @Override
    public void deleteUserById(UUID userId, UUID accountId) {
        log.info("Удаление пользователя по id {}", userId);
        checkAccount(accountId);
        User user = findUserById(userId);
        userRepository.delete(user);
        log.info("Пользователь удален");
        try {
            log.info("Запрос на удаление аккаунта {}", accountId);
            authServiceClient.deleteAccount(accountId);
        } catch (FeignException ex) {
            log.warn("Ошибка при удалении аккаунта {}: {}", accountId, ex.getMessage());
            throw ex;
        }
    }

    /**
     * Возвращает профиль пользователя по идентификатору.
     *
     * <p>Перед получением выполняется проверка существования аккаунта.</p>
     *
     * @param userId    идентификатор пользователя
     * @param accountId идентификатор аккаунта
     * @return DTO с данными пользователя
     * @throws AccountNotFoundException если аккаунт не существует
     * @throws UserNotFoundException    если пользователь не существует
     */
    @Override
    public UserDto getUserById(UUID userId, UUID accountId) {
        checkAccount(accountId);
        return mapper.toDto(findUserById(userId));
    }
    /**
     * Обновляет детальную информацию профиля пользователя.
     *
     * <p>Перед обновлением выполняется проверка существования аккаунта и проверка,
     * что указанный профиль пользователя действительно принадлежит этому аккаунту.</p>
     *
     * Только поля, содержащиеся в {@link UserDetailsDto} (имя, фамилия, дата рождения), будут обновлены.
     * Остальные поля профиля пользователя остаются без изменений.
     *
     * @param userId идентификатор пользователя
     * @param userDetailsDto DTO с новыми значениями полей профиля
     * @param accountId идентификатор аккаунта, которому принадлежит пользователь
     * @return обновлённый профиль пользователя в формате {@link UserDto}
     *
     * @throws AccountNotFoundException если аккаунт не существует
     * @throws UserNotFoundException если пользователь не найден
     * @throws ValidationException если профиль пользователя не принадлежит указанному аккаунту
     */
    public UserDto updateUserDetails(UUID userId, UserDetailsDto userDetailsDto, UUID accountId) {
        log.info("Обновление данных {} для пользователя с id {}", userDetailsDto, userId);
        checkAccount(accountId);
        User user = findUserById(userId);
        checkAccountAndUserProfile(user, accountId);
        mapper.updateUserDetailsFromDto(userDetailsDto, user);
        log.info("Сохранение обновленного профиля пользователя {}", user);
        return mapper.toDto(userRepository.save(user));
    }

    private void checkAccount(UUID accountId) {
        try {
            authServiceClient.getAccount(accountId);
        } catch (FeignException.NotFound ex) {
            throw new AccountNotFoundException("Аккаунт с id не найден" + accountId);
        }
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден id" + userId));
    }

    private void checkAccountAndUserProfile(User user, UUID accountId) {
        if (user.getAccountId() != accountId) {
            throw new ValidationException(
                    String.format("Номер аккаунта %s не совпадает с номером аккаунта записанным для пользователя %s",
                            accountId, user));
        }
    }
}
