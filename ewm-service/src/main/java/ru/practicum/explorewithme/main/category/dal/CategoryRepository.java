package ru.practicum.explorewithme.main.category.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.main.category.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    List<Category> findAllByIdIn(List<Long> ids);
}
