package com.github.romansheffer.eventify.services;


import com.github.romansheffer.eventify.dto.BookingResponse;
import com.github.romansheffer.eventify.dto.CreateBookingRequest;
import com.github.romansheffer.eventify.exceptions.ErrorDetailException;
import com.github.romansheffer.eventify.mapper.BookingMapper;
import com.github.romansheffer.eventify.model.Booking;
import com.github.romansheffer.eventify.model.Event;
import com.github.romansheffer.eventify.repository.BookingRepository;
import com.github.romansheffer.eventify.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final BookingMapper bookingMapper;


    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {

        log.info("запуск сервиса получения брони по id {}", id);
        Booking foundBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ErrorDetailException("id", "бронирование не найдено", HttpStatus.NOT_FOUND));

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentCustomerEmail = ((UserDetails) principal).getUsername();

        if (!foundBooking.getCustomerEmail().equals(currentCustomerEmail)) {
            throw new ErrorDetailException("auth", "ошибка доступа", HttpStatus.FORBIDDEN);

        }
        return bookingMapper.toDto(foundBooking);

    }

    public List<BookingResponse> getAllBookingByUser() {

        log.info("запуск сервиса получения всех броней пользователя");
        String currentCustomerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        List<Booking> allBookings = bookingRepository.findByCustomerEmail(currentCustomerEmail);

        return allBookings.stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    public Page<BookingResponse> getAllBookings(
            Long eventId,
            Boolean unconfirmedOnly,
            Pageable pageable
    ) {
        log.info("запуск сервиса получения всех броней ");
        Boolean confirmedParam = (unconfirmedOnly != null && unconfirmedOnly) ? false : null;

        Page<Booking> allBookingsPage = bookingRepository.findAllByEventIdAndConfirmed(eventId, confirmedParam, pageable);

        return allBookingsPage.map(bookingMapper::toDto);


    }


    @Transactional
    public BookingResponse updateBooking(Long id, Long newTicketCount) {

        log.info("запуск сервиса обновления брони с  id {}", id);

        Booking foundBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ErrorDetailException("id", "бронирование не найдено", HttpStatus.NOT_FOUND));

        String currentCustomerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!foundBooking.getCustomerEmail().equals(currentCustomerEmail)) {
            throw new ErrorDetailException("auth", "ошибка доступа", HttpStatus.UNAUTHORIZED);
        }

        foundBooking.setTicketCount(newTicketCount);
        foundBooking.setConfirmed(false);
        Booking returnedBooking = bookingRepository.save(foundBooking);

        return bookingMapper.toDto(returnedBooking);


    }


    @Transactional
    public String removeBooking(Long id) {

        log.info("запуск сервиса удаления брони с id {}", id);
        Booking foundBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ErrorDetailException("id", "бронирование не найдено", HttpStatus.NOT_FOUND));

        String currentCustomerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));

        if (!foundBooking.getCustomerEmail().equals(currentCustomerEmail) && !isAdmin) {
            throw new ErrorDetailException("auth", "ошибка доступа", HttpStatus.FORBIDDEN);
        }

        //возвращаем белеты в евент при удалении брони
        Event foundEvent = foundBooking.getEvent();
        foundEvent.setAvailableTickets(foundEvent.getAvailableTickets() + foundBooking.getTicketCount());
        eventRepository.save(foundEvent);
        bookingRepository.delete(foundBooking);

        return "успешно удалено";
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest createBookingRequest) {

        log.info("запуск сервиса создания брони ");

        String currentCustomerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Booking savedBooking = new Booking();
        Event foundEvent = eventRepository.findById(createBookingRequest.getEventId())
                .orElseThrow(() -> new ErrorDetailException("id", "бронирование не найдено", HttpStatus.NOT_FOUND));

        foundEvent.setAvailableTickets(foundEvent.getAvailableTickets() - createBookingRequest.getTicketCount());

        savedBooking.setCreatedAt(Instant.now());
        savedBooking.setTicketCount(createBookingRequest.getTicketCount());
        savedBooking.setEvent(foundEvent);
        savedBooking.setCustomerEmail(currentCustomerEmail);
        savedBooking.setConfirmed(false);

        Booking persistedBooking = bookingRepository.save(savedBooking);


        return bookingMapper.toDto(persistedBooking);

    }

    @Transactional
    public String confirmBooking(Long id) {

        log.info("запуск сервиса подтвержения брони");

        Booking foundBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ErrorDetailException("id", "бронирование не найдено", HttpStatus.NOT_FOUND));

        foundBooking.setConfirmed(true);

        return "бронирование " + id + " подтверждено";

    }


}
