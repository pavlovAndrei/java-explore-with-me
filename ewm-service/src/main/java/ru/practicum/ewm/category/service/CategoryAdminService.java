package ru.practicum.ewm.category.service;

import static java.lang.String.format;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.model.ConflictException;
import ru.practicum.ewm.exception.model.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminService {

    private final CategoryMapper mapper;
    private final CategoryRepository repository;

    public CategoryDto create(Category category) {
        log.debug("Add Category with name={}.", category.getName());
        Category categoryToCreate;

        try {
            categoryToCreate = repository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(format("Provided category with name:'%s' is a duplicate.", category.getName()));
        }

        log.debug("Category with ID: {} is added.", categoryToCreate.getId());
        return mapper.toCategoryDto(categoryToCreate);
    }

    public CategoryDto update(Long id, CategoryDto categoryDto) {
        log.debug("Update category with ID: {}.", id);
        Category updatedCategory;

        if (!repository.existsById(id)) {
            throw new NotFoundException(format("Category with id: '%d' is not found", id));
        }
        categoryDto.setId(id);

        try {
            updatedCategory = repository.save(mapper.toCategory(categoryDto));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(format("Provided category with name:'%s' is a duplicate.", categoryDto.getName()));
        }

        log.debug("Category with ID: {} is updated.", id);
        return mapper.toCategoryDto(updatedCategory);
    }

    public void delete(Long id) {
        log.debug("Delete category with ID: {}.", id);

        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(format("Category with id: '%d' is not found.", id));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Prohibited to delete category with events.");
        }

        log.debug("Category with ID: {} is removed.", id);
    }
}
