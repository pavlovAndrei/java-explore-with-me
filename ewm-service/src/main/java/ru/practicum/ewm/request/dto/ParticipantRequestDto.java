package ru.practicum.ewm.request.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.request.model.RequestStatus;

@Data
@Builder
@AllArgsConstructor
public class ParticipantRequestDto {

    private Long id;

    private Long requester;

    private Long event;

    private RequestStatus status;

    private LocalDateTime created;
}
