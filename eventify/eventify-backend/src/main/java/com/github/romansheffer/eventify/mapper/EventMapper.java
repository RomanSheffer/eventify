package com.github.romansheffer.eventify.mapper;


import com.github.romansheffer.eventify.dto.EventResponse;
import com.github.romansheffer.eventify.model.Event;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {


     EventResponse toDto(Event event);

    Event toEntity(EventResponse eventResponse);





}
