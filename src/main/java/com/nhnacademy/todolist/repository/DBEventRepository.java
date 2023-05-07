package com.nhnacademy.todolist.repository;

import com.nhnacademy.todolist.domain.Event;
import com.nhnacademy.todolist.share.UserIdStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DBEventRepository implements EventRepository {
    private final DataSource dataSource;

    private Connection connection = null;
    private PreparedStatement statement = null;
    private ResultSet resultSet = null;
    private static final Event result = null;
    private static final String userId = "user_id";
    private static final String eventAt = "event_at";
    private static final String subject = "subject";


    private void connect() {
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        if (Objects.nonNull(statement)) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (Objects.nonNull(resultSet)) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Event save(Event event) {
        try {
            this.connect();
            String sql = "INSERT INTO EVENT(subject, user_id, event_at, created_at) VALUES (?,?,?, NOW())";
            int index = 0;

            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(++index, event.getSubject());
            statement.setString(++index, UserIdStore.getUserId());
            statement.setObject(++index, event.getEventAt());
            statement.executeUpdate();

            connection.commit();
            resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                long eventId = resultSet.getLong(1);
                return getEvent(eventId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
           this.close();
        }

        return result;
    }

    @Override
    public void deleteById(String userId, long eventId) {
        this.connect();
        String sql = "DELETE FROM event WHERE id = ? AND user_id = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, eventId);
            statement.setString(2, userId);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }

    @Override
    public Event getEvent(String userId, long eventId) {
        try {
            this.connect();
            String sql = "SELECT id, subject, user_id, event_at, created_at FROM event WHERE id = ? AND user_id = ?";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, eventId);
            statement.setString(2, userId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Event event = new Event(resultSet.getString(userId), resultSet.getString(subject),
                        LocalDate.parse(resultSet.getString(eventAt)));

                event.setId(eventId);
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }

        return null;
    }

    @Override
    public Event getEvent(long eventId) {
        try {
            this.connect();
            String sql = "SELECT id, subject, user_id, event_at, created_at FROM event WHERE id = ?";

            statement = connection.prepareStatement(sql);
            statement.setLong(1, eventId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Event event = new Event(resultSet.getString(userId), resultSet.getString(subject),
                        LocalDate.parse(resultSet.getString(eventAt)));

                event.setId(eventId);
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }


        return null;
    }

    @Override
    public List<Event> findAllByDay(String userId, int year, int month, int day) {
        List<Event> events = new ArrayList<>();
        this.connect();
        String sql = "SELECT * FROM event WHERE event_at = ?";
        LocalDate date = LocalDate.of(year,month,day);

        try {
            statement = connection.prepareStatement(sql);
            statement.setObject(1, date);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Event event = new Event(resultSet.getString(userId), resultSet.getString(subject),
                        LocalDate.parse(resultSet.getString(eventAt)));

                event.setId(resultSet.getLong("id"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }

        return events;
    }

    @Override
    public List<Event> findAllByMonth(String userId, int year, int month) {
        List<Event> events = new ArrayList<>();
        this.connect();
        String sql = "SELECT * FROM event WHERE event_at LIKE ?";
        String date = year + "-" + Month.of(month) + "%";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, date);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Event event = new Event(resultSet.getString(userId), resultSet.getString(subject),
                        LocalDate.parse(resultSet.getString(eventAt)));

                event.setId(resultSet.getLong("id"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
        return events;
    }

    @Override
    public long countByUserIdAndEventAt(String userId, LocalDate targetDate) {
        long count;
        this.connect();
        String sql = "SELECT count(*) FROM event WHERE user_id = ? AND event_at = ?";
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setDate(2, Date.valueOf(targetDate));
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                count = resultSet.getLong(1);
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
        return 0;
    }

    @Override
    public void deletebyDaily(String userId, LocalDate targetDate) {
        this.connect();
        String sql = "DELETE FROM event WHERE user_id = ? AND event_At = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, userId);
            statement.setDate(2, Date.valueOf(targetDate));
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            this.close();
        }
    }
}
