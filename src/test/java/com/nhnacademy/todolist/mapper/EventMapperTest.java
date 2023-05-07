package com.nhnacademy.todolist.mapper;

import com.nhnacademy.todolist.config.RootConfig;
import com.nhnacademy.todolist.domain.Event;
import com.nhnacademy.todolist.dto.EventResponseDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@WebAppConfiguration
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RootConfig.class)
@Transactional
class EventMapperTest {
    @Autowired
    EventMapper eventMapper;
    String userId = "suebin";

//    @Test
//    @DisplayName("이벤트 저장")
//    void save() {
//        Event event = new Event(userId, "Spring MVC test", LocalDate.now());
//        eventMapper.save(event);
//        Assertions.assertThat(event.getId()).isNotZero();
//    }

//    @Test
//    @DisplayName("이벤트 조회")
//    void getEvent() {
//        EventResponseDto event = eventMapper.getEvent(91);
//        Assertions.assertThat(event).isNotNull();
//    }
//
//    @Test
//    @DisplayName("해당 아이디의 이벤트 삭제")
//    void deleteById() {
//        eventMapper.deleteById(userId,1);
//        Event event = eventMapper.getEventByUser(userId, 1);
//        Assertions.assertThat(event).isNull();
//    }
//
//    @Test
//    @DisplayName("해당 일자의 모든 이벤트 삭제")
//    void deleteByDaily() {
//        eventMapper.deleteByDaily(userId, LocalDate.now());
//        long count = eventMapper.countByUserIdAndEventAt(userId, LocalDate.now());
//        Assertions.assertThat(count).isZero();
//    }
//
//    @Test
//    @DisplayName("일별 조회")
//    void findAllByDay() {
//        List<EventResponseDto> eventList = eventMapper.findAllByDay(userId, "2023", "05", "08");
//        Assertions.assertThat(eventList).isNotNull();
//    }
//
//    @Test
//    @DisplayName("월별 조회")
//    void findAllByMonth() {
//        List<EventResponseDto> eventList = eventMapper.findAllByMonth(userId, "2023", "05");
//        Assertions.assertThat(eventList).isNotNull();
//    }
//
//    @Test
//    @DisplayName("해당 일자의 등록된 이벤트 카운트")
//    void countByUserIdAndEventAt() {
//        LocalDate date = LocalDate.of(2023,5,1);
//        long count = eventMapper.countByUserIdAndEventAt(userId, date);
//        Assertions.assertThat(count).isZero();
//    }
}