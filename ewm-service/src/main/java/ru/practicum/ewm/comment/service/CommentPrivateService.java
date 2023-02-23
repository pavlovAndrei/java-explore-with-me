package ru.practicum.ewm.comment.service;

import java.util.List;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.ForbiddenException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto addComment(Long userId, Long eventId, CommentDto commentDto) {
        log.debug("Add comment to event with id: {} by user with id: {}.", eventId, userId);

        User commenter = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(format("User with id: '%d' is not found.", userId)));
        Event eventToUpdate = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(format("Event with id: '%d' is not found", eventId)));

        Comment commentToAdd = Comment.builder()
                .comment(commentDto.getComment())
                .user(commenter)
                .timestamp(now())
                .build();

        List<Comment> comments = eventToUpdate.getComments();
        comments.add(commentToAdd);
        eventToUpdate.setComments(comments);

        Comment addedComment = commentRepository.save(commentToAdd);
        eventRepository.save(eventToUpdate);

        log.debug("Comment with ID: {} is added to event with ID: {}.", addedComment.getId(), eventId);
        return commentMapper.toCommentDto(addedComment);
    }

    @Transactional
    public CommentDto updateComment(Long userId, Long commentId, CommentDto commentDto) {
        log.debug("Update comment with id: {} by user with id: {}.", commentId, userId);

        Comment commentToUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(format("Comment with id: '%d' is not found.", commentId)));

        verifyCommentOwnership(userId, commentToUpdate);
        commentToUpdate.setComment(commentDto.getComment());

        Comment updatedComment = commentRepository.save(commentToUpdate);
        log.debug("Comment with ID: {} is updated.", commentId);
        return commentMapper.toCommentDto(updatedComment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        log.debug("Delete comment with id: {} by user with ID: {}.", commentId, userId);

        Comment commentToDelete = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(format("Comment with id: '%d' is not found.", commentId)));
        verifyCommentOwnership(userId, commentToDelete);

        commentRepository.deleteById(commentId);

        log.debug("Comment with id: {} is removed.", commentId);
    }

    private void verifyCommentOwnership(Long userId, Comment comment) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Only commenter is able to delete the comment.");
        }
    }
}
