package com.github.romansheffer.eventify.exceptions;


import java.util.List;

public record ErrorResponse(
        String code,
        String level,
        String message,
        List<ErrorDetail> details
) {}

