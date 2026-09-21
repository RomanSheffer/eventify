package com.github.romansheffer.eventify.services;

import com.github.romansheffer.eventify.dto.AuthResponse;
import com.github.romansheffer.eventify.dto.LoginRequest;
import com.github.romansheffer.eventify.dto.RegisterRequest;

public interface AuthService {

    AuthResponse registerNewUser(RegisterRequest registerRequest);

    AuthResponse logining(LoginRequest loginRequest);
}
