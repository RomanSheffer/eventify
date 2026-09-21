package com.github.romansheffer.eventify.services;

import com.github.romansheffer.eventify.dto.EventCreateRequest;
import com.github.romansheffer.eventify.dto.EventResponse;
import com.github.romansheffer.eventify.dto.EventUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDateTime;

public interface EventService {


    EventResponse createEvent(EventCreateRequest eventCreateRequest);

    EventResponse getEventById(Long id);

    Page<EventResponse> getAllEvents(Instant from, Instant to, Pageable pageable);

    EventResponse updateEvent(Long id, EventUpdateRequest eventUpdateRequest);

    String removeEvent(Long id);


}
