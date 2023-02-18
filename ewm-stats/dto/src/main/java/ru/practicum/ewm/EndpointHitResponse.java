package ru.practicum.ewm;

import java.time.LocalDateTime;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Valid
@Builder
@AllArgsConstructor
public class EndpointHitResponse {

    private Long id;

    private String app;

    private String uri;

    private String ip;

    private LocalDateTime timestamp;
}
