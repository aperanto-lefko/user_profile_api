package ru.telros.practicum.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.telros.practicum.service.UserProfileService;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserProfileController {
UserProfileService userProfileService;


@PostMapping("/{userId}/photo")
    public ResponseEntity<Void> uploadPhoto(@PathVariable UUID uuid,
                                            @RequestParam("file") MultipartFile file) throws IOException {
    userProfileService.uploadPhoto(uuid, file.getBytes());
    return ResponseEntity.ok().build();
}
}
