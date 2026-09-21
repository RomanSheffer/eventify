package com.github.romansheffer.eventify.mapper;

import com.github.romansheffer.eventify.dto.NotificationPreferences;
import com.github.romansheffer.eventify.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationPreferences toDto(Notification notification);

    Notification toEntity(NotificationPreferences notificationPreferences);


}
