package ru.practicum.explorewithme.main.event.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.main.category.model.Category;
import ru.practicum.explorewithme.main.event.model.Event;
import ru.practicum.explorewithme.main.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByInitiator(User user, Pageable pageable);

    Optional<Event> getEventById(Long eventId);

    @Query("select e from Event e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.initiator " +
            "where (?1 is null or e.initiator.id in ?1) " +
            "and (?2 is null or e.category.id in ?2) " +
            "and (?3 is null or e.state in ?3) " +
            "and (cast(?4 as date) is null or e.eventDate >= ?4) " +
            "and (cast(?5 as date) is null or e.eventDate <= ?5)")
    List<Event> getEventsByFilter(List<Long> users, List<Long> categories, List<String> states, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable);

    @Query("select e from Event e " +
            "where e.published is not null " +
            "and (?1 is null or lower(e.annotation) like %?1% or lower(e.description) like %?1%) " +
            "and (?2 is null or e.category.id in ?2) " +
            "and (?3 is null or e.paid = ?3) " +
            "and (cast(?4 as date) is null or e.eventDate >= ?4) " +
            "and (cast(?5 as date) is null or e.eventDate <= ?5) " +
            "and (?6 is null or (?6 = true and e.participantLimit = 0 or e.participantLimit > e.confirmedRequests))" +
            "order by e.eventDate desc")
    List<Event> getPublicEventsByParamsSortedByEventDate(String lowerCase, List<Long> categories, Boolean paid, LocalDateTime dateFrom, LocalDateTime dateTo, Boolean onlyAvailable, Pageable pageable);

    @Query("select e from Event e " +
            "where e.published is not null " +
            "and (?1 is null or lower(e.annotation) like %?1% or lower(e.description) like %?1%) " +
            "and (?2 is null or e.category.id in ?2) " +
            "and (?3 is null or e.paid = ?3) " +
            "and (cast(?4 as date) is null or e.eventDate >= ?4) " +
            "and (cast(?5 as date) is null or e.eventDate <= ?5) " +
            "and (?6 is null or (?6 = true and e.participantLimit = 0 or e.participantLimit > e.confirmedRequests))" +
            "order by e.views desc")
    List<Event> getEventsByParamsSortedByViews(String lowerCase, List<Long> categories, Boolean paid, LocalDateTime dateFrom, LocalDateTime dateTo, Boolean onlyAvailable, Pageable pageable);

    Boolean existsByCategory(Category category);

    List<Event> findAllByIdIn(List<Long> list);
}
