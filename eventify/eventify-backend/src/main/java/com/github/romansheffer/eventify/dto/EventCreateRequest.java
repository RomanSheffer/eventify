package com.github.romansheffer.eventify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventCreateRequest {

    @Size(min = 0, max = 100, message = "длинна названия мероприятия")
    @Schema(description = "название мероприятия, от 0 до 100 символов")
    private String title;

    private String description;

    private String coverUrl;

    @NotNull(message = "Дата и время обязательны для заполнения")
    @Future(message = "Мероприятие должно быть запланировано на будущее время")
    @Schema(
            description = "Дата и время проведения",
            example = "2026-10-25T18:00:00"
    )
    private Instant dateTime;

    @Min(1)
    @Schema(description = "Количество билетов", minimum = "1")
    private Long totalTickets;

}
