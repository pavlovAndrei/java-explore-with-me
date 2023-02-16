package ru.practicum.ewm.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    String QUERY_NOT_UNIQUE = "select distinct(e.uri) as uri, count(e.app) as hits, e.app as app "
            + "from EndpointHit e where e.timestamp > ?1 and e.timestamp < ?2 "
            + "group by e.app, (e.uri)";
    String QUERY_UNIQUE = QUERY_NOT_UNIQUE + ", e.ip";

    @Query(value = QUERY_NOT_UNIQUE)
    List<ViewStats> findAllNotUnique(LocalDateTime start, LocalDateTime end);

    @Query(value = QUERY_UNIQUE)
    List<ViewStats> findAllUnique(LocalDateTime start, LocalDateTime end);
}
