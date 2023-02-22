package ru.practicum.ewm.event.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventPrivateService;
import ru.practicum.ewm.request.dto.ParticipantRequestDto;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventPrivateService eventPrivateService;

    @GetMapping
    public List<EventShortDto> findAllByUserId(@PathVariable Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Received the request to get events.");
        return eventPrivateService.findAllByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @Valid @RequestBody NewEventDto newEventDto) {
        log.debug("Received the request to create Event with title: {}", newEventDto.getTitle());
        return eventPrivateService.create(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable Long userId,
                                @PathVariable Long eventId) {
        log.debug("Received the request to get Event by ID: {}", eventId);
        return eventPrivateService.getById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @Valid @RequestBody UpdateEventUserRequest request) {
        log.debug("Received the request to update Event with id: {}", eventId);
        return eventPrivateService.update(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipantRequestDto> findAllRequests(@PathVariable Long userId,
                                                       @PathVariable Long eventId) {
        log.debug("Received the request to get requests for event with id: {}", eventId);
        return eventPrivateService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequests(@PathVariable Long userId,
                                                         @PathVariable Long eventId,
                                                         @RequestBody EventRequestStatusUpdateRequest updateDto) {
        log.debug("Received the request to update request with id: {}", eventId);
        return eventPrivateService.updateRequests(userId, eventId, updateDto);
    }
}
