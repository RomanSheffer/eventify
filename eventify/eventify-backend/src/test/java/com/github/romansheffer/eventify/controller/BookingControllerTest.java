
package com.github.romansheffer.eventify.controller;


import com.github.romansheffer.eventify.model.Event;
import com.github.romansheffer.eventify.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    private Long createAndSaveTestEventId() {
      Event event = new Event();
        event.setTitle("Epic Fest 2028");
        event.setDescription("Тестовое описание");
        event.setCoverUrl("https://example.com");
        event.setTotalTickets(100L);
        event.setAvailableTickets(100L);

        event.setDateTime(Instant.now().plus(5, ChronoUnit.DAYS));
        return eventRepository.save(event).getId();
    }

    private int createTestBookingAndGetId(Long eventId) throws Exception {
        String createRequestJson = """
                {
                    "eventId": %d,
                    "ticketCount": 2
                }
                """.formatted(eventId);

        String response = mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestJson)
                        .with(user("user").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return com.jayway.jsonpath.JsonPath.read(response, "$.id");
    }

    @Test
    @DisplayName("успешное создание нового бронирования")
    void createBooking_happyPath() throws Exception {
        Long eventId = createAndSaveTestEventId();

        String createRequestJson = """
                {
                    "eventId": %d,
                    "ticketCount": 3
                }
                """.formatted(eventId);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequestJson)
                        .with(user("user").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.ticketCount").value(3));
    }

    @Test
    @DisplayName("успешное получение бронирования по ID")
    void getBookingById_happyPath() throws Exception {
        Long eventId = createAndSaveTestEventId();
        int bookingId = createTestBookingAndGetId(eventId);

        mockMvc.perform(get("/bookings/{id}", bookingId)
                        .with(user("user").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    @DisplayName("успешное обновление бронирования по ID")
    void updateBooking_happyPath() throws Exception {
        Long eventId = createAndSaveTestEventId();
        int bookingId = createTestBookingAndGetId(eventId);

        String updateRequestJson = """
                {
                    "ticketCount": 5
                }
                """;

        mockMvc.perform(put("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestJson)
                        .with(user("user").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.ticketCount").value(5));
    }

    @Test
    @DisplayName("успешное получение всех своих бронирований")
    void getAllBookings_happyPath() throws Exception {
        Long eventId = createAndSaveTestEventId();
        createTestBookingAndGetId(eventId);

        mockMvc.perform(get("/bookings")
                        .with(user("user").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    @DisplayName("успешное удаление (отмена) бронирования")
    void removeBooking_happyPath() throws Exception {
        Long eventId = createAndSaveTestEventId();
        int bookingId = createTestBookingAndGetId(eventId);

        mockMvc.perform(delete("/bookings/{id}", bookingId)
                        .with(user("user").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
}