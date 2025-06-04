package ru.practicum.explorewithme.main.request.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RequestStatusDto {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
