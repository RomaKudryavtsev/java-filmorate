package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.Optional;

@Data
@Builder
public class User {
    int id;
    @Email
    String email;
    @NonNull
    String login;
    Optional<String> name;
    LocalDate birthday;
}
