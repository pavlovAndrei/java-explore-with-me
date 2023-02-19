package ru.practicum.ewm.user.service;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.exception.model.ConflictException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.QUser;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAdminService {

    private final UserMapper mapper;
    private final UserRepository repository;

    public List<UserDto> getUsers(Long[] ids, Integer from, Integer size) {
        log.debug("Requested the list of Users with parameters: from={} and size={}.", from, size);

        QUser qUser = QUser.user;
        BooleanExpression expression = Expressions.asBoolean(true).isTrue();

        if (nonNull(ids)) {
            expression = expression.and(qUser.id.in(ids));
        }

        Pageable page = CustomPageRequest.of(from, size, Sort.Direction.ASC, "id");
        List<User> foundUsers = repository.findAll(expression, page).getContent();

        log.debug("Found Users size: {}.", foundUsers.size());
        return foundUsers.stream()
                .map(mapper::toUserDto)
                .collect(toList());
    }

    @Transactional
    public UserDto createUser(User user) {
        log.debug("Add user with name: {}.", user.getName());

        User userToCreate;

        try {
            userToCreate = repository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(format("Provided user with email: '%s' is a duplicate.", user.getName()));
        }

        log.debug("User with ID: {} is added.", userToCreate.getId());
        return mapper.toUserDto(userToCreate);
    }

    public void deleteUser(Long id) {
        log.debug("Delete user with ID: {}.", id);

        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(format("User with id: '%d' is not found.", id));
        }

        log.debug("User with ID: {} is removed.", id);
    }
}
