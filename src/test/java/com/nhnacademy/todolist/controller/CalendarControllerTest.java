package com.nhnacademy.todolist.controller;

import com.nhnacademy.todolist.advice.CommonRestControllerAdvice;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nhnacademy.todolist.dto.DailyRegisterCountRequestDto;
import com.nhnacademy.todolist.dto.DailyRegisterCountResponseDto;
import com.nhnacademy.todolist.interceptor.AuthCheckInterceptor;
import com.nhnacademy.todolist.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static com.nhnacademy.todolist.config.WebTestConfig.*;

class CalendarControllerTest {
    private MockMvc mockMvc;
    private EventService eventService;


    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(new CalendarController(eventService))
                .setControllerAdvice(new CommonRestControllerAdvice())
                .setMessageConverters(mappingJackson2HttpMessageConverter())
                .addFilter(new CharacterEncodingFilter())
                .addFilter(new HiddenHttpMethodFilter())
                .addInterceptors(new AuthCheckInterceptor())
                .addDispatcherServletCustomizer(dispatcherServlet -> dispatcherServlet.setThrowExceptionIfNoHandlerFound(true))
                .build();
    }

    @Test
    @DisplayName("일별 이벤트 카운트")
    void dailyRegisterCount() throws Exception {
        DailyRegisterCountRequestDto request = new DailyRegisterCountRequestDto();
        request.setDate(LocalDate.now());

        DailyRegisterCountResponseDto count = new DailyRegisterCountResponseDto(3);
        when(eventService.getDailyRegisterCount(any())).thenReturn(count);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/calendar/daily-register-count")
                .param("date", "2023-05-08")
                .header("X-USER-ID","suebin")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3))
                .andDo(print());
    }
}