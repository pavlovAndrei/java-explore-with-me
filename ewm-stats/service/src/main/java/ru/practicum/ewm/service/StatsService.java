package ru.practicum.ewm.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.repository.StatsRepository;

@Slf4j
@Service
@AllArgsConstructor
public class StatsService {

    private final StatsRepository statsRepository;

    public void save(EndpointHit endpointHit) {
        log.debug("Save endpoint hit to DB: {}", endpointHit);
        statsRepository.save(endpointHit);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end,
                                    String[] uri, boolean unique) {
        List<ViewStats> viewStats;

        log.debug("Get stats with the next params: start - {}, end - {}, uri - {}, unique - {}",
                start, end, uri, unique);

        if (!unique) {
            viewStats = statsRepository.findAllNotUnique(start, end);
        } else {
            viewStats = statsRepository.findAllUnique(start, end);
        }

        if (uri != null) {
            return viewStats.stream()
                    .map(view -> filterByUri(view, uri))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } else {
            return viewStats;
        }
    }

    public ViewStats filterByUri(ViewStats viewStats, String[] uris) {
        for (String uri : uris) {
            if (viewStats.getUri().equals(uri)) {
                return viewStats;
            }
        }
        return null;
    }
}
