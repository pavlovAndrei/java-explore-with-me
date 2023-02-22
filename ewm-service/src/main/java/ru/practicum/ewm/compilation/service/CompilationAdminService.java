package ru.practicum.ewm.compilation.service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.model.ConflictException;
import ru.practicum.ewm.exception.model.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminService {

    private final CompilationMapper mapper;
    private final CompilationRepository compRepository;
    private final EventRepository eventRepository;

    public CompilationDto create(NewCompilationDto newCompilationDto) {
        log.debug("Add compilation with title: {}.", newCompilationDto.getTitle());

        Compilation createdCompilation;
        try {
            createdCompilation = compRepository.save(buildNewCompilation(newCompilationDto));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(format("Provided compilation with title: '%s' is a duplicate.",
                    newCompilationDto.getTitle()));
        }

        log.debug("Compilation with ID: {} is added.", createdCompilation.getId());
        return mapper.toCompilationDto(createdCompilation);
    }

    public void delete(Long compId) {
        log.debug("Delete compilation with id: {}.", compId);

        try {
            compRepository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(format("Compilation with ID: '%d' is not found", compId));
        }

        log.debug("Compilation with id: {} is removed.", compId);
    }

    public CompilationDto update(Long compId, UpdateCompilationRequest request) {
        log.debug("Update compilation with ID: {}.", compId);

        Compilation savedCompilation = compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(format("Compilation with ID: '%d' is not found", compId)));

        Compilation updatedCompilation;
        try {
            updatedCompilation = compRepository.save(buildCompilationUpdate(request, savedCompilation));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(format("Provided compilation with ID: '%d' is a duplicate.", compId));
        }

        log.debug("Compilation with ID: {} is updated.", compId);
        return mapper.toCompilationDto(updatedCompilation);
    }

    private Compilation buildNewCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();

        if (newCompilationDto.getEvents().length != 0) {
            events = eventRepository.findEventsByIds(newCompilationDto.getEvents());
        }

        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .events(events)
                .build();
    }

    private Compilation buildCompilationUpdate(UpdateCompilationRequest compilationRequest,
                                               Compilation compilation) {
        if (nonNull(compilationRequest.getTitle())) {
            compilation.setTitle(compilationRequest.getTitle());
        }

        if (nonNull(compilationRequest.getPinned())) {
            compilation.setPinned(compilationRequest.getPinned());
        }

        if (nonNull(compilationRequest.getEvents())) {
            List<Event> events = new ArrayList<>();
            if (compilationRequest.getEvents().length != 0) {
                events = eventRepository.findEventsByIds(compilationRequest.getEvents());
            }

            compilation.setEvents(events);
        }
        return compilation;
    }
}
