package com.nhnacademy.todolist.service.impl;

import com.nhnacademy.todolist.domain.Event;
import com.nhnacademy.todolist.dto.DailyRegisterCountResponseDto;
import com.nhnacademy.todolist.dto.EventCreatedResponseDto;
import com.nhnacademy.todolist.dto.EventDto;
import com.nhnacademy.todolist.dto.EventResponseDto;
import com.nhnacademy.todolist.mapper.EventMapper;
import com.nhnacademy.todolist.service.EventService;
import com.nhnacademy.todolist.share.UserIdStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class DbEventServiceImpl implements EventService {

    private final EventMapper eventMapper;

    @Override
    public EventCreatedResponseDto insert(EventDto eventDto) {
        Event event = new Event(UserIdStore.getUserId(), eventDto.getSubject(), eventDto.getEventAt());
        eventMapper.save(event);
        return new EventCreatedResponseDto(event.getId());
    }

    @Override
    public void deleteOne(long eventId) {
        eventMapper.deleteById(UserIdStore.getUserId(), eventId);
    }

    @Override
    public EventDto getEvent(long eventId) {
        EventResponseDto event = eventMapper.getEvent(eventId);

        if (Objects.isNull(event)) {
            return null;
        }

        checkOwner(event.getUserId());

        return new EventDto(event.getId(), event.getSubject(), event.getEventAt(), event.getCreatedAt());
    }

    @Override
    public List<EventDto> getEventListByMonthly(String year, String month) {
        List<EventResponseDto> events = eventMapper.findAllByMonth(UserIdStore.getUserId(), year, month);
        List<EventDto> eventDtos = new ArrayList<>();
        for (EventResponseDto event : events) {
            eventDtos.add(new EventDto(event.getId(), event.getSubject(), event.getEventAt(), event.getCreatedAt()));
        }
        return eventDtos;
    }

    @Override
    public List<EventDto> getEventListBydaily(String year, String month, String day) {
        List<EventResponseDto> events = eventMapper.findAllByDay(UserIdStore.getUserId(), year, month, day);
        List<EventDto> eventDtos = new ArrayList<>();
        for (EventResponseDto event : events) {
            eventDtos.add(new EventDto(event.getId(), event.getSubject(), event.getEventAt(), event.getCreatedAt()));
        }
        return eventDtos;
    }

    @Override
    public DailyRegisterCountResponseDto getDayilyRegisterCount(LocalDate targetDate) {
        long count = eventMapper.countByUserIdAndEventAt(UserIdStore.getUserId(), targetDate);
        return new DailyRegisterCountResponseDto(count);
    }

    @Override
    public void deleteEventByDaily(LocalDate eventAt) {
        eventMapper.deleteByDaily(UserIdStore.getUserId(), eventAt);
    }
}
