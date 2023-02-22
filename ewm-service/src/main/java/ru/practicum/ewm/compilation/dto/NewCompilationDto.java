package ru.practicum.ewm.compilation.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Valid
@AllArgsConstructor
public class NewCompilationDto {

    @NotNull(message = "Compilation events should not be empty.")
    private Long[] events;

    @NotNull(message = "Compilation pinned value should not be empty.")
    private Boolean pinned;

    @NotBlank(message = "Compilation title should not be empty.")
    private String title;
}
