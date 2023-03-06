package ru.yandex.practicum.filmorate.controller;

import lombok.Data;

@Data
public class ErrorResponse {
    String error;

    public ErrorResponse(final String error) {
        this.error = error;
    }
}
