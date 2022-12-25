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
    private int id;
    @Email
    private String email;
    @NonNull
    private String login;
    private Optional<String> name;
    private LocalDate birthday;
}
