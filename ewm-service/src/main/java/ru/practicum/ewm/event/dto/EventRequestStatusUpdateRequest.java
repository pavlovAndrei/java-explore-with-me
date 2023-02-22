package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    private Long[] requestIds;

    private EventRequestStatus status;
}
