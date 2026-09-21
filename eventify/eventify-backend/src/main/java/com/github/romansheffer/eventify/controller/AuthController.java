package com.github.romansheffer.eventify.controller;


import com.github.romansheffer.eventify.dto.AuthResponse;
import com.github.romansheffer.eventify.dto.LoginRequest;
import com.github.romansheffer.eventify.dto.RegisterRequest;
import com.github.romansheffer.eventify.services.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<Object> registrationOfUser(@RequestBody RegisterRequest registerRequest) {

        log.info("Регистрация нового пользователя");

        try {
            AuthResponse authResponse = authServiceImpl.registerNewUser(registerRequest);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(authResponse);
        } catch (IllegalArgumentException e) {

            log.error(" Ошибка при  регистрации пользователя", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loggining(@RequestBody LoginRequest loginRequest) {

        log.info("Залогинивание пользователя");

        try {
            AuthResponse authResponse = authServiceImpl.logining(loginRequest);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(authResponse);
        } catch (IllegalArgumentException e) {

            log.error(" Ошибка при  авторизации пользователя", e);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
