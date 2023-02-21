package ru.practicum.ewm.event.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query(value = "SELECT e FROM Event e WHERE e.id IN :ids ORDER BY e.id")
    List<Event> findEventsByIds(@Param("ids") Long[] eventIds);

    @Modifying
    @Query(value = "UPDATE Event e SET e.confirmedRequests = :requests WHERE e.id = :id")
    void setEventConfirmedRequests(@Param("id") Long eventId,
                                   @Param("requests") Integer confirmedRequests);

    List<Event> findEventsByInitiatorId(Long userId, Pageable page);

    Optional<Event> findEventByIdAndState(Long eventId, EventState published);

    Optional<Event> findEventByInitiatorIdAndId(Long userId, Long eventId);
}
