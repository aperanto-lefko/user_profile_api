package ru.telros.practicum.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.telros.practicum.dto.user_service.UserContactsDto;
import ru.telros.practicum.dto.user_service.UserDetailsDto;
import ru.telros.practicum.dto.user_service.UserDto;
import ru.telros.practicum.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserDetailsFromDto(UserDetailsDto userDetailsDto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserContactsFromDto(UserContactsDto userContactsDto, @MappingTarget User user);
}
