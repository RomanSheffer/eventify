package com.github.romansheffer.eventify.services;

import com.github.romansheffer.eventify.dto.BookingResponse;
import com.github.romansheffer.eventify.dto.CreateBookingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {

    BookingResponse getBookingById(Long id);

    List<BookingResponse> getAllBookingByUser();

    Page<BookingResponse> getAllBookings(
            Long eventId,
            Boolean unconfirmedOnly,
            Pageable pageable
    );

    BookingResponse updateBooking(Long id, Long newTicketCount);

    String removeBooking(Long id);

    BookingResponse createBooking(CreateBookingRequest createBookingRequest);

    String confirmBooking(Long id);


}
