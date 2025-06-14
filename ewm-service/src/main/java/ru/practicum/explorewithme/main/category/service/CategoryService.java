package ru.practicum.explorewithme.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.category.dal.CategoryRepository;
import ru.practicum.explorewithme.main.category.dto.CategoryDto;
import ru.practicum.explorewithme.main.category.dto.CategoryMapper;
import ru.practicum.explorewithme.main.category.model.Category;
import ru.practicum.explorewithme.main.event.dal.EventRepository;
import ru.practicum.explorewithme.main.exception.ConflictException;
import ru.practicum.explorewithme.main.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.getName()).isPresent()) {
            throw new ConflictException("Категория с таким названием уже существует");
        }
        return CategoryMapper.fromCategory(categoryRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        Category oldCcategory = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена"));

        Optional<Category> theSameCategory = categoryRepository.findByName(categoryDto.getName());

        if (theSameCategory.isPresent() && !theSameCategory.get().getId().equals(catId)) {
            throw new ConflictException("Категория с таким названием уже существует");
        }

        oldCcategory.setName(categoryDto.getName());

        return CategoryMapper.fromCategory(categoryRepository.save(oldCcategory));
    }

    @Transactional
    public void deleteCategory(Long catId) {
        Category oldCategory = categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена"));

        if (eventRepository.existsByCategory(oldCategory)) {
            throw new ConflictException("Нельзя удалить категорию, в которой есть события");
        }

        categoryRepository.deleteById(oldCategory.getId());
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        Page<Category> pagedResult = categoryRepository.findAll(pageable);
        return pagedResult.stream().map(CategoryMapper::fromCategory).toList();
    }

    public CategoryDto getCategoryById(Long catId) {
        return CategoryMapper.fromCategory(categoryRepository.findById(catId).orElseThrow(() -> new NotFoundException("Категория не найдена")));
    }
}