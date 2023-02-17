package ru.practicum.ewm.service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.EndpointHitPost;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.repository.StatsRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final AppService appService;
    private final StatsMapper mapper;
    private final StatsRepository statsRepository;

    @Transactional
    public void addStat(EndpointHitPost statsDto) {
        App app = appService.findByApp(statsDto.getApp());
        EndpointHit endpointHit = mapper.toEndpointHit(statsDto, app);
        statsRepository.save(endpointHit);
    }

    public List<ViewStatsDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> statsList;

        if (unique) {
            if (uris.isEmpty()) {
                return convertStatsToDtoList(statsRepository.getUniqueStatsWithEmptyUris(start, end));
            }

            statsList = statsRepository.getUniqueStats(start, end, uris);
        } else {
            if (uris.isEmpty()) {
                return convertStatsToDtoList(statsRepository.getStatsWithEmptyUris(start, end));
            }

            statsList = statsRepository.getStats(start, end, uris);
        }

        return convertStatsToDtoList(statsList);
    }

    private List<ViewStatsDto> convertStatsToDtoList(List<ViewStats> viewStatsList) {
        return viewStatsList.stream()
                .map(StatsMapper::toViewStatsDto)
                .collect(toList());
    }
}
