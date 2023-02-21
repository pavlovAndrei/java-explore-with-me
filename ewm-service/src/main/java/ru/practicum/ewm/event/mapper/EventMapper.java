package ru.practicum.ewm.event.mapper;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;

import org.mapstruct.Mapper;

import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import static ru.practicum.ewm.event.model.EventState.PENDING;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventShortDto toEventShortDto(Event event);

    EventFullDto toEventFullDto(Event event);

    default Event toEvent(Long userId, NewEventDto newEventDto) {
        if (isNull(newEventDto)) {
            return null;
        }

        Category category = Category.builder()
                .id(newEventDto.getCategory())
                .build();
        User user = User.builder()
                .id(userId)
                .build();

        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(0)
                .createdOn(now())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(PENDING)
                .title(newEventDto.getTitle())
                .views(0)
                .build();
    }
}
