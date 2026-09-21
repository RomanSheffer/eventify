package com.github.romansheffer.eventify.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingRequest {

    Long eventId;

  @Min(1)
  @Schema(description = "Количество билетов", minimum = "1")
  Long ticketCount;

}
