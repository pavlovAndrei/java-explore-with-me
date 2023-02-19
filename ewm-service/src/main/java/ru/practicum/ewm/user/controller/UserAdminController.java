package ru.practicum.ewm.user.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.service.UserAdminServiceImpl;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/users")
public class UserAdminController {

    private final UserAdminServiceImpl userAdminService;
    private final UserMapper mapper;

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Long[] ids,
                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Get list of Users");
        return userAdminService.getUsers(ids, from, size);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest userDto) {
        log.debug("Create User with name: {}", userDto.getName());
        return userAdminService.createUser(mapper.toUser(userDto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteUser(@Positive @PathVariable Long id) {
        log.debug("Delete User with id: {}", id);
        userAdminService.deleteUser(id);
    }
}
