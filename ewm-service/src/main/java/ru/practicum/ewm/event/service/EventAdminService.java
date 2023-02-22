package ru.practicum.ewm.event.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.data.domain.Sort.Direction.ASC;

import static com.querydsl.core.types.dsl.Expressions.asBoolean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.StateActionAdmin;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.ConflictException;
import ru.practicum.ewm.exception.model.NotFoundException;

import static ru.practicum.ewm.event.dto.StateActionAdmin.PUBLISH_EVENT;
import static ru.practicum.ewm.event.dto.StateActionAdmin.REJECT_EVENT;
import static ru.practicum.ewm.event.model.EventState.CANCELED;
import static ru.practicum.ewm.event.model.EventState.PENDING;
import static ru.practicum.ewm.event.model.EventState.PUBLISHED;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventAdminService {

    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    private final Map<StateActionAdmin, Consumer<Event>> settingEventStatusMap = Map.of(
            PUBLISH_EVENT, this::setEventStatusPublished,
            REJECT_EVENT, this::setEventStatusCanceled
    );

    public List<EventFullDto> findAll(Long[] users, EventState[] states, Long[] categories,
                                      LocalDateTime start, LocalDateTime end,
                                      Integer from, Integer size) {
        log.debug("Get the list of Events with parameters: from={} and size={}.", from, size);
        Pageable page = CustomPageRequest.of(from, size, ASC, "id");

        QEvent qEvent = QEvent.event;
        var expression = asBoolean(true).isTrue();

        if (nonNull(categories)) {
            expression = expression.and(qEvent.category.id.in(categories));
        }

        if (nonNull(users)) {
            expression = expression.and(qEvent.initiator.id.in(users));
        }

        if (nonNull(states)) {
            expression = expression.and(qEvent.state.in(states));
        }

        if (nonNull(start)) {
            expression = expression.and(qEvent.eventDate.after(start));
        }

        if (nonNull(end)) {
            expression = expression.and(qEvent.eventDate.before(end));
        }

        var foundEvents = eventRepository.findAll(expression, page).getContent();

        log.debug("Found Events size: {}.", foundEvents.size());
        return foundEvents.stream()
                .map(eventMapper::toEventFullDto)
                .collect(toList());
    }

    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminRequest adminRequest) {
        log.debug("Update event with ID: {}.", eventId);

        if (nonNull(adminRequest.getEventDate())) {
            validateEventDate(adminRequest.getEventDate());
        }

        var eventToSave = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(format("Event with id: '%d' is not found", eventId)));

        Event updatedEvent;

        if (eventRepository.existsEventByTitle(adminRequest.getTitle())) {
            throw new ConflictException("Provided title is a duplicate.");
        }
        updatedEvent = eventRepository.save(buildEventFromUpdateRequest(adminRequest, eventToSave));

        log.debug("Event with ID: {} is updated.", eventId);
        return eventMapper.toEventFullDto(updatedEvent);
    }

    private Event buildEventFromUpdateRequest(UpdateEventAdminRequest eventDto, Event event) {
        if (nonNull(eventDto.getCategory())) {
            Category category = categoryRepository
                    .findById(eventDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(format("Category with id: '%d' is not found", eventDto.getCategory())));
            event.setCategory(category);
        }

        if (nonNull(eventDto.getDescription())) {
            event.setDescription(eventDto.getDescription());
        }

        if (nonNull(eventDto.getEventDate())) {
            event.setEventDate(eventDto.getEventDate());
        }

        if (nonNull(eventDto.getTitle())) {
            event.setTitle(eventDto.getTitle());
        }

        if (nonNull(eventDto.getAnnotation())) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (nonNull(eventDto.getParticipantLimit())) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (nonNull(eventDto.getRequestModeration())) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

        if (nonNull(eventDto.getLocation())) {
            event.setLocation(event.getLocation());
        }

        if (nonNull(eventDto.getPaid())) {
            event.setPaid(eventDto.getPaid());
        }

        settingEventStatusMap.get(eventDto.getStateAction()).accept(event);
        return event;
    }

    private void validateEventDate(LocalDateTime eventDate) {
        LocalDateTime verificationDate = now().plusHours(2);
        if (eventDate.isBefore(verificationDate)) {
            throw new ConflictException("Event date should be at least 2 hours after the current time.");
        }
    }

    private void setEventStatusCanceled(Event event) {
        if (event.getState().equals(PENDING)) {
            event.setState(CANCELED);
        } else throw new ConflictException("Event should be in Pending state to be cancelled.");
    }

    private void setEventStatusPublished(Event event) {
        if (event.getState().equals(PENDING)) {
            event.setState(PUBLISHED);
            event.setPublishedOn(now());
        } else throw new ConflictException("Event should be in Pending state to be published.");
    }
}
