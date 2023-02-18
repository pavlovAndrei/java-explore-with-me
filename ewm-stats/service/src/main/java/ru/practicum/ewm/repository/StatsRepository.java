package ru.practicum.ewm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(s.app, s.uri, COUNT(s.ip)) "
            + " FROM EndpointHit s "
            + " WHERE s.timestamp between ?1 and ?2 and s.uri in (?3) "
            + " GROUP BY s.app, s.uri "
            + " ORDER BY count(s.ip) DESC")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(s.app, s.uri, COUNT(s.ip)) "
            + " FROM EndpointHit s "
            + " WHERE s.timestamp between ?1 and ?2 "
            + " GROUP BY s.app, s.uri "
            + " ORDER BY count(s.ip) DESC")
    List<ViewStats> getStatsWithEmptyUris(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(s.app, s.uri, COUNT(distinct s.ip)) "
            + " FROM EndpointHit s "
            + " WHERE s.timestamp between ?1 and ?2 and s.uri in (?3) "
            + " GROUP BY s.app, s.uri"
            + " ORDER BY count(distinct s.ip) DESC")
    List<ViewStats> getUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(s.app, s.uri, COUNT(distinct s.ip)) "
            + " FROM EndpointHit s "
            + " WHERE s.timestamp between ?1 and ?2 "
            + " GROUP BY s.app, s.uri"
            + " ORDER BY count(distinct s.ip) DESC")
    List<ViewStats> getUniqueStatsWithEmptyUris(LocalDateTime start, LocalDateTime end);
}
