package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface FeedDao {
    List<Event> getFeed(int userId);

    void addEvent(Event event);

    void deleteFeed(int userId);

    Integer getOperationId(String operationName);

    Integer getEventTypeId(String eventTypeName);


}
