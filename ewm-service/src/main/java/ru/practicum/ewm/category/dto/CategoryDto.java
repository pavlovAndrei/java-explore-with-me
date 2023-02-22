package ru.practicum.ewm.category.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryDto {

    private Long id;

    @NotBlank(message = "Category should have name.")
    private String name;
}
