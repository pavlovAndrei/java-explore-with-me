package ru.practicum.ewm.user.dto;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Valid
public class NewUserRequest {

    private Long id;

    @Email(message = "{email.user.not_valid}")
    @NotBlank(message = "{email.user.not_blank}")
    private String email;

    @NotBlank(message = "{name.user.not_blank}")
    private String name;

}
