package ru.telros.practicum.dto.user_service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.UUID;
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
public class UserDto {
    UUID id;
    @NotBlank
    @Size(min = 2, max = 254)
    String lastName;
    @NotBlank
    @Size(min = 2, max = 254)
    String firstName;
    LocalDate birthDate;
    @Email()
    @NotBlank
    @Size(min = 6, max = 254)
    String email;
    @Size(min = 6, max = 100)
    String phone;

}
