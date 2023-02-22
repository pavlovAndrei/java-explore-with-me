package ru.practicum.ewm.compilation.service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import org.springframework.dao.DataIntegrityViolationException;
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

        if (compRepository.existsCompilationByTitle(newCompilationDto.getTitle())) {
            throw new ConflictException(format("Provided compilation with title: '%s' is a duplicate.",
                    newCompilationDto.getTitle()));
        }
        Compilation createdCompilation = compRepository.save(buildNewCompilation(newCompilationDto));

        log.debug("Compilation with ID: {} is added.", createdCompilation.getId());
        return mapper.toCompilationDto(createdCompilation);
    }

    public void delete(Long compId) {
        log.debug("Delete compilation with id: {}.", compId);

        if (!compRepository.existsById(compId)) {
            throw new NotFoundException(format("Compilation with ID: '%d' is not found", compId));
        }
        compRepository.deleteById(compId);

        log.debug("Compilation with id: {} is removed.", compId);
    }

    public CompilationDto update(Long compId, UpdateCompilationRequest request) {
        log.debug("Update compilation with ID: {}.", compId);

        Compilation savedCompilation = compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(format("Compilation with ID: '%d' is not found", compId)));

        Compilation updatedCompilation;
        if (compRepository.existsCompilationByTitle(request.getTitle())) {
            throw new ConflictException(format("Provided compilation with title: '%s' is a duplicate.",
                    request.getTitle()));
        }
        updatedCompilation = compRepository.save(buildCompilationUpdate(request, savedCompilation));

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
