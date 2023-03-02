package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Access;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Director {
    int id;
    String name;
}