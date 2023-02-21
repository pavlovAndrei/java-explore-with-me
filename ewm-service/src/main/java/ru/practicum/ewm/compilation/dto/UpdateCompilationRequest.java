package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateCompilationRequest {

    private Long[] events;

    private Boolean pinned;

    private String title;
}
