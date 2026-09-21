package com.github.romansheffer.eventify.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import com.github.romansheffer.eventify.dto.EventCreateRequest;
import com.github.romansheffer.eventify.dto.EventResponse;
import com.github.romansheffer.eventify.dto.EventUpdateRequest;
import com.github.romansheffer.eventify.exceptions.ErrorDetailException;
import com.github.romansheffer.eventify.mapper.EventMapper;
import com.github.romansheffer.eventify.model.Event;
import com.github.romansheffer.eventify.repository.BookingRepository;
import com.github.romansheffer.eventify.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventServiceImpl eventService;

    @Test
    @DisplayName("успешное создание мероприятия")
    void createEvent_Success() {

        // arrange
        EventCreateRequest request = new EventCreateRequest();
        request.setTitle("Rock Concert");
        request.setDescription("Live music");
        request.setCoverUrl("cover.jpg");
        request.setDateTime(Instant.now().plus(5, ChronoUnit.DAYS));

        request.setTotalTickets(100L);

        Event savedEvent = new Event();
        EventResponse expectedResponse = new EventResponse();

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        when(eventMapper.toDto(savedEvent)).thenReturn(expectedResponse);

        // act
        EventResponse actualResponse = eventService.createEvent(request);

        // asserts
        assertNotNull(actualResponse);
        verify(eventRepository).save(argThat(event ->
                event.getTitle().equals("Rock Concert") &&
                        event.getAvailableTickets().equals(100L)
        ));
    }

    @Test
    @DisplayName(" успешно получить мероприятие по id ")
    void getEventById_Success() {

        // arrange
        Long eventId = 1L;
        Event event = new Event();
        EventResponse expectedResponse = new EventResponse();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(expectedResponse);

        // act
        EventResponse actualResponse = eventService.getEventById(eventId);

        // asserts
        assertNotNull(actualResponse);
        verify(eventRepository).findById(eventId);
    }

    @Test
    @DisplayName("если мероприятие не найдено по id - ошибка")
    void getEventById_ThrowsException_WhenNotFound() {

        // arrange
        Long eventId = 99L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // act, asserts
        ErrorDetailException exception = assertThrows(ErrorDetailException.class, () -> {
            eventService.getEventById(eventId);
        });

        assertEquals("id", exception.getField());
        assertEquals("мероприятие не найдено", exception.getMessage());
    }

    @Test
    @DisplayName("успешно получить все мероприятия")
    void getAllEvents_WithProvidedDates_Success() {

        // arrange
        Instant from = Instant.now();
        Instant to = from.plus(10, ChronoUnit.DAYS);
        Pageable pageable = PageRequest.of(0, 10);

        Event event = new Event();
        Page<Event> mockPage = new PageImpl<>(List.of(event), pageable, 1);
        EventResponse expectedResponse = new EventResponse();

        when(eventRepository.findAllByDateTimeBetween(from, to, pageable)).thenReturn(mockPage);
        when(eventMapper.toDto(event)).thenReturn(expectedResponse);

        // act
        Page<EventResponse> resultPage = eventService.getAllEvents(from, to, pageable);

        // asserts
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        verify(eventRepository).findAllByDateTimeBetween(from, to, pageable);
    }

    @Test
    @DisplayName("получение всех мероприятий без указания дат - успешно отдать все в пределах 1970-3000 ")
    void getAllEvents_WithNullDates_UsesDefaultFallback() {

        // arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> mockPage = new PageImpl<>(List.of());

        Instant expectedStart = Instant.EPOCH;
        Instant expectedEnd = Instant.parse("3000-01-01T00:00:00Z");

        when(eventRepository.findAllByDateTimeBetween(expectedStart, expectedEnd, pageable))
                .thenReturn(mockPage);

        // act
        eventService.getAllEvents(null, null, pageable);

        // asserts
        verify(eventRepository).findAllByDateTimeBetween(expectedStart, expectedEnd, pageable);
    }

    @Test
    @DisplayName(" успешное обновление мероприятия")
    void updateEvent_Success() {

        // arrange
        Long eventId = 1L;
        EventUpdateRequest updateRequest = new EventUpdateRequest();
        updateRequest.setTitle("New Title");
        updateRequest.setTotalTickets(150L);

        Event existingEvent = new Event();
        existingEvent.setTitle("Old Title");

        EventResponse expectedResponse = new EventResponse();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        when(eventRepository.save(existingEvent)).thenReturn(existingEvent);
        when(eventMapper.toDto(existingEvent)).thenReturn(expectedResponse);

        // act
        EventResponse actualResponse = eventService.updateEvent(eventId, updateRequest);

        // asserts
        assertNotNull(actualResponse);
        assertEquals("New Title", existingEvent.getTitle());
        verify(eventRepository).save(existingEvent);
    }

    @Test
    @DisplayName("успешное удаление мероприятия")
    void removeEvent_Success() {

        // arrange
        Long eventId = 1L;
        Event event = new Event();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // act
        String result = eventService.removeEvent(eventId);

        // asserts
        assertEquals("мероприятие успешно удалено", result);

        verify(bookingRepository).deleteByEventId(eventId);
        verify(eventRepository).delete(event);
    }
}