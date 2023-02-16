package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;

@Component
public class EndpointHitMapper {
    public static EndpointHit toHit(EndpointHitDto dto) {
        return EndpointHit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}
