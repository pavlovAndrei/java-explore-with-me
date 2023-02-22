package ru.practicum.ewm.compilation.controller;

import java.util.List;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationPublicService;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/compilations")
public class CompilationPublicController {

    private final CompilationPublicService compilationPublicService;

    @GetMapping
    public List<CompilationDto> findAll(@RequestParam(required = false) Boolean pinned,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Received the request to get compilations.");
        return compilationPublicService.findAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable @PositiveOrZero Long compId) {
        log.debug("Received the request to update Compilation with id: {}", compId);
        return compilationPublicService.getById(compId);
    }
}
