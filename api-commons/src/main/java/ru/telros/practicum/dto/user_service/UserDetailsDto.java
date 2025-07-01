package ru.telros.practicum.dto.user_service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@ToString
public class UserDetailsDto {

    @NotBlank
    @Size(min = 2, max = 254)
    String lastName;
    @NotBlank
    @Size(min = 2, max = 254)
    String firstName;
    LocalDate birthDate;
}
