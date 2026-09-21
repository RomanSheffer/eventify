package com.github.romansheffer.eventify.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Internal;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
@RequiredArgsConstructor
public class NotificationPreferences {

    private boolean notifyNewEvents;
    private boolean notifyUpcoming;
    @Range(min = 1, max = 24, message = "Количество часов должно быть в диапазоне от 1 до 24")
    @Schema(description = "За сколько часов уведомлять")
   private Long notifyBeforeHours;

}
