package com.nhnacademy.todolist.controller;

import com.nhnacademy.todolist.dto.DailyRegisterCountRequestDto;
import com.nhnacademy.todolist.dto.DailyRegisterCountResponseDto;
import com.nhnacademy.todolist.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
public class CalendarController {
    private final EventService eventService;

    @GetMapping("/daily-register-count")
    public DailyRegisterCountResponseDto dailyRegisterCount(@Valid DailyRegisterCountRequestDto dailyRegisterCountRequestDto) {
        return eventService.getDayilyRegisterCount(dailyRegisterCountRequestDto.getDate());
    }

}
