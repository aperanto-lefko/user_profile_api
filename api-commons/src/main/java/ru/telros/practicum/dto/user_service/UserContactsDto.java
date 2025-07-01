package ru.telros.practicum.dto.user_service;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Builder
@ToString
public class UserContactsDto {
    @Email()
    @NotBlank
    @Size(min = 6, max = 254)
    String email;
    @Size(min = 6, max = 100)
    String phone;
}
