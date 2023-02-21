package ru.practicum.ewm.compilation.service;

import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static org.springframework.data.domain.Sort.Direction.ASC;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.common.CustomPageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.QCompilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.exception.model.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicService {

    private final CompilationMapper mapper;
    private final CompilationRepository compilationRepository;

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        log.debug("Get the list of Compilations with parameters: from={} and size={}.", from, size);
        Pageable page = CustomPageRequest.of(from, size, ASC, "id");

        BooleanExpression expression = Expressions.asBoolean(true).isTrue();
        if (nonNull(pinned)) {
            expression = expression.and(QCompilation.compilation.pinned.eq(pinned));
        }

        var foundCompilations = compilationRepository
                .findAll(expression, page).getContent()
                .stream()
                .map(mapper::toCompilationDto)
                .collect(toList());

        log.debug("Found Compilations size: {}.", foundCompilations.size());
        return foundCompilations;
    }


    public CompilationDto getCompilationById(Long compId) {
        log.debug("Get compilation with ID: {}.", compId);

        Compilation foundCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(format("Compilation with id: '%d' is not found.", compId)));
        return mapper.toCompilationDto(foundCompilation);
    }
}
