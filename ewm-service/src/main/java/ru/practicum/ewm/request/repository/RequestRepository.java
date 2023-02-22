package ru.practicum.ewm.request.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ru.practicum.ewm.request.model.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(value = "SELECT r FROM Request r WHERE r.event.initiator.id = :userId AND r.event.id = :eventId")
    List<Request> findRequestsByUserIdAndEventId(@Param("userId") Long userId,
                                                 @Param("eventId") Long eventId);

    @Query(value = "SELECT r FROM Request r WHERE r.event.id = :eventId AND r.id IN :requestIds")
    List<Request> findRequestsByEventIdAndRequestIdIn(@Param("eventId") Long eventId,
                                                      @Param("requestIds") Long[] requestIds);

    Optional<Request> findRequestByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findRequestByRequesterId(Long requesterId);

    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);
}
