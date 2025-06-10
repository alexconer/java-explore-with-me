package ru.practicum.explorewithme.main.comment.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.main.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c JOIN FETCH c.event JOIN FETCH c.author where c.author.id = :userId and c.event.id = :eventId order by c.created desc")
    List<Comment> findAllByAuthorIdAndEventId(Long userId, Long eventId);

    @Query("select c from Comment c JOIN FETCH c.event JOIN FETCH c.author where c.accepted = false order by c.created desc")
    Page<Comment> findAllNotAccepted(Pageable pageable);

    @Query("select c from Comment c JOIN FETCH c.event JOIN FETCH c.author where c.accepted = true order by c.created desc")
    List<Comment> findAllByEventId(Long eventId);
}
