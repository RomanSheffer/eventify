package com.github.romansheffer.eventify.mapper;

import com.github.romansheffer.eventify.dto.UserDto;
import com.github.romansheffer.eventify.model.Booking;
import com.github.romansheffer.eventify.model.Event;
import com.github.romansheffer.eventify.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;


@Mapper(componentModel = "spring")
public interface UserMapper {


     UserDto toDto(User user);

    User toEntity(UserDto userDto);





}
