package com.github.romansheffer.eventify.services;

import com.github.romansheffer.eventify.configuration.JwtTokenProvider;
import com.github.romansheffer.eventify.dto.AuthResponse;
import com.github.romansheffer.eventify.dto.LoginRequest;
import com.github.romansheffer.eventify.dto.RegisterRequest;
import com.github.romansheffer.eventify.exceptions.ErrorDetailException;
import com.github.romansheffer.eventify.model.User;
import com.github.romansheffer.eventify.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {


    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private NotificationServiceImpl notificationServiceImpl;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authServiceImpl;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("rawPassword123");
    }

    @Test
    @DisplayName("успешная регистрация")
    void registerNewUser_Success() {

                // arrange
        String expectedHash = "hashed_password";
        String expectedToken = "mocked_jwt_token";

        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn(expectedHash);
        when(jwtTokenProvider.generateToken(registerRequest.getEmail())).thenReturn(expectedToken);

        //act
        AuthResponse response = authServiceImpl.registerNewUser(registerRequest);

        // asserts
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        assertEquals("USER", response.getRole());

        verify(userRepository, times(1)).findByEmail(registerRequest.getEmail());
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(notificationServiceImpl, times(1)).createNotifications(any(User.class));
        verify(jwtTokenProvider, times(1)).generateToken(registerRequest.getEmail());
    }

    @Test
    @DisplayName("если email уже существует - ошибка регистрации")
    void registerNewUser_ThrowsException_WhenEmailExists() {

        // arrange
        when(userRepository.findByEmail(registerRequest.getEmail())).thenReturn(Optional.of(new User()));

        // assets
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authServiceImpl.registerNewUser(registerRequest);
        });

        assertEquals("Пользователь с таким email уже существует", exception.getMessage());

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(notificationServiceImpl, never()).createNotifications(any(User.class));
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("успешное залогинивание")
    void logining_Success() {

        // arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("rawPassword");

        User foundUser = new User();
        foundUser.setEmail("user@test.com");
        foundUser.setPassword("hashedPasswordInDb");
        foundUser.setRole("USER");

        String expectedToken = "generated_jwt_token";

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(foundUser));

        when(passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())).thenReturn(true);
        when(jwtTokenProvider.generateToken(foundUser.getEmail())).thenReturn(expectedToken);

        // act
        AuthResponse response = authServiceImpl.logining(loginRequest);

        // asserts
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        assertEquals("USER", response.getRole());

        verify(jwtTokenProvider).generateToken(foundUser.getEmail());
    }

    @Test
    @DisplayName("если при залогинивании юзер не найден - ошибка")
    void logining_ThrowsException_WhenUserNotFound() {

        // arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("unknown@test.com");
        loginRequest.setPassword("anyPassword");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // act, asserts
        ErrorDetailException exception = assertThrows(ErrorDetailException.class, () -> {
            authServiceImpl.logining(loginRequest);
        });

        assertEquals("auth", exception.getField());
        assertEquals("Некорректный email или  пароль", exception.getMessage());

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("если пароль неверный - ошибка")
    void logining_ThrowsException_WhenPasswordInvalid() {

        // arrange
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@test.com");
        loginRequest.setPassword("wrongPassword");

        User foundUser = new User();
        foundUser.setEmail("user@test.com");
        foundUser.setPassword("hashedPasswordInDb");

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(foundUser));
              when(passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())).thenReturn(false);

        // act, asserts
        ErrorDetailException exception = assertThrows(ErrorDetailException.class, () -> {
            authServiceImpl.logining(loginRequest);
        });

        assertEquals("auth", exception.getField());
        assertEquals("Некорректный email или  пароль", exception.getMessage());

        verify(jwtTokenProvider, never()).generateToken(anyString());
    }
}

