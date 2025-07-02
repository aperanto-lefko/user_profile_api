package ru.telros.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.telros.practicum.dto.auth_service.AccountDto;
import ru.telros.practicum.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "login", target = "login")
    AccountDto toDto(Account account);
}
