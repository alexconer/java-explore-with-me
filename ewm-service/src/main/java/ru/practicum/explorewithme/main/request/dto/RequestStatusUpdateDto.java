package ru.practicum.explorewithme.main.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.main.request.model.RequestState;

import java.util.List;

@Data
@NoArgsConstructor
public class RequestStatusUpdateDto {
    private List<Long> requestIds;
    private RequestState status;
}
