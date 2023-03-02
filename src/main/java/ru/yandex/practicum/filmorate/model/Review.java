package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review {
    private int reviewId;
    @NonNull
    private String content;
    @NonNull
    private Boolean isPositive;
    @NonNull
    private Integer userId;
    @NonNull
    private Integer filmId;
    private int useful;

    public boolean getIsPositive() {
        return isPositive;
    }
}
