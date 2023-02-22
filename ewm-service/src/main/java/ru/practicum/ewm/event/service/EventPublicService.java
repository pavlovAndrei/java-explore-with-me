package ru.practicum.ewm.event.service;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.querydsl.core.types.dsl.BooleanExpression;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSearchSort;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.NotFoundException;

import static ru.practicum.ewm.event.model.EventState.PUBLISHED;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventPublicService {

    private final EventMapper mapper;
    private final EventRepository eventRepository;


    public List<EventShortDto> findAll(String text, Long[] categories, Boolean paid, LocalDateTime start,
                                       LocalDateTime end, Boolean onlyAvailable, EventSearchSort searchSort,
                                       Integer from, Integer size) {
        log.debug("Get the list of Events with parameters: from={} and size={}.", from, size);

        var expression = buildExpression(text, categories, paid, start, end);

        Pageable page;
        if (searchSort.equals(EventSearchSort.VIEWS)) {
            page = CustomPageRequest.of(from, size, DESC, "views");
        } else {
            page = CustomPageRequest.of(from, size, ASC, "eventDate");
        }

        var eventsFromRepo = eventRepository.findAll(expression, page).getContent();
        List<EventShortDto> foundEvents;

        if (onlyAvailable) {
            foundEvents = eventsFromRepo.stream()
                    .filter(event -> event.getParticipantLimit().equals(0) ||
                            (event.getParticipantLimit() - event.getConfirmedRequests()) > 0)
                    .map(mapper::toEventShortDto)
                    .collect(toList());
        } else {
            foundEvents = eventsFromRepo.stream()
                    .map(mapper::toEventShortDto)
                    .collect(toList());
        }

        log.debug("Found Events size: {}.", foundEvents.size());
        return foundEvents;
    }

    public EventFullDto getById(Long eventId) {
        log.debug("Get Event by ID: {}.", eventId);

        Event foundEvent = eventRepository.findEventByIdAndState(eventId, PUBLISHED)
                .orElseThrow(() -> new NotFoundException(format("Event with id: '%d' is not found", eventId)));

        foundEvent.setViews(foundEvent.getViews() + 1);
        eventRepository.save(foundEvent);

        return mapper.toEventFullDto(foundEvent);
    }

    private BooleanExpression buildExpression(String text, Long[] categories, Boolean paid,
                                              LocalDateTime start, LocalDateTime end) {

        QEvent qEvent = QEvent.event;

        var expression = qEvent.state.eq(PUBLISHED);

        if (nonNull(text)) {
            expression = expression.and(qEvent.annotation.containsIgnoreCase(text)
                    .or(qEvent.description.containsIgnoreCase(text)));
        }

        if (nonNull(start)) {
            expression = expression.and(qEvent.eventDate.after(start));
        }

        if (nonNull(end)) {
            expression = expression.and(qEvent.eventDate.before(end));
        }

        if (nonNull(categories)) {
            expression = expression.and(qEvent.category.id.in(categories));
        }

        if (nonNull(paid)) {
            expression = expression.and(qEvent.paid.eq(paid));
        }

        return expression;
    }
}
