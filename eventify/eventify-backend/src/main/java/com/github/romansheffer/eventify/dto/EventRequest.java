package com.github.romansheffer.eventify.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class EventRequest {

 String title;

 String description;

 String coverUrl;

 Instant dateTime;

 Long totalTickets;

}


