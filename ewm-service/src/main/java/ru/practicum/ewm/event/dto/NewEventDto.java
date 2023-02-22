package ru.practicum.ewm.event.dto;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Location;

@Data
@Valid
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {

    private Long id;

    @NotBlank(message = "Event annotation shouldn't be empty.")
    @Size(min = 20, max = 2000, message = "Event annotation should have size from 20 to 2000.")
    private String annotation;

    @NotNull(message = "Category ID reference shouldn't be empty.")
    @Positive(message = "Category ID reference should be positive.")
    private Long category;

    @NotBlank(message = "Event description shouldn't be empty.")
    @Size(min = 20, max = 7000, message = "Event description should have size from 20 to 7000.")
    private String description;

    @NotNull(message = "Event date shouldn't be empty.")
    private LocalDateTime eventDate;

    @NotNull(message = "Event location shouldn't be empty.")
    private Location location;

    private Boolean paid;

    @PositiveOrZero(message = "Event participant should be more or equal to zero")
    private Integer participantLimit;

    private Boolean requestModeration;

    @NotBlank(message = "Event title shouldn't be empty.")
    @Size(min = 3, max = 120, message = "Event title should have size from 3 to 120.")
    private String title;
}
