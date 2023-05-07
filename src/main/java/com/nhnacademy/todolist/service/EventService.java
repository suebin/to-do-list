package com.nhnacademy.todolist.service;

import com.nhnacademy.todolist.dto.DailyRegisterCountResponseDto;
import com.nhnacademy.todolist.dto.EventCreatedResponseDto;
import com.nhnacademy.todolist.dto.EventDto;
import com.nhnacademy.todolist.exception.InvalidEventOwnerException;
import com.nhnacademy.todolist.share.UserIdStore;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    EventCreatedResponseDto insert(EventDto eventDto);

    void deleteOne(long eventId);

    EventDto getEvent(long eventId);

    List<EventDto> getEventListByMonthly(String year, String month);

    List<EventDto> getEventListBydaily(String year, String month, String day);

    DailyRegisterCountResponseDto getDailyRegisterCount(LocalDate targetDate);

    void deleteEventByDaily(LocalDate eventAt);

    default boolean checkOwner(String dbUserId) {
        if (!UserIdStore.getUserId().equals(dbUserId)) {
            throw new InvalidEventOwnerException();
        }
        return true;
    }
}
