import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.telros.practicum.dto.user_service.UserDetailsDto;
import ru.telros.practicum.dto.user_service.UserDto;
import ru.telros.practicum.entity.User;
import ru.telros.practicum.exception.AccountNotFoundException;
import ru.telros.practicum.feign.AuthServiceClient;
import ru.telros.practicum.mapper.UserMapper;
import ru.telros.practicum.repository.UserRepository;
import ru.telros.practicum.service.UserServiceImpl;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private UserServiceImpl userService;

    private final UUID userId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final byte[] photoBytes = "test-photo".getBytes();

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(userId)
                .accountId(accountId)
                .photo(null)
                .build();
    }

    @Test
    void uploadPhoto_shouldSavePhoto() {
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.uploadPhoto(userId, photoBytes);

        Assertions.assertArrayEquals(photoBytes, user.getPhoto());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void getPhoto_shouldReturnPhoto_whenAccountExists() {
        user.setPhoto(photoBytes);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(authServiceClient.getAccount(accountId)).thenReturn(null);

        byte[] result = userService.getPhoto(userId, accountId);

        Assertions.assertArrayEquals(photoBytes, result);
        Mockito.verify(authServiceClient).getAccount(accountId);
    }

    @Test
    void getPhoto_shouldThrow_whenAccountNotFound() {
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://url",
                Map.of(),
                null,
                (Charset) null
        );
        Mockito.doThrow(new FeignException.NotFound("Not Found", request, null, null))
                .when(authServiceClient).getAccount(accountId);

        Assertions.assertThrows(AccountNotFoundException.class, () -> {
            userService.getPhoto(userId, accountId);
        });
    }

    @Test
    void deletePhoto_shouldSetPhotoNullAndSave() {
        user.setPhoto(photoBytes);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(authServiceClient.getAccount(accountId)).thenReturn(null);

        userService.deletePhoto(userId, accountId);

        Assertions.assertNull(user.getPhoto());
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldCreateUser_whenAccountExists() {
        UserDto userDto = UserDto.builder()
                .id(userId)
                .lastName("Ivanov")
                .firstName("Ivan")
                .birthDate(null)
                .email("test@mail.com")
                .phone("12345")
                .build();
        User user = User.builder()
                .id(userId)
                .accountId(accountId)
                .build();
        User savedUser = User.builder().id(userId).accountId(accountId).build();

        Mockito.when(authServiceClient.getAccount(accountId)).thenReturn(null);
        Mockito.when(mapper.toEntity(userDto)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(savedUser);
        Mockito.when(mapper.toDto(savedUser)).thenReturn(userDto);

        UserDto result = userService.createUser(userDto, accountId);

        Assertions.assertEquals(userDto, result);
        Mockito.verify(authServiceClient).getAccount(accountId);
        Mockito.verify(userRepository).save(user);
    }
    @Test
    void getUserById_shouldReturnUser_whenAccountExists() {
        UserDto userDto = UserDto.builder()
                .id(userId)
                .lastName("Ivanov")
                .firstName("Ivan")
                .birthDate(null)
                .email("test@mail.com")
                .phone("12345")
                .build();

        Mockito.when(authServiceClient.getAccount(accountId)).thenReturn(null);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(mapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(userId, accountId);

        Assertions.assertEquals(userDto, result);
    }
    @Test
    void updateUserDetails_shouldUpdateDetails_whenAccountMatches() {
        UserDetailsDto detailsDto = UserDetailsDto.builder()

                .lastName("Ivanov")
                .firstName("Ivan")
                .birthDate(null)
                .build();
        User updatedUser = User.builder()
                .id(userId)
                .accountId(accountId)
                .firstName("Ivan")
                .lastName("Ivanov")
                .build();
        UserDto expectedDto = UserDto.builder()
                .id(userId)
                .lastName("Ivanov")
                .firstName("Ivan")
                .birthDate(null)
                .email(null)
                .phone(null)
                .build();

        Mockito.when(authServiceClient.getAccount(accountId)).thenReturn(null);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(updatedUser);
        Mockito.when(mapper.toDto(updatedUser)).thenReturn(expectedDto);

        user.setAccountId(accountId);

        UserDto result = userService.updateUserDetails(userId, detailsDto, accountId);

        Assertions.assertEquals(expectedDto, result);
        Mockito.verify(mapper).updateUserDetailsFromDto(detailsDto, user);
    }
    @Test
    void deleteUserById_shouldDeleteUserAndAccount_whenAccountExists() {
        Mockito.when(authServiceClient.getAccount(accountId)).thenReturn(null);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(authServiceClient.getAccount(accountId)).thenReturn(null);

        user.setAccountId(accountId);

        userService.deleteUserById(userId, accountId);

        Mockito.verify(userRepository).delete(user);
        Mockito.verify(authServiceClient).deleteAccount(accountId);
    }
}
