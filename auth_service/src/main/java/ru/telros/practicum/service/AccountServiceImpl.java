package ru.telros.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.telros.practicum.dto.auth_service.AccountDto;
import ru.telros.practicum.exception.AccountNotFoundException;
import ru.telros.practicum.mapper.AccountMapper;
import ru.telros.practicum.repository.AccountRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
        return accountRepository.findById(accountId)
                .map(mapper::toDto)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }
}
