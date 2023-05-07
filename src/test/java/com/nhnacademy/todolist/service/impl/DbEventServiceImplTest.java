package com.nhnacademy.todolist.service.impl;

import com.nhnacademy.todolist.domain.Event;
import com.nhnacademy.todolist.dto.DailyRegisterCountResponseDto;
import com.nhnacademy.todolist.dto.EventCreatedResponseDto;
import com.nhnacademy.todolist.dto.EventDto;
import com.nhnacademy.todolist.dto.EventResponseDto;
import com.nhnacademy.todolist.mapper.EventMapper;
import com.nhnacademy.todolist.service.EventService;
import com.nhnacademy.todolist.share.UserIdStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DbEventServiceImplTest {
    private static EventService eventService;
    private static EventMapper eventMapper;
    private static MockedStatic<UserIdStore> userIdStore;

    @BeforeAll
    static void setUp() {
        eventMapper = mock(EventMapper.class);
        userIdStore = mockStatic(UserIdStore.class);
        eventService = new DbEventServiceImpl(eventMapper);
    }

    @Test
    @DisplayName("이벤트 등록")
    void insert() {
        EventDto eventDto = new EventDto(1, "test", LocalDate.now(), LocalDateTime.now());
        userIdStore.when(UserIdStore::getUserId).thenReturn("suebin");
        doNothing().when(eventMapper).save(any());
        Event event = new Event(UserIdStore.getUserId(), eventDto.getSubject(), eventDto.getEventAt());

        EventCreatedResponseDto eventCreatedResponseDto = eventService.insert(eventDto);

        Assertions.assertThat(eventCreatedResponseDto.getId()).isEqualTo(event.getId());
    }

    @Test
    @DisplayName("이벤트 아이디로 이벤트 삭제")
    void deleteOne() {
        userIdStore.when(UserIdStore::getUserId).thenReturn("suebin");
        when(eventMapper.deleteById(anyString(), anyLong())).thenReturn(1);

        eventService.deleteOne(1);

        verify(eventMapper, atMostOnce()).deleteById(anyString(), anyLong());
    }

    @Test
    @DisplayName("이벤트 아이디로 이벤트 조회")
    void getEvent() {
        Event event = new Event("suebin", "test", LocalDate.now());
        ReflectionTestUtils.setField(event, "id", 1);
        EventResponseDto eventResponseDto = new EventResponseDto();
        eventResponseDto.setId(event.getId());
        eventResponseDto.setSubject(event.getSubject());
        eventResponseDto.setUserId(event.getUserId());

        when(eventMapper.getEvent(1)).thenReturn(eventResponseDto);
        userIdStore.when(UserIdStore::getUserId).thenReturn("suebin");

        EventDto eventDto = eventService.getEvent(1);

        Assertions.assertThat(eventDto.getId()).isEqualTo(1);
        Assertions.assertThat(eventDto.getSubject()).isEqualTo("test");
    }

    @Test
    @DisplayName("이벤트 월 단위 조회")
    void getEventListByMonthly() {
        EventResponseDto eventResponseDto1 = new EventResponseDto();
        eventResponseDto1.setSubject("test1");
        eventResponseDto1.setEventAt(LocalDate.now());

        EventResponseDto eventResponseDto2 = new EventResponseDto();
        eventResponseDto2.setSubject("test2");
        eventResponseDto2.setEventAt(LocalDate.now());

        List<EventResponseDto> eventList = List.of(
                eventResponseDto1, eventResponseDto2
        );

        userIdStore.when(UserIdStore::getUserId).thenReturn("suebin");
        when(eventMapper.findAllByMonth(anyString(), anyString(), anyString())).thenReturn(eventList);

        List<EventDto> eventDtos = eventService.getEventListByMonthly(String.valueOf(LocalDate.now().getYear()), String.valueOf(LocalDate.now().getMonthValue()));

        Assertions.assertThat(eventDtos).hasSize(2);
        Assertions.assertThat(eventDtos.get(0).getSubject()).isEqualTo("test1");
        Assertions.assertThat(eventDtos.get(1).getSubject()).isEqualTo("test2");
    }

    @Test
    @DisplayName("이벤트 일 단위 조회")
    void getEventListBydaily() {
        EventResponseDto eventResponseDto1 = new EventResponseDto();
        eventResponseDto1.setSubject("test1");
        eventResponseDto1.setEventAt(LocalDate.now());

        EventResponseDto eventResponseDto2 = new EventResponseDto();
        eventResponseDto2.setSubject("test2");
        eventResponseDto2.setEventAt(LocalDate.now());

        List<EventResponseDto> eventList = List.of(
                eventResponseDto1, eventResponseDto2
        );

        userIdStore.when(UserIdStore::getUserId).thenReturn("suebin");
        when(eventMapper.findAllByDay(anyString(), anyString(), anyString(), anyString())).thenReturn(eventList);

        List<EventDto> eventDtos = eventService.getEventListBydaily(String.valueOf(LocalDate.now().getYear()), String.valueOf(LocalDate.now().getMonthValue()), String.valueOf(LocalDate.now().getDayOfMonth()));

        Assertions.assertThat(eventDtos).hasSize(2);
        Assertions.assertThat(eventDtos.get(0).getSubject()).isEqualTo("test1");
        Assertions.assertThat(eventDtos.get(1).getSubject()).isEqualTo("test2");
    }

    @Test
    @DisplayName("이벤트 일 등록 카운트")
    void getDailyRegisterCount() {
        userIdStore.when(UserIdStore::getUserId).thenReturn("suebin");
        when(eventMapper.countByUserIdAndEventAt(anyString(), isA(LocalDate.class))).thenReturn(1L);

        DailyRegisterCountResponseDto dailyRegisterCountResponseDto = eventService.getDailyRegisterCount(LocalDate.now());

        Assertions.assertThat(dailyRegisterCountResponseDto.getCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("이벤트 일 단위 삭제")
    void deleteEventByDaily() {
        userIdStore.when(UserIdStore::getUserId).thenReturn("suebin");
        doNothing().when(eventMapper).deleteByDaily(anyString(), isA(LocalDate.class));

        eventService.deleteEventByDaily(LocalDate.now());

        verify(eventMapper, atMostOnce()).deleteByDaily(anyString(), isA(LocalDate.class));
    }
}