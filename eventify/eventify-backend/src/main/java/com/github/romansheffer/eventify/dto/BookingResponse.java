package com.github.romansheffer.eventify.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponse {


    private Long id;

    private EventResponse event;

    private String customerEmail;

    private Long ticketCount;

    private Boolean confirmed;

    private Instant createdAt;

    private Instant expiryTime;


}
