package com.github.romansheffer.eventify.services;

import com.github.romansheffer.eventify.dto.EventCreateRequest;
import com.github.romansheffer.eventify.dto.EventResponse;
import com.github.romansheffer.eventify.dto.EventUpdateRequest;
import com.github.romansheffer.eventify.exceptions.ErrorDetailException;
import com.github.romansheffer.eventify.mapper.EventMapper;
import com.github.romansheffer.eventify.model.Event;
import com.github.romansheffer.eventify.repository.BookingRepository;
import com.github.romansheffer.eventify.repository.EventRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final EventMapper eventMapper;

    @Transactional
    public EventResponse createEvent(EventCreateRequest eventCreateRequest) {

        log.info("запуск сервиса создания мероприятия");

        Event savedEvent = new Event();

        savedEvent.setTitle(eventCreateRequest.getTitle());
        savedEvent.setDescription(eventCreateRequest.getDescription());
        savedEvent.setCoverUrl(eventCreateRequest.getCoverUrl());
        savedEvent.setDateTime(eventCreateRequest.getDateTime());
        savedEvent.setTotalTickets(eventCreateRequest.getTotalTickets());
        savedEvent.setAvailableTickets(eventCreateRequest.getTotalTickets());

        Event returnedEvent = eventRepository.save(savedEvent);

        return eventMapper.toDto(returnedEvent);

    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(Long id) {

        log.info("запуск сервиса получения  мероприятия с id {}", id);

        Event foundEvent = eventRepository.findByIdWithBookings(id)
                .orElseThrow(() -> new ErrorDetailException("id", "мероприятие не найдено", HttpStatus.NOT_FOUND));

        return eventMapper.toDto(foundEvent);

    }

    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(Instant from, Instant to, Pageable pageable) {

        log.info("запуск сервиса получения  всех мероприятий");

        Instant start = (from != null) ? from : Instant.EPOCH;
        Instant end = (to != null) ? to : Instant.parse("3000-01-01T00:00:00Z");

        Page<Event> eventPage = eventRepository.findAllByDateTimeBetween(start, end, pageable);

        return eventPage.map(eventMapper::toDto);

    }

    @Transactional
    public EventResponse updateEvent(Long id, EventUpdateRequest eventUpdateRequest) {

        log.info("запуск сервиса обновления мероприятия с id {}", id);

        Event foundEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ErrorDetailException("id", "мероприятие не найдено", HttpStatus.NOT_FOUND));

        foundEvent.setTitle(eventUpdateRequest.getTitle());
        foundEvent.setDescription(eventUpdateRequest.getDescription());
        foundEvent.setDateTime(eventUpdateRequest.getDateTime());
        foundEvent.setTotalTickets(eventUpdateRequest.getTotalTickets());
        foundEvent.setCoverUrl(eventUpdateRequest.getCoverUrl());


        Event returnedEvent = eventRepository.save(foundEvent);

        return eventMapper.toDto(returnedEvent);

    }

    @Transactional
    public String removeEvent(Long id) {

        log.info("запуск сервиса удаления  мероприятия с id {}", id);

        Event foundEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ErrorDetailException("id", "мероприятие не найдено", HttpStatus.NOT_FOUND));

        bookingRepository.deleteByEventId(id);

        eventRepository.delete(foundEvent);

        return "мероприятие успешно удалено";

    }

}
