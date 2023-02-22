package ru.practicum.ewm.request.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.request.dto.ParticipantRequestDto;
import ru.practicum.ewm.request.service.RequestService;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipantRequestDto> findAllByUserId(@PathVariable Long userId) {
        log.debug("Received the request to get Requests for user with ID: {}", userId);
        return requestService.findAllByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ParticipantRequestDto create(@PathVariable Long userId,
                                        @RequestParam Long eventId) {
        log.debug("Received the request to create Request for event with ID: {} by user with ID: {}", eventId, userId);
        return requestService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipantRequestDto cancel(@PathVariable Long userId,
                                        @PathVariable Long requestId) {
        log.debug("Received the request to cancel Request with ID: {} by user with ID: {}", requestId, userId);
        return requestService.cancel(userId, requestId);
    }
}
