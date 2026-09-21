package com.github.romansheffer.eventify.controller;

import com.github.romansheffer.eventify.model.Notification;
import com.github.romansheffer.eventify.model.User;
import com.github.romansheffer.eventify.repository.NotificationRepository;
import com.github.romansheffer.eventify.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;


    @Test
    @DisplayName("успешный запрос настроек уведомлений")
    void getNotificationPreferences_happyPath() throws Exception {

        // arrange
        createTestUserWithNotifications();

        // act, assert
        mockMvc.perform(get("/user/notifications")
                        .with(user("emailOfUser.com").authorities(new SimpleGrantedAuthority("USER"))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifyNewEvents").value(false))
                .andExpect(jsonPath("$.notifyUpcoming").value(false));
    }

    @Test
    @DisplayName("успешное обновление настроек уведомлений")
    void updateNotifications_happyPath() throws Exception {

        // arrange
        createTestUserWithNotifications();

        String updatePreferencesJson = """
                {
                  "notifyNewEvents": true,
                  "notifyUpcoming": true,
                  "notifyBeforeHours": 2
                }
                """;

        // act, asserts
        mockMvc.perform(put("/user/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePreferencesJson)
                        .with(user("emailOfUser.com").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notifyNewEvents").value(true))
                .andExpect(jsonPath("$.notifyUpcoming").value(true))
                .andExpect(jsonPath("$.notifyBeforeHours").value(2));
    }

    @Test
    @DisplayName("успешное удаление настроек уведомлений")
    void removeNotifications_happyPath() throws Exception {

        // arrange
        createTestUserWithNotifications();

        // act, asserts
        mockMvc.perform(delete("/user/notifications")
                        .with(user("emailOfUser.com").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())

                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("запрос настроек — ошибка 403 Forbidden для неавторизованного пользователя")
    void getNotificationPreferences_unauthorized_ShouldReturn403() throws Exception {

        //asserts
        mockMvc.perform(get("/user/notifications"))
                .andDo(print())

                .andExpect(status().isForbidden());
    }


    //для сокращения кода
    private void createTestUserWithNotifications() {

        User testUser = new User();
        testUser.setEmail("emailOfUser.com");
        testUser.setPassword("123IsCoolPassword");
        testUser.setRole("USER");

        User savedUser = userRepository.save(testUser);

        Notification newNotifications = new Notification();
        newNotifications.setUser(savedUser);
        newNotifications.setNotifyNewEvents(false);
        newNotifications.setNotifyUpcoming(false);

        notificationRepository.save(newNotifications);
    }
}