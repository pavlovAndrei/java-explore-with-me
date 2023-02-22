package ru.practicum.ewm.user.dto;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Valid
@AllArgsConstructor
public class NewUserRequest {

    private Long id;

    @Email(message = "User's email should be valid.")
    @NotBlank(message = "User's email shouldn't be empty.")
    private String email;

    @NotBlank(message = "User's name shouldn't be empty.")
    private String name;
}
