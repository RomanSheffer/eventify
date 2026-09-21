package com.github.romansheffer.eventify.controller;

import com.github.romansheffer.eventify.dto.EventResponse;
import com.github.romansheffer.eventify.services.EventServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/events")
@Tag(name = "управление мероприятиями")
@RequiredArgsConstructor
public class EventController {

    private final EventServiceImpl eventServiceImpl;

    @GetMapping("/{id}")
    @Operation(summary = "получить мероприятие по id", description = "только для админов")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id){

        log.info(" Получить мероприятие с id {}", id);
        EventResponse foundEvent = eventServiceImpl.getEventById(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(foundEvent);

    }

    @GetMapping
    @Operation(summary = "получить все мероприятия по фильтру")
    public ResponseEntity<Page<EventResponse>> getAllEventsBeetwenDates(
            @RequestParam(required = false) Instant from,
            @RequestParam(required = false) Instant to,
            @ParameterObject Pageable pageable
    ){
        log.info("Получить все мероприятия ");
        Page<EventResponse> allEvents = eventServiceImpl.getAllEvents(from, to, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allEvents);
    }

}
