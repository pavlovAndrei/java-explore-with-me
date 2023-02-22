package ru.practicum.ewm.event.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import ru.practicum.ewm.request.dto.ParticipantRequestDto;

@Data
public class EventRequestStatusUpdateResult {

    List<ParticipantRequestDto> confirmedRequests;

    List<ParticipantRequestDto> rejectedRequests;

    public EventRequestStatusUpdateResult() {
        this.confirmedRequests = new ArrayList<>();
        this.rejectedRequests = new ArrayList<>();
    }
}
