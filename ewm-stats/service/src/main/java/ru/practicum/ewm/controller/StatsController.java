package ru.practicum.ewm.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.service.StatsService;

@Slf4j
@RestController
@AllArgsConstructor
public class StatsController {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final StatsService statsService;

    @PostMapping("/hit")
    public void save(@RequestBody EndpointHitDto endpointHitDto) {
        log.debug("Save new event request by uri: {}", endpointHitDto.getUri());
        EndpointHit endpointHit = EndpointHitMapper.toHit(endpointHitDto);
        statsService.save(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.debug("Get views to event uri");
        return statsService.getStats(start, end, uris, unique);
    }
}