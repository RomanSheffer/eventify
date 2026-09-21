package com.github.romansheffer.eventify.controller;


import com.github.romansheffer.eventify.dto.*;

import com.github.romansheffer.eventify.services.BookingServiceImpl;
import com.github.romansheffer.eventify.services.EventServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
@Tag(name = "Панель администратора")
public class AdminController {

    private final EventServiceImpl eventServiceImpl;
    private final BookingServiceImpl bookingServiceImpl;


    @PutMapping("/events/{id}")
    @Operation(summary = " обновить информацию о мероприятии", description = "только для админов")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateRequest eventUpdateRequest ) {

        log.info("Обновление информации о мероприятии с id: {}", id);
        EventResponse returnedEvent = eventServiceImpl.updateEvent(id, eventUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(returnedEvent);

    }

    @DeleteMapping("/events/{id}")
    @Operation(summary = "удалить мероприятие", description = "только для админов")
    public ResponseEntity<String> removeEvent(@PathVariable Long id) {

        log.info("удаление мероприятия id: {}", id);

        String removingLabel = eventServiceImpl.removeEvent(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(removingLabel);
    }

    @PutMapping("/bookings/{id}/confirm")
    @Operation(summary = "подтвердить бронь ", description = "только для админов")
    public ResponseEntity<String> confirmEvent(@PathVariable Long id) {

        log.info("Подтвержение брони на мероприятие с id: {}", id);

        String answer = bookingServiceImpl.confirmBooking(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(answer);

    }

    @PostMapping("/events")
    @Operation(summary = "создать мероприятие", description = "только для админов")
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest eventCreateRequest ) {

        log.info("Создание мероприятия");
        EventResponse returnedEvent = eventServiceImpl.createEvent(eventCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(returnedEvent);
    }

    @GetMapping("/bookings")
    @Operation(summary = "получить все бронирования по фильтру", description = "только для админов")
    public ResponseEntity<Page<BookingResponse>> getAllEvents(

            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Boolean unconfirmedOnly,
            @ParameterObject Pageable pageable
            ){

           log.info("Получение всех броней");
        Page<BookingResponse> allBookings = bookingServiceImpl.getAllBookings(eventId, unconfirmedOnly, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allBookings);
    }


    @DeleteMapping("/bookings/{id}")
    @Operation(summary = "удалить бронь ", description = "только для админов")
    public ResponseEntity<String> removeBooking(@PathVariable Long id) {

        log.info("Получение  брони с id: {}", id);
        String answer = bookingServiceImpl.removeBooking(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(answer);
    }



}



