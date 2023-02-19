package ru.practicum.ewm.exception.model;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ApiError {

    private final String message;

    private final String reason;

    private final HttpStatus status;

    private final LocalDateTime timestamp;
}
