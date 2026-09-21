package com.github.romansheffer.eventify.mapper;


import com.github.romansheffer.eventify.dto.BookingResponse;
import com.github.romansheffer.eventify.model.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {


     BookingResponse toDto(Booking booking);

     Booking toEntity(BookingResponse bookingResponse);




    }


