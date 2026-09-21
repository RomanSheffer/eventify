package com.github.romansheffer.eventify.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String email;
    private String password;

    public RegisterRequest() {}


}
