package ru.telros.practicum.service;

import feign.FeignException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.telros.practicum.dto.user_service.UserContactsDto;
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

    /**
     * Загружает или обновляет фотографию пользователя.
     *
     * Если фотография уже была загружена ранее, она будет перезаписана новыми данными.
     *
     * @param userId идентификатор пользователя
     * @param photoBytes массив байтов фотографии
     *
     * @throws UserNotFoundException если пользователь не найден
     */
    public void uploadPhoto(UUID userId, byte[] photoBytes) {
        log.info("Загрузка фотографии для пользователя с id {}", userId);
        User user = findUserById(userId);
        user.setPhoto(photoBytes);
        log.info("Сохранение пользователя с фотографией");
        userRepository.save(user);

    }
    /**
     * Возвращает фотографию пользователя в виде массива байтов.
     *
     * Перед получением выполняется проверка существования аккаунта,
     * а также проверка принадлежности пользователя к указанному аккаунту.
     *
     * @param userId идентификатор пользователя
     * @param accountId идентификатор аккаунта, которому принадлежит пользователь
     * @return массив байтов фотографии или {@code null}, если фотография отсутствует
     *
     * @throws AccountNotFoundException если аккаунт не существует
     * @throws UserNotFoundException если пользователь не найден
     */
    public byte[] getPhoto(UUID userId, UUID accountId) {
        log.info("Поиск фотографии для пользователя с id {}", userId);
        checkAccount(accountId);
        User user = findUserById(userId);
        return user.getPhoto();
    }
    /**
     * Удаляет фотографию пользователя.
     *
     * После удаления поле фотографии будет установлено в {@code null}.
     * Перед удалением выполняется проверка существования аккаунта,
     * а также проверка принадлежности пользователя к указанному аккаунту.
     *
     * @param userId идентификатор пользователя
     * @param accountId идентификатор аккаунта, которому принадлежит пользователь
     *
     * @throws AccountNotFoundException если аккаунт не существует
     * @throws UserNotFoundException если пользователь не найден
     */
    public void deletePhoto(UUID userId, UUID accountId) {
        log.info("Удаление фотографии для пользователя с id {}", userId);
        checkAccount(accountId);
        User user = findUserById(userId);
        user.setPhoto(null);
        log.info("Сохранение пользователя с фотографией");
        userRepository.save(user);
    }

    /**
     * Создаёт новый профиль пользователя и связывает его с существующим аккаунтом.
     *
     * Перед созданием выполняется проверка существования аккаунта по указанному идентификатору.
     * Если аккаунт не существует, выбрасывается исключение {@link AccountNotFoundException}.
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
     * Выполняет проверку существования аккаунта перед удалением.
     * Если аккаунт не найден, выбрасывается {@link AccountNotFoundException}.
     * После удаления профиля пользователя выполняется запрос на удаление аккаунта через сервис авторизации.
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
     * Перед получением выполняется проверка существования аккаунта.
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
     * Перед обновлением выполняется проверка существования аккаунта и проверка,
     * что указанный профиль пользователя действительно принадлежит этому аккаунту.
     *
     * Только поля, содержащиеся в {@link UserDetailsDto} (имя, фамилия, дата рождения), будут обновлены.
     * Остальные поля профиля пользователя остаются без изменений.
     *
     * @param userId         идентификатор пользователя
     * @param userDetailsDto DTO с новыми значениями полей профиля
     * @param accountId      идентификатор аккаунта, которому принадлежит пользователь
     * @return обновлённый профиль пользователя в формате {@link UserDto}
     * @throws AccountNotFoundException если аккаунт не существует
     * @throws UserNotFoundException    если пользователь не найден
     * @throws ValidationException      если профиль пользователя не принадлежит указанному аккаунту
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

    /**
     * Обновляет контактную информацию пользователя.
     *
     * Перед обновлением выполняется проверка существования аккаунта и проверка,
     * что указанный пользователь принадлежит этому аккаунту.
     *
     * Обновляются только поля контактной информации: электронная почта и номер телефона.
     * Остальные данные пользователя остаются без изменений.
     *
     * @param userId          идентификатор пользователя
     * @param userContactsDto DTO с новыми контактными данными пользователя
     * @param accountId       идентификатор аккаунта, которому принадлежит пользователь
     * @return обновлённый профиль пользователя в формате {@link UserDto}
     * @throws AccountNotFoundException если аккаунт не существует
     * @throws UserNotFoundException    если пользователь не найден
     * @throws ValidationException      если профиль пользователя не принадлежит указанному аккаунту
     */
    public UserDto updateUserContacts(UUID userId, UserContactsDto userContactsDto, UUID accountId) {
        log.info("Обновление контактных данных {} для пользователя с id {}", userContactsDto, userId);
        checkAccount(accountId);
        User user = findUserById(userId);
        checkAccountAndUserProfile(user, accountId);
        mapper.updateUserContactsFromDto(userContactsDto, user);
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
        log.info("Поиск пользователя ");
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден id" + userId));
    }

    private void checkAccountAndUserProfile(User user, UUID accountId) {
        if (!user.getAccountId().equals(accountId)) {
            throw new ValidationException(
                    String.format("Номер аккаунта %s не совпадает с номером аккаунта записанным для пользователя %s",
                            accountId, user));
        }
    }
}
