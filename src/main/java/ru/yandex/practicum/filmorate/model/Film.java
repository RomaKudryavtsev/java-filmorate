package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
@Builder
public class Film {
    int id;
    @NonNull
    @NotBlank
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
}
