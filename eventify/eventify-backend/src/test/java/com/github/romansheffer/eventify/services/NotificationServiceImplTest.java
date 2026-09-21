package com.github.romansheffer.eventify.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.github.romansheffer.eventify.dto.NotificationPreferences;
import com.github.romansheffer.eventify.exceptions.ErrorDetailException;
import com.github.romansheffer.eventify.mapper.NotificationMapper;
import com.github.romansheffer.eventify.model.Notification;
import com.github.romansheffer.eventify.model.User;
import com.github.romansheffer.eventify.repository.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private static final String USER_EMAIL = "user@test.com";

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
    @DisplayName("успешно получить уведомления")
    void getNotificationPreferences_Success() {

        // arrange
        Notification notification = new Notification();
        NotificationPreferences expectedDto = new NotificationPreferences();

        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(notificationRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(notification));
        when(notificationMapper.toDto(notification)).thenReturn(expectedDto);

        // act
        NotificationPreferences actualDto = notificationService.getNotificationPreferences();

        // asserts
        assertNotNull(actualDto);
        verify(notificationRepository).findByUserEmail(USER_EMAIL);
    }

    @Test
    @DisplayName(" получить несуществующие уведомления - ошибка")
    void getNotificationPreferences_ThrowsException_WhenNotFound() {

        // arrange
        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(notificationRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.empty());

        // act, asserts
        ErrorDetailException exception = assertThrows(ErrorDetailException.class, () -> {
            notificationService.getNotificationPreferences();
        });

        assertEquals("id", exception.getField());
        assertEquals("настройки уведомлений не найдены", exception.getMessage());
    }

    @Test
    @DisplayName("успешно обновить уведомления")
    void updateNotifications_Success() {

        // arrange
        NotificationPreferences updateRequest = new NotificationPreferences();
        updateRequest.setNotifyUpcoming(true);
        updateRequest.setNotifyNewEvents(false);
        updateRequest.setNotifyBeforeHours(3L);

        Notification existingNotification = new Notification();
        existingNotification.setNotifyUpcoming(false);

        NotificationPreferences expectedDto = new NotificationPreferences();

        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(notificationRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(existingNotification));
        when(notificationRepository.save(existingNotification)).thenReturn(existingNotification);
        when(notificationMapper.toDto(existingNotification)).thenReturn(expectedDto);

        // act
        NotificationPreferences result = notificationService.updateNotifications(updateRequest);

        // asserts
        assertNotNull(result);
        assertTrue(existingNotification.isNotifyUpcoming());
        assertFalse(existingNotification.isNotifyNewEvents());
        assertEquals(3, existingNotification.getNotifyBeforeHours());

        verify(notificationRepository).save(existingNotification);
    }

    @Test
    @DisplayName("успешно удалить уведомления")
    void removeNotifications_Success() {

        // arrange
        Notification existingNotification = new Notification();
        existingNotification.setNotifyUpcoming(true);
        existingNotification.setNotifyNewEvents(true);
        existingNotification.setNotifyBeforeHours(5L);

        when(authentication.getName()).thenReturn(USER_EMAIL);
        when(notificationRepository.findByUserEmail(USER_EMAIL)).thenReturn(Optional.of(existingNotification));

        // act
        String result = notificationService.removeNotifications();

        // asserts
        assertEquals("Уведомления отключены", result);

        assertFalse(existingNotification.isNotifyUpcoming());
        assertFalse(existingNotification.isNotifyNewEvents());
        assertEquals(0L, existingNotification.getNotifyBeforeHours());
    }

    @Test
    @DisplayName("успешно создать уведомления")
    void createNotifications_Success() {

        // arrange
        User user = new User();
        user.setEmail(USER_EMAIL);

        // act
        notificationService.createNotifications(user);

        // asserts
        verify(notificationRepository).save(argThat(notification ->
                notification.getUser() != null && notification.getUser().getEmail().equals(USER_EMAIL)
        ));

    }
}

