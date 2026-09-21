package com.github.romansheffer.eventify.controller;

import com.github.romansheffer.eventify.model.Booking;
import com.github.romansheffer.eventify.model.Event;
import com.github.romansheffer.eventify.repository.EventRepository;
import com.github.romansheffer.eventify.repository.BookingRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Вспомогательный метод для динамической сборки JSON мероприятия
    private String buildEventJson(String title, String description, int totalTickets) {
        return """
                {
                    "title": "%s",
                    "description": "%s",
                    "coverUrl": "https://example.com",
                    "dateTime": "2028-10-25T18:00:00Z",
                    "totalTickets": %d
                }
                """.formatted(title, description, totalTickets);
    }

    @Test
    @DisplayName("успешный запрос и ответ при создании мероприятия")
    void createEvent_happyPath() throws Exception {
        String eventCreateRequestJson = buildEventJson("Fest 2026", "Главный фестиваль года", 5000);

        mockMvc.perform(post("/admin/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventCreateRequestJson)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Fest 2026"))
                .andExpect(jsonPath("$.description").value("Главный фестиваль года"))
                .andExpect(jsonPath("$.coverUrl").value("https://example.com"))
                .andExpect(jsonPath("$.dateTime").value("2028-10-25T18:00:00Z"))
                .andExpect(jsonPath("$.totalTickets").value(5000));
    }

    @Test
    @DisplayName("успешный запрос и ответ при обновлении мероприятия")
    void updateEvent_happyPath() throws Exception {
        String initialEventJson = buildEventJson("Старый Фест", "Старое описание", 1000);

        String responseContent = mockMvc.perform(post("/admin/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(initialEventJson)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int savedEventId = com.jayway.jsonpath.JsonPath.read(responseContent, "$.id");

        String eventUpdateRequestJson = """
                {
                    "title": "Обновленный Fest 2026",
                    "description": "Новое крутое описание"
                }
                """;

        mockMvc.perform(put("/admin/events/{id}", savedEventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventUpdateRequestJson)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedEventId))
                .andExpect(jsonPath("$.title").value("Обновленный Fest 2026"))
                .andExpect(jsonPath("$.description").value("Новое крутое описание"));
    }

    @Test
    @DisplayName("успешный запрос и ответ при удалении мероприятия")
    void removeEvent_happyPath() throws Exception {
        String initialEventJson = buildEventJson("Мероприятие для удаления", "Описание", 100);

        String responseContent = mockMvc.perform(post("/admin/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(initialEventJson)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int savedEventId = com.jayway.jsonpath.JsonPath.read(responseContent, "$.id");

        mockMvc.perform(delete("/admin/events/{id}", savedEventId)
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("успешный запрос и ответ при подтверждении брони")
    void confirmEvent_happyPath() throws Exception {
        Event event = createAndSaveTestEvent();
        Booking booking = createAndSaveTestBooking(event, false);

        mockMvc.perform(put("/admin/bookings/{id}/confirm", booking.getId())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("успешный запрос и ответ при получении бронирований по фильтру")
    void getAllEvents_happyPath() throws Exception {
        Event event = createAndSaveTestEvent();
        createAndSaveTestBooking(event, false);

        mockMvc.perform(get("/admin/bookings")
                        .param("eventId", event.getId().toString())
                        .param("unconfirmedOnly", "true")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("успешный запрос и ответ при удалении бронирования")
    void removeBooking_happyPath() throws Exception {
        Event event = createAndSaveTestEvent();
        Booking booking = createAndSaveTestBooking(event, true);

        mockMvc.perform(delete("/admin/bookings/{id}", booking.getId())
                        .with(user("admin").authorities(new SimpleGrantedAuthority("ADMIN")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ошибка 403 при попытке создания мероприятия обычным пользователем")
    void createEvent_asUser_shouldReturnForbidden() throws Exception {
        String eventJson = buildEventJson("Секретный Фест", "Обычный юзер не должен его создать", 100);

        mockMvc.perform(post("/admin/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson)
                        .with(user("user").authorities(new SimpleGrantedAuthority("USER")))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    private Event createAndSaveTestEvent() {
        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Description");
        event.setCoverUrl("https://example.com");
        event.setTotalTickets(100L);
        event.setAvailableTickets(100L);
        event.setDateTime(Instant.now().plus(5, ChronoUnit.DAYS));
        return eventRepository.save(event);
    }

    private Booking createAndSaveTestBooking(Event event, boolean confirmed) {
        Booking booking = new Booking();
        booking.setEvent(event);
        booking.setCustomerEmail("customer@test.com");
        booking.setTicketCount(2L);
        booking.setConfirmed(confirmed);
        booking.setCreatedAt(Instant.now());
        booking.setExpiryTime(Instant.now().plus(15, ChronoUnit.MINUTES));
        return bookingRepository.save(booking);
    }
}