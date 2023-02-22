package ru.practicum.ewm.event.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatus;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.StateActionPrivate;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.ConflictException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.request.dto.ParticipantRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import static ru.practicum.ewm.event.dto.StateActionPrivate.CANCEL_REVIEW;
import static ru.practicum.ewm.event.dto.StateActionPrivate.SEND_TO_REVIEW;
import static ru.practicum.ewm.event.model.EventState.CANCELED;
import static ru.practicum.ewm.event.model.EventState.PUBLISHED;
import static ru.practicum.ewm.request.model.RequestStatus.CONFIRMED;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventPrivateService {

    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    private final Map<StateActionPrivate, Consumer<Event>> settingEventStatusMap = Map.of(
            SEND_TO_REVIEW, event -> event.setState(EventState.PENDING),
            CANCEL_REVIEW, event -> event.setState(CANCELED));

    public List<EventShortDto> findAllByUserId(Long userId, Integer from, Integer size) {
        log.debug("Get the list of Events with parameters: from={} and size={}.", from, size);
        verifyUserExists(userId);

        Pageable page = CustomPageRequest.of(from, size, Sort.Direction.ASC, "id");
        List<Event> foundEvents = eventRepository.findEventsByInitiatorId(userId, page);

        log.debug("Found Events size: {}.", foundEvents.size());
        return foundEvents.stream()
                .map(eventMapper::toEventShortDto)
                .collect(toList());
    }

    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        log.debug("Add event with title: {}.", newEventDto.getTitle());

        verifyUserExists(userId);
        validateEventDate(newEventDto.getEventDate());

        Event event = buildNewEvent(userId, newEventDto);
        Event eventToCreate;

        try {
            eventToCreate = eventRepository.save(event);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(format("Provided event with title: '%s' is a duplicate.", newEventDto.getTitle()));
        }

        log.debug("Event with ID: {} is added.", eventToCreate.getId());
        return eventMapper.toEventFullDto(eventToCreate);
    }

    public EventFullDto getById(Long userId, Long eventId) {
        log.debug("Get Event by ID: {} for user with ID: {}.", eventId, userId);
        verifyUserExists(userId);

        Event foundEvent = eventRepository
                .findEventByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(format("Event with id: '%d' is not found", eventId)));
        return eventMapper.toEventFullDto(foundEvent);
    }

    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest request) {
        log.debug("Update event with ID: {}.", eventId);
        verifyUserExists(userId);

        if (nonNull(request.getEventDate())) {
            validateEventDate(request.getEventDate());
        }

        Event eventFromRepo = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(format("Event with id: '%d' is not found", eventId)));

        if (!userId.equals(eventFromRepo.getInitiator().getId())) {
            throw new ConflictException("Provided user ID is not initiator of the event.");
        }

        if (eventFromRepo.getState().equals(PUBLISHED)) {
            throw new ConflictException("Event should have PENDING or CANCELED state to be updated.");
        }

        Event updatedEvent;
        try {
            updatedEvent = eventRepository.save(buildEventForUpdate(request, eventFromRepo));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(format("Provided event with title: '%s' is a duplicate.", request.getTitle()));
        }

        log.debug("Event with ID: {} is updated.", eventId);
        return eventMapper.toEventFullDto(updatedEvent);
    }

    public List<ParticipantRequestDto> getRequests(Long userId, Long eventId) {
        log.debug("Get the list of Requests for user with ID: {}.", userId);

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(format("Event with id: '%d' is not found.", eventId));
        }
        verifyUserExists(userId);

        var foundRequests = requestRepository.findRequestsByUserIdAndEventId(userId, eventId);

        log.debug("Found Requests size: {}.", foundRequests.size());
        return foundRequests
                .stream()
                .map(requestMapper::toParticipantRequestDto)
                .collect(toList());
    }

    @Transactional
    public EventRequestStatusUpdateResult updateRequests(Long userId, Long eventId,
                                                         EventRequestStatusUpdateRequest updateDto) {
        log.debug("Update requests for event with id: {} by user with id: {}.", eventId, userId);
        verifyUserExists(userId);

        Event event = eventRepository
                .findEventByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(format("Event with ID: '%d' is not found", eventId)));

        List<Request> requestsForUpdate = requestRepository
                .findRequestsByEventIdAndRequestIdIn(eventId, updateDto.getRequestIds());

        return updateAndSaveRequests(requestsForUpdate, updateDto.getStatus(), event);
    }

    private Event buildNewEvent(Long userId, NewEventDto newEventDto) {
        Category category = categoryRepository
                .findById(newEventDto.getCategory())
                .orElseThrow(() ->
                        new NotFoundException(format("Category with id: '%d' is not found.", newEventDto.getCategory())));
        User initiator = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(format("User with id: '%d' is not found.", userId)));

        Event event = eventMapper.toEvent(userId, newEventDto);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setLocation(newEventDto.getLocation());

        if (isNull(event.getParticipantLimit())) {
            event.setParticipantLimit(0);
        }

        if (isNull(event.getRequestModeration())) {
            event.setRequestModeration(true);
        }

        if (isNull(event.getPaid())) {
            event.setPaid(false);
        }

        return event;
    }

    private Event buildEventForUpdate(UpdateEventUserRequest updateRequest, Event event) {
        if (nonNull(updateRequest.getCategory())) {
            Category category = categoryRepository
                    .findById(updateRequest.getCategory())
                    .orElseThrow(() ->
                            new NotFoundException(format("Category with id: '%d' is not found.", updateRequest.getCategory())));
            event.setCategory(category);
        }

        if (nonNull(updateRequest.getAnnotation())) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (nonNull(updateRequest.getDescription())) {
            event.setDescription(updateRequest.getDescription());
        }

        if (nonNull(updateRequest.getEventDate())) {
            event.setEventDate(updateRequest.getEventDate());
        }

        if (nonNull(updateRequest.getLocation())) {
            event.setLocation(event.getLocation());
        }

        if (nonNull(updateRequest.getTitle())) {
            event.setTitle(updateRequest.getTitle());
        }

        if (nonNull(updateRequest.getParticipantLimit())) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (nonNull(updateRequest.getPaid())) {
            event.setPaid(updateRequest.getPaid());
        }

        if (nonNull(updateRequest.getRequestModeration())) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        settingEventStatusMap.get(updateRequest.getStateAction()).accept(event);

        return event;
    }

    private EventRequestStatusUpdateResult updateAndSaveRequests(List<Request> requestsForUpdate,
                                                                 EventRequestStatus status, Event event) {
        switch (status) {
            case REJECTED:
                return rejectRequests(requestsForUpdate);
            case CONFIRMED:
                return confirmRequests(requestsForUpdate, event);
            default:
                return new EventRequestStatusUpdateResult();
        }
    }

    private EventRequestStatusUpdateResult confirmRequests(List<Request> requestsForUpdate, Event event) {
        var result = new EventRequestStatusUpdateResult();

        requestsForUpdate.forEach(request -> {
            try {
                validateRequestStatusIsPending(request);
            } catch (ConflictException e) {
                return;
            }

            if (event.getParticipantLimit() - event.getConfirmedRequests() <= 0) {
                throw new ConflictException("Participants limit for event should be 3 as max.");
            } else {
                confirmAndSaveRequest(result, request);
                incrementEventConfirmedRequests(event);
            }
        });

        return result;
    }

    private void confirmAndSaveRequest(EventRequestStatusUpdateResult result, Request request) {
        request.setStatus(CONFIRMED);

        var confirmedRequest = requestRepository.save(request);
        result.getConfirmedRequests()
                .add(requestMapper.toParticipantRequestDto(confirmedRequest));
    }

    private void incrementEventConfirmedRequests(Event event) {
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        eventRepository.setEventConfirmedRequests(event.getId(), event.getConfirmedRequests());
    }

    private EventRequestStatusUpdateResult rejectRequests(List<Request> requestsForUpdate) {
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        requestsForUpdate.forEach(request -> {
            validateRequestStatusIsPending(request);
            Request rejectedRequest = setRequestStatusToRejected(request);
            result.getRejectedRequests()
                    .add(requestMapper.toParticipantRequestDto(rejectedRequest));
        });
        return result;
    }

    private void verifyUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(format("User with id: '%d' is not found.", userId));
        }
    }

    private void validateRequestStatusIsPending(Request request) throws ConflictException {
        if (!request.getStatus().equals(RequestStatus.PENDING)) {
            throw new ConflictException("Request doesn't have PENDING state.");
        }
    }

    private void validateEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(now().plusHours(2))) {
            throw new ConflictException("Event date should be at least 2 hours after the current time.");
        }
    }

    private Request setRequestStatusToRejected(Request request) {
        request.setStatus(RequestStatus.REJECTED);
        return requestRepository.save(request);
    }
}
