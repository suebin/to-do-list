package com.nhnacademy.todolist.repository;

import com.nhnacademy.todolist.domain.Event;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository {
    Event save(Event event);

    void deleteById(String userId, long eventId);

    Event getEvent(String userId, long eventId);

    Event getEvent(long eventId);

    List<Event> findAllByDay(String userId, int year, int month, int day);

    List<Event> findAllByMonth(String userId, int year, int month);

    long countByUserIdAndEventAt(String userId, LocalDate targetDate);

    void deletebyDaily(String userId, LocalDate targetDate);

}
