package ru.practicum.explorewithme.main.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Float lat;
    private Float lon;
}
