package ru.practicum.explorewithme.main.request.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.main.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByEventAndRequester(Long eventId, Long userId);

    List<Request> findAllByRequester(Long id);

    List<Request> findAllByEvent(Long eventId);

    @Query("select r from Request r where r.id in :requestIds")
    List<Request> findAllById(List<Long> requestIds);
}
