package com.nhnacademy.todolist.mapper;

import com.nhnacademy.todolist.domain.Event;
import com.nhnacademy.todolist.dto.EventResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Mapper
@Primary
@Repository
public interface EventMapper {
    void save(Event event);

    int deleteById(@Param("userId") String userId, @Param("eventId") long eventId);

    void deleteByDaily(@Param("userId") String userId, @Param("targetDate") LocalDate targetDate);

    Event getEventByUser(@Param("userId") String userId, @Param("eventId") long eventId);

    EventResponseDto getEvent(@Param("eventId") long eventId);

    List<EventResponseDto> findAllByDay(@Param("userId") String userId, @Param("year") String year, @Param("month") String month, @Param("day") String day);

    List<EventResponseDto> findAllByMonth(@Param("userId") String userId, @Param("year") String year, @Param("month") String month);

    long countByUserIdAndEventAt(@Param("userId") String userId, @Param("targetDate") LocalDate targetDate);
}
