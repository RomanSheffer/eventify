package com.github.romansheffer.eventify.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class EventResponse {

    private Long id;

    private String title;

    private String description;

    private Instant dateTime;

    private Long totalTickets;

    private Long availableTickets;

    private String coverUrl;



}
