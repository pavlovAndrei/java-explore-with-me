package ru.practicum.ewm.controller;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

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
    @ResponseStatus(CREATED)
    public void create(@Valid @RequestBody EndpointHitPost endpointHitDto) {
        log.info("POST /hit the next object: dto={},", endpointHitDto);
        statsServiceImpl.create(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> findAll(@RequestParam("start") LocalDateTime start,
                                      @RequestParam("end") LocalDateTime end,
                                      @RequestParam(value = "uris", required = false,
                                              defaultValue = "") List<String> uris,
                                      @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("GET statistic with params: start = {}, end = {}, "
                + "uris = {}, unique = {}", start, end, uris, unique);
        return statsServiceImpl.findAll(start, end, uris, unique);
    }
}