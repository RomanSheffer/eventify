package com.github.romansheffer.eventify.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private int createTestEventAndGetId() throws Exception {
        String eventCreateRequestJson = """
                {
                    "title": "Фестиваль 2026",
                    "description": "Описание тестового мероприятия",
                    "coverUrl": "https://example.com",
                    "dateTime": "2026-10-25T18:00:00Z",
                    "totalTickets": 1000
                }
                """;

        String response = mockMvc.perform(post("/admin/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventCreateRequestJson)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return com.jayway.jsonpath.JsonPath.read(response, "$.id");
    }

    @Test
    @DisplayName("успешное получение мероприятия по ID (для админа)")
    void getEventById_happyPath() throws Exception {
        int eventId = createTestEventAndGetId();

        mockMvc.perform(get("/events/{id}", eventId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andDo(print())

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId))
                .andExpect(jsonPath("$.title").value("Фестиваль 2026"));
    }

    @Test
    @DisplayName("ошибка 404 при получении мероприятия по несуществующему ID")
    void getEventById_notFound_shouldReturn404() throws Exception {

        Long nonExistentId = 99999L;

        mockMvc.perform(get("/events/{id}", nonExistentId)
                        .with(user("user").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"));
    }

    @Test
    @DisplayName("успешное получение всех мероприятий по фильтру дат и пагинации")
    void getAllEventsBetweenDates_happyPath() throws Exception {

        createTestEventAndGetId();

        mockMvc.perform(get("/events")
                        .param("from", "2026-10-01T00:00:00Z")
                        .param("to", "2026-11-01T00:00:00Z")
                        .param("page", "0")
                        .param("size", "5")
                        .with(user("user").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].title").value(org.hamcrest.Matchers.hasItem("Фестиваль 2026")));
    }
}