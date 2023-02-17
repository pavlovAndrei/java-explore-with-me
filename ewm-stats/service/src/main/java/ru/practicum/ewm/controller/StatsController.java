package ru.practicum.ewm.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.EndpointHitPost;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.service.StatsService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsServiceImpl;

    @PostMapping("/hit")
    public void add(@Valid @RequestBody EndpointHitPost endpointHitDto) {
        log.info("POST /hit the next object: dto={},", endpointHitDto);
        statsServiceImpl.addStat(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStat(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              LocalDateTime start,
                                      @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                              LocalDateTime end,
                                      @RequestParam(value = "uris", required = false,
                                              defaultValue = "") List<String> uris,
                                      @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("GET statistic with params: start = {}, end = {}, "
                + "uris = {}, unique = {}", start, end, uris, unique);
        return statsServiceImpl.getStat(start, end, uris, unique);
    }
}