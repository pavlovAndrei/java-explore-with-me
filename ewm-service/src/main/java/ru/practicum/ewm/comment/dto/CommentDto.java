package ru.practicum.ewm.comment.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Valid
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @Size(min = 10, max = 500, message = "Comment should have length at least 10 and 500 as max.")
    @NotBlank(message = "Comment shouldn't be empty.")
    private String comment;
}
