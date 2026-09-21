package com.github.romansheffer.eventify.exceptions;



import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ErrorDetailException.class)
    public ResponseEntity<ErrorResponse> handleErrorDetailException(ErrorDetailException ex) {

               ErrorDetail detail = new ErrorDetail(
                ex.getField(),
                ex.getMessage()
        );

        ErrorResponse errorResponse = new ErrorResponse(
                "VALIDATION_FAILED",
                "error",
                "Некоторые поля заполнены неверно",
                List.of(detail)
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockingFailure(        ObjectOptimisticLockingFailureException ex) {

        ErrorDetail detail = new ErrorDetail(
                "version",
                "Данные были изменены другим пользователем или процессом. Обновите страницу."
        );

        ErrorResponse errorResponse = new ErrorResponse(
                "DATA_CONFLICT",
                "error",
                "Конфликт параллельного изменения данных",
                List.of(detail)
        );

        return ResponseEntity
                .status(org.springframework.http.HttpStatus.CONFLICT)
                .body(errorResponse);
    }
}

