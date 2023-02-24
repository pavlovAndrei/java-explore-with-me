package ru.practicum.ewm.comment.controller;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.comment.service.CommentPrivateService;
import ru.practicum.ewm.comment.dto.CommentDto;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {

    private final CommentPrivateService commentPrivateService;

    @PostMapping("/{eventId}")
    @ResponseStatus(CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @PathVariable Long eventId,
                                 @Valid @RequestBody CommentDto commentDto) {
        log.debug("Received the request to add comment to event with id: {}", eventId);
        return commentPrivateService.addComment(userId, eventId, commentDto);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(OK)
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.debug("Received the request to update comment with id: {}", commentId);
        return commentPrivateService.updateComment(userId, commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.debug("Received the request to delete comment with id: {}", commentId);
        commentPrivateService.deleteComment(userId, commentId);
    }
}
