package com.github.romansheffer.eventify.services;

import com.github.romansheffer.eventify.dto.BookingResponse;
import com.github.romansheffer.eventify.dto.CreateBookingRequest;
import com.github.romansheffer.eventify.exceptions.ErrorDetailException;
import com.github.romansheffer.eventify.mapper.BookingMapper;
import com.github.romansheffer.eventify.model.Booking;
import com.github.romansheffer.eventify.model.Event;
import com.github.romansheffer.eventify.repository.BookingRepository;
import com.github.romansheffer.eventify.repository.EventRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private EventRepository eventRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;
    @Mock private UserDetails userDetails;

    private final String userEmail = "user@test.com";

    @BeforeEach
    void setUp() {

        mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void tearDown() {
                mockedSecurityContextHolder.close();
    }

    @Test
    @DisplayName(" успешно получить бронь по id")
    void getBookingById_Success() {

        // arrange
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setCustomerEmail(userEmail);
        BookingResponse expectedResponse = new BookingResponse();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(userEmail);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(booking)).thenReturn(expectedResponse);

        //act
        BookingResponse actualResponse = bookingService.getBookingById(bookingId);

        //asserts
        assertNotNull(actualResponse);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    @DisplayName("получить бронь по id если доступ запрещен - ошибка")
    void getBookingById_ThrowsException_WhenAccessDenied() {

        // arrange
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setCustomerEmail("another_user@test.com");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(userEmail);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        //asserts
        ErrorDetailException exception = assertThrows(ErrorDetailException.class, () -> {
            bookingService.getBookingById(bookingId);
        });

        assertEquals("auth", exception.getField());
        assertEquals("ошибка доступа", exception.getMessage());
    }

    @Test
    @DisplayName(" успешно удалить чужую бронь админом")
    void removeBooking_Success_ByAdmin() {

        // arrange
        Long bookingId = 5L;
        Booking booking = new Booking();
        booking.setCustomerEmail("someone_else@test.com");

        Event event = new Event();
        event.setAvailableTickets(10L);
        booking.setEvent(event);
        booking.setTicketCount(2L);

        when(authentication.getName()).thenReturn(userEmail);

        GrantedAuthority adminAuthority = () -> "ADMIN";
        doReturn(List.of(adminAuthority)).when(authentication).getAuthorities();

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        String result = bookingService.removeBooking(bookingId);

        // asserts
        assertEquals("успешно удалено", result);
        assertEquals(12L, event.getAvailableTickets());
        verify(eventRepository).save(event);
        verify(bookingRepository).delete(booking);
    }

    @Test
    @DisplayName(" успешно создать бронь ")
    void createBooking_Success() {

        // arrange
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(10L);
        request.setTicketCount(3L);

        Event event = new Event();
        event.setAvailableTickets(20L);

        Booking savedBooking = new Booking();
        BookingResponse expectedResponse = new BookingResponse();

        when(authentication.getName()).thenReturn(userEmail);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
        when(bookingMapper.toDto(savedBooking)).thenReturn(expectedResponse);

        // act
        BookingResponse actualResponse = bookingService.createBooking(request);

        // asserts
        assertNotNull(actualResponse);
        assertEquals(17L, event.getAvailableTickets());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    @DisplayName(" успешно обновить бронь ")
    void updateBooking_Success() {

        // arrange
        Long bookingId = 1L;
        Long newTicketCount = 5L;

        Booking foundBooking = new Booking();
        foundBooking.setId(bookingId);
        foundBooking.setCustomerEmail(userEmail);
        foundBooking.setTicketCount(2L);
        foundBooking.setConfirmed(true);

        BookingResponse expectedResponse = new BookingResponse();

        when(authentication.getName()).thenReturn(userEmail);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(foundBooking));

        when(bookingRepository.save(foundBooking)).thenReturn(foundBooking);
        when(bookingMapper.toDto(foundBooking)).thenReturn(expectedResponse);

        // act
        BookingResponse actualResponse = bookingService.updateBooking(bookingId, newTicketCount);

        // asserts
        assertNotNull(actualResponse);

        assertEquals(newTicketCount, foundBooking.getTicketCount());
        assertFalse(foundBooking.getConfirmed());

        verify(bookingRepository).save(foundBooking);
    }

    @Test
    @DisplayName(" обновить бронь если  доступ запрещен - ошибка")
    void updateBooking_ThrowsException_WhenAccessDenied() {

        // arrange
        Long bookingId = 1L;
        Long newTicketCount = 5L;

        Booking foundBooking = new Booking();
        foundBooking.setCustomerEmail("hacker@test.com");

        when(authentication.getName()).thenReturn(userEmail);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(foundBooking));

        //act
        ErrorDetailException exception = assertThrows(ErrorDetailException.class, () -> {
            bookingService.updateBooking(bookingId, newTicketCount);
        });

        //asserts
        assertEquals("auth", exception.getField());
        assertEquals("ошибка доступа", exception.getMessage());

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName(" успешно получить свои брони юзером")
    void getAllBookingByUser_Success() {

        // arrange
        Booking booking1 = new Booking();
        Booking booking2 = new Booking();
        List<Booking> mockBookings = List.of(booking1, booking2);

        BookingResponse response1 = new BookingResponse();
        BookingResponse response2 = new BookingResponse();

        when(authentication.getName()).thenReturn(userEmail);
        when(bookingRepository.findByCustomerEmail(userEmail)).thenReturn(mockBookings);
        when(bookingMapper.toDto(booking1)).thenReturn(response1);
        when(bookingMapper.toDto(booking2)).thenReturn(response2);

        // act
        List<BookingResponse> result = bookingService.getAllBookingByUser();

        // arrange
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingRepository).findByCustomerEmail(userEmail);
    }

    @Test
    @DisplayName(" успешно получить все брони с пагинацией")
    void getAllBookings_WithPagination_Success() {

        // arrange
        Long eventId = 10L;
        Boolean unconfirmedOnly = true;

        Pageable pageable = PageRequest.of(0, 10);
        Booking booking = new Booking();
        Page<Booking> mockPage = new PageImpl<>(List.of(booking), pageable, 1);

        BookingResponse expectedResponse = new BookingResponse();

        when(bookingRepository.findAllByEventIdAndConfirmed(eventId, false, pageable))
                .thenReturn(mockPage);
        when(bookingMapper.toDto(booking)).thenReturn(expectedResponse);

        // act
        Page<BookingResponse> resultPage = bookingService.getAllBookings(eventId, unconfirmedOnly, pageable);

        // asserts
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(expectedResponse, resultPage.getContent().get(0));

        verify(bookingRepository).findAllByEventIdAndConfirmed(eventId, false, pageable);
    }

    @Test
    @DisplayName(" успешно подтвердить бронь админом")
    void confirmBooking_Success() {

        // arrange
        Long bookingId = 1L;
        Booking foundBooking = new Booking();
        foundBooking.setId(bookingId);
        foundBooking.setConfirmed(false);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(foundBooking));

        // act
        String result = bookingService.confirmBooking(bookingId);

        // asserts
        assertEquals("бронирование 1 подтверждено", result);
        assertTrue(foundBooking.getConfirmed());
    }

}