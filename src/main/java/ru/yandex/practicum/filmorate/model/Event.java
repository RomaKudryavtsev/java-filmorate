package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults (level = AccessLevel.PRIVATE)
public class Event {
    int eventId;
    long timestamp;
    String operation;
    String eventType;
    int userId;
    int entityId;
}
