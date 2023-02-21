package ru.practicum.ewm.event.dto;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Location;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Valid
public class UpdateEventAdminRequest {

    @Null
    private Long id;

    @Size(min = 20, max = 2000, message = "Event annotation should have size from 20 to 2000.")
    private String annotation;

    @Positive(message = "Category ID reference should be positive.")
    private Long category;

    @Size(min = 20, max = 7000, message = "Event description should have size from 20 to 7000.")
    private String description;

    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero(message = "Event participant should be more or equal to zero")
    private Integer participantLimit;

    private Boolean requestModeration;

    private StateActionAdmin stateAction;

    @Size(min = 3, max = 120, message = "Event title should have size from 3 to 120.")
    private String title;
}
