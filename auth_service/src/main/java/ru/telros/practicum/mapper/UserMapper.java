package ru.telros.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.telros.practicum.dto.auth_service.UserDto;
import ru.telros.practicum.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "login", target = "login")
    UserDto toDto(User user);
}
