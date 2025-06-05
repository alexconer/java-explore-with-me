package ru.practicum.explorewithme.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.main.compilation.dal.CompilationRepository;
import ru.practicum.explorewithme.main.compilation.dto.CompilationDto;
import ru.practicum.explorewithme.main.compilation.dto.CompilationMapper;
import ru.practicum.explorewithme.main.compilation.dto.CompilationCreateDto;
import ru.practicum.explorewithme.main.compilation.dto.CompilationUpdateDto;
import ru.practicum.explorewithme.main.compilation.model.Compilation;
import ru.practicum.explorewithme.main.event.dal.EventRepository;
import ru.practicum.explorewithme.main.event.model.Event;
import ru.practicum.explorewithme.main.exception.ConflictException;
import ru.practicum.explorewithme.main.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CompilationDto createCompilation(CompilationCreateDto compilationDto) {
        if (compilationRepository.findByTitle(compilationDto.getTitle()).isPresent()) {
            throw new ConflictException("Подборка с таким названием уже существует");
        }
        if (compilationDto.getPinned() == null) {
            compilationDto.setPinned(false);
        }

        Compilation compilation = CompilationMapper.toCompilation(compilationDto);

        List<Event> events = List.of();
        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllById(compilationDto.getEvents());
        }
        compilation.setEvents(events);

        return CompilationMapper.fromCompilation(compilationRepository.save(compilation));
    }

    @Transactional
    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto compilationDto) {
        Compilation oldCompilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка не найдена"));

        Optional<Compilation> theSameCompilation = compilationRepository.findByTitle(compilationDto.getTitle());
        if (theSameCompilation.isPresent() && !theSameCompilation.get().getId().equals(compId)) {
            throw new ConflictException("Подборка с таким названием уже существует");
        }

        if (compilationDto.getPinned() != null) {
            oldCompilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getTitle() != null) {
            oldCompilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getEvents() != null && !compilationDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllById(compilationDto.getEvents());
            oldCompilation.setEvents(events);
        }
        return CompilationMapper.fromCompilation(compilationRepository.save(oldCompilation));
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка не найдена"));
        compilationRepository.deleteById(compId);
    }

    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
        Page<Compilation> compilations = pinned == null ? compilationRepository.findAll(pageable) : compilationRepository.findByPinned(pinned, pageable);
        return compilations.stream().map(CompilationMapper::fromCompilation).toList();
    }

    public CompilationDto getCompilationById(Long compId) {
        return CompilationMapper.fromCompilation(compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Подборка не найдена")));
    }
}
