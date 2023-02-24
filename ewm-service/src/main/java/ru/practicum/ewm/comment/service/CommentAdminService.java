package ru.practicum.ewm.comment.service;

import static java.lang.String.format;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.exception.model.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentAdminService {

    private final CommentRepository commentRepository;

    @Transactional
    public void deleteComment(Long commentId) {
        log.debug("Delete comment with id: {}.", commentId);

        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException(format("Comment with id: '%d' is not found.", commentId));
        }

        commentRepository.deleteById(commentId);
        log.debug("Comment with id: {} is removed.", commentId);
    }
}
