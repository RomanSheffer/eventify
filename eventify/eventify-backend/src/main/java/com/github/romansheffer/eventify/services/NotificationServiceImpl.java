package com.github.romansheffer.eventify.services;


import com.github.romansheffer.eventify.dto.NotificationPreferences;
import com.github.romansheffer.eventify.exceptions.ErrorDetailException;
import com.github.romansheffer.eventify.mapper.NotificationMapper;
import com.github.romansheffer.eventify.model.Notification;
import com.github.romansheffer.eventify.model.User;
import com.github.romansheffer.eventify.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;


    @Transactional(readOnly = true)
    public NotificationPreferences getNotificationPreferences() {

        log.info("Запуск сервиса получения уведомлений пользователя");

        String currentCustomerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        return notificationRepository.findByUserEmail(currentCustomerEmail)
                .map(notificationMapper::toDto)
                .orElseThrow(() -> new ErrorDetailException("id", "настройки уведомлений не найдены", HttpStatus.NOT_FOUND));

    }

    @Transactional
    public NotificationPreferences updateNotifications(NotificationPreferences notificationPreferences) {

        log.info("Запуск сервиса обновления уведомлений пользователя");
        String currentCustomerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Notification foundNotifications = notificationRepository.findByUserEmail(currentCustomerEmail)
                .orElseThrow(() -> new ErrorDetailException("id", "настройки уведомлений не найдены", HttpStatus.NOT_FOUND));

        foundNotifications.setNotifyUpcoming(notificationPreferences.isNotifyUpcoming());
        foundNotifications.setNotifyNewEvents(notificationPreferences.isNotifyNewEvents());
        foundNotifications.setNotifyBeforeHours(notificationPreferences.getNotifyBeforeHours());

        Notification returnedNotifications = notificationRepository.save(foundNotifications);

        return notificationMapper.toDto(returnedNotifications);

    }

    @Transactional
    public String removeNotifications(){

        log.info("Запуск сервиса отключения уведомлений пользователя");

        String answer = "Уведомления отключены";
        String currentCustomerEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Notification foundNotification = notificationRepository.findByUserEmail(currentCustomerEmail)
                .orElseThrow(() -> new ErrorDetailException("id", "настройки уведомлений не найдены", HttpStatus.NOT_FOUND));

        foundNotification.setNotifyUpcoming(false);
        foundNotification.setNotifyNewEvents(false);
        foundNotification.setNotifyBeforeHours(0L);

        return  answer;
    }

    @Transactional
    public void createNotifications(User user){

        log.info("Запуск сервиса создания уведомлений пользователя(при создании пользователя)");

        Notification newNotifications = new Notification();
        newNotifications.setUser(user);
        notificationRepository.save(newNotifications);

    }
}
