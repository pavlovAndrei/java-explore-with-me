package ru.practicum.ewm.comment.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NO_CONTENT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.comment.service.CommentAdminService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentAdminController {

    private final CommentAdminService commentAdminService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(NO_CONTENT)
    public void deleteComment(@PathVariable Long commentId) {
        log.debug("Received the request to delete comment with id: {}", commentId);
        commentAdminService.deleteComment(commentId);
    }
}
