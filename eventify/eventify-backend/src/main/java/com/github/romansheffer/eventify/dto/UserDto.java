package com.github.romansheffer.eventify.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDto {

      private UUID id;

     private String email;

    private String firstName;

    private String lastName;

    private String role;

    private List<String> eventNames;

    private List<String> bookingNames;


}
