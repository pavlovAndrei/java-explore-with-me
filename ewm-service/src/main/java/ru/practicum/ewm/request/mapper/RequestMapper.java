package ru.practicum.ewm.request.mapper;

import static java.util.Objects.isNull;

import org.mapstruct.Mapper;

import ru.practicum.ewm.request.dto.ParticipantRequestDto;
import ru.practicum.ewm.request.model.Request;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    default ParticipantRequestDto toParticipantRequestDto(Request request) {
        if (isNull(request)) {
            return null;
        }

        return ParticipantRequestDto.builder()
                .id(request.getId())
                .requester(request.getRequester().getId())
                .event(request.getEvent().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }
}
