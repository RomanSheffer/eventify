package com.github.romansheffer.eventify.controller;


import com.github.romansheffer.eventify.dto.BookingResponse;
import com.github.romansheffer.eventify.dto.CreateBookingRequest;
import com.github.romansheffer.eventify.dto.UpdateBookingRequest;
import com.github.romansheffer.eventify.services.BookingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
@Tag(name = "Бронирования", description = "Операции для работы с бронированиями")
public class BookingController {

    private final BookingServiceImpl bookingServiceImpl;

    @GetMapping("/{id}")
    @Operation(summary = "Получить бронирование по ID", description = "Доступен юзеру при совпадении id или админу")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {

        log.info("получить бронь по id {}", id);
        BookingResponse foundBooking = bookingServiceImpl.getBookingById(id);


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(foundBooking);
    }

    @PutMapping("/{id}")
    @Operation(summary = "обновить бронирование по ID", description = "Доступен юзеру при совпадении id или админу")
    public ResponseEntity<BookingResponse> updateBooking(@PathVariable Long id,@Valid  @RequestBody UpdateBookingRequest updateBookingRequest) {

        log.info("Обновление брони с id {}", id);
        Long newTicketCount = updateBookingRequest.getTicketCount();
        BookingResponse updatedBooking = bookingServiceImpl.updateBooking(id, newTicketCount);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedBooking);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "удалить(отменить) бронирование по ID", description = "Доступен юзеру при совпадении id или админу")
    public ResponseEntity<String> removeBooking(@PathVariable Long id) {

        log.info(" удаление брони с id {}", id);
        String answer = bookingServiceImpl.removeBooking(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(answer);
    }

    @GetMapping
    @Operation(summary = "получить все свои бронирования", description = "Доступен юзеру при совпадении id или админу")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {

        log.info("получить все бронирования");
        List<BookingResponse> allBookings = bookingServiceImpl.getAllBookingByUser();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allBookings);
    }

    @PostMapping
    @Operation(summary = "создать бронирование ")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest createBookingRequest) {

        log.info("Создать новое бронирование");
        BookingResponse createdBooking = bookingServiceImpl.createBooking(createBookingRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(createdBooking);
    }


}
