package ru.practicum.ewm.category.service;

import static java.lang.String.format;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.ConflictException;
import ru.practicum.ewm.exception.model.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminService {

    private final CategoryMapper mapper;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CategoryDto create(Category category) {
        log.debug("Add Category with name={}.", category.getName());
        Category categoryToCreate;

        if (categoryRepository.existsCategoryByName(category.getName())) {
            throw new ConflictException(format("Provided category with name:'%s' is a duplicate.", category.getName()));
        }
        categoryToCreate = categoryRepository.save(category);

        log.debug("Category with ID: {} is added.", categoryToCreate.getId());
        return mapper.toCategoryDto(categoryToCreate);
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        log.debug("Update category with ID: {}.", id);
        Category updatedCategory;

        verifyCategoryExists(id);
        categoryDto.setId(id);

        if (categoryRepository.existsCategoryByName(categoryDto.getName())) {
            throw new ConflictException(format("Provided category with name:'%s' is a duplicate.", categoryDto.getName()));
        }
        updatedCategory = categoryRepository.save(mapper.toCategory(categoryDto));

        log.debug("Category with ID: {} is updated.", id);
        return mapper.toCategoryDto(updatedCategory);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Delete category with ID: {}.", id);

        verifyCategoryExists(id);

        if (!eventRepository.findEventByCategoryId(id).isEmpty()) {
            throw new ConflictException("Prohibited to delete category with events.");
        }

        categoryRepository.deleteById(id);
        log.debug("Category with ID: {} is removed.", id);
    }

    private void verifyCategoryExists(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(format("Category with id: '%d' is not found.", id));
        }
    }
}
