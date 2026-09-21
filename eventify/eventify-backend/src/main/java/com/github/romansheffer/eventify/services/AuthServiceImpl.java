package com.github.romansheffer.eventify.services;


import com.github.romansheffer.eventify.configuration.JwtTokenProvider;
import com.github.romansheffer.eventify.dto.AuthResponse;
import com.github.romansheffer.eventify.dto.LoginRequest;
import com.github.romansheffer.eventify.dto.RegisterRequest;
import com.github.romansheffer.eventify.exceptions.ErrorDetailException;
import com.github.romansheffer.eventify.model.User;
import com.github.romansheffer.eventify.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final NotificationServiceImpl notificationServiceImpl;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public AuthResponse registerNewUser(RegisterRequest registerRequest) {

        log.info("запуск сервиса регистрации новогоо пользователя");
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User newUser = new User();
        newUser.setRole("USER");
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(hashedPassword);

        userRepository.save(newUser);

        notificationServiceImpl.createNotifications(newUser);

        String token = jwtTokenProvider.generateToken(newUser.getEmail());

        return new AuthResponse(token, newUser.getRole());
    }


    public AuthResponse logining(LoginRequest loginRequest) {

        log.info("запуск сервиса залогинивания пользователя");
        User foundUser = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ErrorDetailException("auth", "Некорректный email или  пароль", HttpStatus.UNAUTHORIZED));


        if (!passwordEncoder.matches(loginRequest.getPassword(), foundUser.getPassword())) {
            throw new ErrorDetailException("auth", "Некорректный email или  пароль", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtTokenProvider.generateToken(foundUser.getEmail());

        return new AuthResponse(token, foundUser.getRole());
    }


}
