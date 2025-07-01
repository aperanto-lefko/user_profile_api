package ru.telros.practicum.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.telros.practicum.dto.user_service.UserDetailsDto;
import ru.telros.practicum.dto.user_service.UserDto;
import ru.telros.practicum.service.UserService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping("/{userId}")
    ResponseEntity<UserDto> getUserById(@PathVariable("userId") UUID userId,
                                        @RequestHeader("X-Account-Id") UUID accountId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUserById(userId, accountId));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto,
                                              @RequestHeader("X-Account-Id") UUID accountId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(userDto, accountId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID userId,
                                           @RequestHeader("X-Account-Id") UUID accountId) {
        userService.deleteUserById(userId, accountId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PatchMapping("/{userId}/details")
    public ResponseEntity<UserDto> updateUserDetails(@PathVariable("userId") UUID userId,
                                                     @RequestBody @Valid UserDetailsDto userDetailsDto,
                                                     @RequestHeader("X-Account-Id") UUID accountId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUserDetails(userId,userDetailsDto, accountId));
    }


    @PostMapping("/{userId}/photo")
    public ResponseEntity<Void> uploadPhoto(@PathVariable("userId") UUID uuid,
                                            @RequestParam("file") MultipartFile file) throws IOException {
        userService.uploadPhoto(uuid, file.getBytes());
        return ResponseEntity.ok().build();
    }


}
