package ru.practicum.ewm.category.service;

import java.util.List;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.exception.model.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicService {

    private final CategoryMapper mapper;
    private final CategoryRepository repository;

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.debug("Get the list of Categories with parameters: from={} and size={}.", from, size);

        Pageable page = CustomPageRequest.of(from, size, Sort.Direction.ASC, "id");
        List<Category> foundCategories = repository.findAll(page).getContent();

        log.debug("Found Categories size: {}.", foundCategories.size());
        return foundCategories.stream()
                .map(mapper::toCategoryDto)
                .collect(toList());
    }

    public CategoryDto getCategoryById(Long catId) {
        log.debug("Get Category with id: {}.", catId);

        Category foundCategory = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(format("Category with id: '%d' is not found", catId)));
        return mapper.toCategoryDto(foundCategory);
    }
}
