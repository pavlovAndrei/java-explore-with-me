package ru.practicum.ewm.request.service;

import java.util.List;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.ConflictException;
import ru.practicum.ewm.exception.model.ForbiddenException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipantRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import static ru.practicum.ewm.event.model.EventState.PUBLISHED;
import static ru.practicum.ewm.request.model.RequestStatus.CANCELED;
import static ru.practicum.ewm.request.model.RequestStatus.CONFIRMED;
import static ru.practicum.ewm.request.model.RequestStatus.PENDING;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestService {

    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    public List<ParticipantRequestDto> findAllByUserId(Long userId) {
        log.debug("Get the list of Requests for user with ID: {}.", userId);

        var foundRequests = requestRepository.findRequestByRequesterId(userId);

        log.debug("Found Requests size: {}.", foundRequests.size());
        return foundRequests.stream()
                .map(requestMapper::toParticipantRequestDto)
                .collect(toList());
    }

    @Transactional
    public ParticipantRequestDto create(Long userId, Long eventId) {
        log.debug("Add request for event with id: {} by user with id: {}.", eventId, userId);
        verifyUserExists(userId);

        if (requestRepository.findRequestByRequesterIdAndEventId(userId, eventId).isPresent()) {
            throw new ConflictException("Request can be sent only once.");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(format("Event with id: '%d' is not found", eventId));
        }

        if (eventRepository.findEventByInitiatorIdAndId(userId, eventId).isPresent()) {
            throw new ConflictException("Owner is not able to request participation.");
        }

        verifyEventHasPublishedStateAndRequestIsAvailable(eventId);

        Request requestToCreate = requestRepository.save(buildNewRequest(userId, eventId));

        log.debug("Request with ID: {} is added.", requestToCreate.getId());
        return requestMapper.toParticipantRequestDto(requestToCreate);
    }

    @Transactional
    public ParticipantRequestDto cancel(Long userId, Long requestId) {
        log.debug("Cancel request with id: {} by user with id: {}.", requestId, userId);
        verifyUserExists(userId);

        Request requestToCancel = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(format("Request with id: '%d' is not found", requestId)));

        if (requestToCancel.getStatus().equals(CANCELED)) {
            throw new ForbiddenException(format("Request with id: '%d' is already cancelled.", requestId));
        }

        if (!requestToCancel.getRequester().getId().equals(userId)) {
            throw new ForbiddenException("Only requester is able to cancel the request.");
        }

        requestToCancel.setStatus(CANCELED);

        Request cancelledRequest = requestRepository.save(requestToCancel);
        Event event = eventRepository
                .findById(cancelledRequest.getEvent().getId())
                .orElseThrow(() -> new NotFoundException(format("Event with id: '%d' is not found", cancelledRequest.getEvent().getId())));

        if (event.getConfirmedRequests() > 0) {
            eventRepository.setEventConfirmedRequests(cancelledRequest.getEvent().getId(), event.getConfirmedRequests() - 1);
        }

        log.debug("Request with id: {} is cancelled. Current status: {}", cancelledRequest.getId(), cancelledRequest.getStatus());
        return requestMapper.toParticipantRequestDto(cancelledRequest);
    }

    private Request buildNewRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(format("User with id: '%d' is not found", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(format("Event with id: '%d' is not found", eventId)));

        Request request = Request.builder()
                .requester(requester)
                .event(event)
                .created(now())
                .build();

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(PENDING);
        } else {
            request.setStatus(CONFIRMED);
            eventRepository.setEventConfirmedRequests(eventId, event.getConfirmedRequests() + 1);
        }

        return request;
    }

    private void verifyUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(format("User with id: '%d' is not found", userId));
        }
    }

    private void verifyEventHasPublishedStateAndRequestIsAvailable(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(format("Event with id: '%d' is not found", eventId)));

        if (!event.getState().equals(PUBLISHED)) {
            throw new ConflictException("Event is not in Published state.");
        }

        if (event.getParticipantLimit() != 0
                && (event.getParticipantLimit() - event.getConfirmedRequests()) <= 0) {
            throw new ConflictException("Participants limit for event should be 3 as max.");
        }
    }
}
