package com.github.romansheffer.eventify.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerImplTest {


    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("успешная регистрация нового пользователя")
    void registrationOfUser_happyPath() throws Exception {
        String registerRequestJson = """
                {
                   "email": "user777@example.com",
                   "password": "securePassword123"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestJson)
                        .with(anonymous())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("ошибка 400 при регистрации, если такой пользователь уже существует")
    void registrationOfUser_alreadyExists_shouldReturnBadRequest() throws Exception {
        String registerRequestJson = """
                {
                    "username": "duplicate_user",
                    "email": "duplicate@example.com",
                    "password": "password"
                }
                """;

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestJson)
                        .with(csrf()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestJson)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("уже существует")));
    }

    @Test
    @DisplayName("успешный вход существующего пользователя")
    void loggining_happyPath() throws Exception {

        String registerJson = """
                {
                   "email": "active@example.com",
                   "password": "my_password"
                }
                """;
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson)
                .with(anonymous())
                .with(csrf()));

        String loginRequestJson = """
                {
                   "email": "active@example.com",
                   "password": "my_password"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson)
                        .with(anonymous())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("ошибка 401 при входе с неверным паролем")
    void loggining_wrongPassword_shouldReturnBadRequest() throws Exception {

        String registerJson = """
                {
                    "email": "email": "fail@example.com",
                                "password": "my_password"
                }
                """;
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerJson)
                .with(anonymous())
                .with(csrf()));

        String wrongLoginRequestJson = """
                {
                   "email": "fail@example.com",
                               "password": "not_my_password"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wrongLoginRequestJson)
                        .with(anonymous())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}