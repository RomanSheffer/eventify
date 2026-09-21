package com.github.romansheffer.eventify.controller;


import com.github.romansheffer.eventify.dto.NotificationPreferences;
import com.github.romansheffer.eventify.services.NotificationService;
import com.github.romansheffer.eventify.services.NotificationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "панель юзера")
public class UserController {

    private final NotificationServiceImpl notificationServiceImpl;

    @GetMapping("/notifications")
    @Operation(summary = "получить настройки уведомлений ")
    public ResponseEntity<NotificationPreferences> getNotificationPreferences() {

        log.info("получить настройки уведомлений");
        NotificationPreferences notificationPreferences = notificationServiceImpl.getNotificationPreferences();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationPreferences);
    }

    @PutMapping("/notifications")
    public ResponseEntity<NotificationPreferences> updateNotifications(@Valid @RequestBody NotificationPreferences notificationPreferences) {

        log.info("изменить настройки уведомлений");
        NotificationPreferences returnedNotifications = notificationServiceImpl.updateNotifications(notificationPreferences);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(returnedNotifications);
    }

    @DeleteMapping("/notifications")
    public ResponseEntity<String> removeNotifications(){

        log.info("удалить настройки уведомлений");
        String answer = notificationServiceImpl.removeNotifications();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(answer);


    }


}
