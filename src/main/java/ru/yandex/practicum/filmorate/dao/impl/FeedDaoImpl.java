package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Component
@Slf4j
public class FeedDaoImpl implements FeedDao {
    private final JdbcTemplate jdbcTemplate;
    private final static String INSERT_NEW_EVENT_SQL = "insert into event (operation_id, event_type_id, " +
            "user_id, entity_id) values (?, ?, ?, ?)";
    private final static String SELECT_FEED_FOR_USER_SQL = "select e.event_id, e.timestamp, " +
            "o.name as operation_name, et.name as event_type_name, e.user_id, e.entity_id from event e " +
            "join operation o using(operation_id) " +
            "join event_type et using(event_type_id) " +
            "where e.user_id = ?";
    private final static String SELECT_OPERATION_ID_SQL = "select operation_id from operation where name = ?";
    private final static String SELECT_EVENT_TYPE_ID_SQL = "select event_type_id from event_type where name = ?";
    private final static String DELETE_FEED_FOR_USER_SQL = "delete from event where user_id = ?";

    @Autowired
    public FeedDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Event> getFeed(int userId) {
        return jdbcTemplate.query(SELECT_FEED_FOR_USER_SQL, (rs, rowNum) -> makeEvent(rs), userId);
    }

    @Override
    public void addEvent(Event event) {
        jdbcTemplate.update(INSERT_NEW_EVENT_SQL
                , getOperationId(event.getOperation())
                , getEventTypeId(event.getEventType())
                , event.getUserId()
                , event.getEntityId());
        log.info("On event: {}", event);
    }

    @Override
    public void deleteFeed(int userId) {
        jdbcTemplate.update(DELETE_FEED_FOR_USER_SQL, userId);
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        int eventId = rs.getInt("event_id");
        long timestamp = rs.getTimestamp("timestamp").toInstant().toEpochMilli();
        String operation = rs.getString("operation_name");
        String eventType = rs.getString("event_type_name");
        int userId = rs.getInt("user_id");
        int entityId = rs.getInt("entity_id");
        return Event.builder()
                .eventId(eventId)
                .timestamp(timestamp)
                .operation(operation)
                .eventType(eventType)
                .userId(userId)
                .entityId(entityId)
                .build();
    }

    @Override
    public Integer getOperationId(String operationName) {
        return jdbcTemplate.query(SELECT_OPERATION_ID_SQL, (rs, rowNum) -> makeOperationId(rs), operationName)
                .stream().findFirst().orElseThrow(RuntimeException::new);
    }

    private Integer makeOperationId(ResultSet rs) throws SQLException {
        return rs.getInt("operation_id");
    }

    @Override
    public Integer getEventTypeId(String eventTypeName) {
        return jdbcTemplate.query(SELECT_EVENT_TYPE_ID_SQL, (rs, rowNum) -> makeEventTypeId(rs), eventTypeName)
                .stream().findFirst().orElseThrow(RuntimeException::new);
    }

    private Integer makeEventTypeId(ResultSet rs) throws SQLException {
        return rs.getInt("event_type_id");
    }
 }
