package ru.practicum.explorewithme.main.compilation.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.compilation.model.Compilation;
import ru.practicum.explorewithme.main.event.dto.EventMapper;

@UtilityClass
public class CompilationMapper {
    public CompilationDto fromCompilation(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream().map(EventMapper::toEventShortDto).toList())
                .build();
    }

    public Compilation toCompilation(CompilationReqDto dto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(dto.getTitle());
        compilation.setPinned(dto.getPinned());
        return compilation;
    }
}
