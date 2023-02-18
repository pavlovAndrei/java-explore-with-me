package ru.practicum.ewm.mapper;

import org.springframework.stereotype.Component;

import ru.practicum.ewm.EndpointHitPost;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

@Component
public class StatsMapper {

    public EndpointHit toEndpointHit(EndpointHitPost statsDtoRequest, App app) {
        return EndpointHit.builder()
                .app(app)
                .uri(statsDtoRequest.getUri())
                .ip(statsDtoRequest.getIp())
                .timestamp(statsDtoRequest.getTimestamp())
                .build();
    }

    public ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp().getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }
}
