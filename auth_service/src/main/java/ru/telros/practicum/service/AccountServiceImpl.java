package ru.telros.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.telros.practicum.dto.auth_service.AccountDto;
import ru.telros.practicum.entity.Account;
import ru.telros.practicum.exception.AccountNotFoundException;
import ru.telros.practicum.mapper.AccountMapper;
import ru.telros.practicum.repository.AccountRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountServiceImpl implements AccountService {
    AccountRepository accountRepository;
    AccountMapper mapper;

    /**
     * Возвращает пользователя по его идентификатору.
     * <p>
     * Если пользователь с указанным ID не найден, выбрасывается исключение {@link AccountNotFoundException}.
     *
     * @param accountId уникальный идентификатор пользователя
     * @return найденный пользователь
     * @throws AccountNotFoundException если пользователь с таким ID не существует
     */
    public AccountDto getAccountById(UUID accountId) {
        log.info("Поиск аккаунта по id {}", accountId);
        return mapper.toDto(findAccountById(accountId));
    }

    public void deleteAccountById(UUID accountId) {
        log.info("Удаление аккаунта по id {}", accountId);
        findAccountById(accountId);
        accountRepository.deleteById(accountId);
    }

    private Account findAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
