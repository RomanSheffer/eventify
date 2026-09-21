package com.github.romansheffer.eventify.services;

import com.github.romansheffer.eventify.dto.NotificationPreferences;
import com.github.romansheffer.eventify.model.User;

public interface NotificationService {

    NotificationPreferences getNotificationPreferences();

    NotificationPreferences updateNotifications(NotificationPreferences notificationPreferences);

    String removeNotifications();

    void createNotifications(User user);

}
