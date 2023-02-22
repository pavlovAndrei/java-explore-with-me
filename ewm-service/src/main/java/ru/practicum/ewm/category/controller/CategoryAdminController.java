package ru.practicum.ewm.category.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.service.CategoryAdminService;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    private final CategoryAdminService categoryAdminService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @ResponseStatus(CREATED)
    public CategoryDto create(@Valid @RequestBody CategoryDto categoryDto) {
        log.debug("Received the request to create Category with name: {}", categoryDto.getName());
        return categoryAdminService.create(categoryMapper.toCategory(categoryDto));
    }

    @PatchMapping("/{catId}")
    public CategoryDto update(@Positive @PathVariable Long catId,
                              @RequestBody @NotNull @Valid CategoryDto categoryDto) {
        log.debug("Received the request to update Category with id: {}", catId);
        return categoryAdminService.update(catId, categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@Positive @PathVariable Long catId) {
        log.debug("Received the request to delete Category with id: {}", catId);
        categoryAdminService.delete(catId);
    }
}
