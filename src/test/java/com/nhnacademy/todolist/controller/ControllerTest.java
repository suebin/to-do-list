package com.nhnacademy.todolist.controller;

import com.nhnacademy.todolist.advice.CommonRestControllerAdvice;
import com.nhnacademy.todolist.config.RootConfig;
import com.nhnacademy.todolist.config.WebConfig;
import com.nhnacademy.todolist.dto.DailyRegisterCountResponseDto;
import com.nhnacademy.todolist.dto.EventCreatedResponseDto;
import com.nhnacademy.todolist.dto.EventDto;
import com.nhnacademy.todolist.exception.UnauthorizedUserException;
import com.nhnacademy.todolist.exception.ValidationFailedException;
import com.nhnacademy.todolist.interceptor.AuthCheckInterceptor;
import com.nhnacademy.todolist.mapper.EventMapper;
import com.nhnacademy.todolist.service.EventService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.nhnacademy.todolist.config.WebTestConfig.mappingJackson2HttpMessageConverter;
import static com.nhnacademy.todolist.config.WebTestConfig.objectMapper;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextHierarchy(value = {
        @ContextConfiguration(classes = {RootConfig.class}),
        @ContextConfiguration(classes = {WebConfig.class})
})
class ControllerTest {
    @Autowired
    WebApplicationContext context;

    @Autowired
    EventMapper eventMapper;
    private MockMvc mockMvc;
    private EventService eventService;


    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(new CharacterEncodingFilter("UTF-8"))
                .build();

        eventService = mock(EventService.class);
    }

    @Test
    @DisplayName("이벤트 생성")
    void createEvent() throws Exception {
        EventDto eventDto = new EventDto(1l,"spring study", LocalDate.now(), LocalDateTime.now());
        when(eventService.insert(any())).thenReturn(new EventCreatedResponseDto(1l));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/calendar/events")
                .header("X-USER-ID","suebin")
                .content(objectMapper().writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("validation Error : subject is null")
    void createEvent_validationError_subjectIsNull() throws Exception {
        EventDto eventDto = new EventDto(1l,null, LocalDate.now(), LocalDateTime.now());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/calendar/events")
                .header("X-USER-ID","suebin")
                .content(objectMapper().writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("제목은 필수값 입니다.")))
                .andExpect(jsonPath("$.message", containsString("NotBlank")))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResolvedException()).isInstanceOf(ValidationFailedException.class))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("validationError : eventDto is null")
    void createEvent_validationError_eventDtIsNull() throws Exception {
        EventDto eventDto = new EventDto(1l,"Spring study", null, LocalDateTime.now());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/calendar/events")
                .header("X-USER-ID","suebin")
                .content(objectMapper().writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("날짜는 필수값 입니다.")))
                .andExpect(jsonPath("$.message", containsString("NotNull")))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResolvedException()).isInstanceOf(ValidationFailedException.class))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("validationError : eventDto previous date")
    void createEvent_validationError_eventDt_previousDate() throws Exception {
        EventDto eventDto = new EventDto(1l,"Spring study", LocalDate.now().minusDays(10), LocalDateTime.now());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/calendar/events")
                .header("X-USER-ID","suebin")
                .content(objectMapper().writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("오늘 날짜부터 선택할 수 있습니다.")))
                .andExpect(jsonPath("$.message", containsString("FutureOrPresent")))
                .andExpect(result -> org.assertj.core.api.Assertions.assertThat(result.getResolvedException()).isInstanceOf(ValidationFailedException.class))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("이벤트 삭제")
    void deleteEvent() throws Exception {
        doNothing().when(eventService).deleteOne(anyLong());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/calendar/events/{event-id}",1)
                .header("X-USER-ID","suebin");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("이벤트 일 단위 삭제")
    void deleteEventByDaily() throws Exception {
        doNothing().when(eventService).deleteEventByDaily(any());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/calendar/events/daily/{eventAt}",LocalDate.now())
                .header("X-USER-ID","suebin");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("이벤트 일 단위 조회 - missing parameters")
    void getEventsByDaily_missingParamers() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/calendar/events")
                .header("X-USER-ID","suebin");

        MvcResult mvcResult= mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        Assertions.assertThat(mvcResult.getResolvedException()).isInstanceOf(MissingServletRequestParameterException.class);
    }


    @Test
    @DisplayName("401 Unauthorized")
    void unauthorizedTest() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/calendar/events/1");

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();

        org.assertj.core.api.Assertions.assertThat(mvcResult.getResolvedException()).isInstanceOf(UnauthorizedUserException.class);
    }

    @Test
    @DisplayName("일별 이벤트 카운트")
    void dailyRegisterCount() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(new CalendarController(eventService))
                .setControllerAdvice(new CommonRestControllerAdvice())
                .setMessageConverters(mappingJackson2HttpMessageConverter())
                .addFilter(new CharacterEncodingFilter())
                .addFilter(new HiddenHttpMethodFilter())
                .addInterceptors(new AuthCheckInterceptor())
                .addDispatcherServletCustomizer(dispatcherServlet -> dispatcherServlet.setThrowExceptionIfNoHandlerFound(true))
                .build();

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
