package com.nhnacademy.todolist.controller;

import com.nhnacademy.todolist.advice.CommonRestControllerAdvice;
import com.nhnacademy.todolist.dto.EventCreatedResponseDto;
import com.nhnacademy.todolist.dto.EventDto;
import com.nhnacademy.todolist.exception.InvalidEventOwnerException;
import com.nhnacademy.todolist.exception.UnauthorizedUserException;
import com.nhnacademy.todolist.exception.ValidationFailedException;
import com.nhnacademy.todolist.interceptor.AuthCheckInterceptor;
import com.nhnacademy.todolist.service.EventService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.nhnacademy.todolist.config.WebTestConfig.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventControllerTest {

    private MockMvc mockMvc;
    private EventService eventService;


    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);

        mockMvc = MockMvcBuilders.standaloneSetup(new EventController(eventService))
                .setControllerAdvice(new CommonRestControllerAdvice())
                .setMessageConverters(mappingJackson2HttpMessageConverter())
                .addFilter(new CharacterEncodingFilter())
                .addFilter(new HiddenHttpMethodFilter())
                .addInterceptors(new AuthCheckInterceptor())
                .addDispatcherServletCustomizer(dispatcherServlet -> dispatcherServlet.setThrowExceptionIfNoHandlerFound(true))
                .build();
    }



    @Test
    @DisplayName("이벤트 생성")
    void createEvent() throws Exception {
        EventDto eventDto = new EventDto(1l,"spring study", LocalDate.now(), LocalDateTime.now());
        when(eventService.insert(any())).thenReturn(new EventCreatedResponseDto(1l));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/calendar/events")
                .header("X-USER-ID","marco")
                .content(objectMapper().writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1l))
                .andDo(print());
    }

    @Test
    @DisplayName("validationError-subject is null")
    void createEvent_validationError_subjectIsNull() throws Exception {
        EventDto eventDto = new EventDto(1l,null, LocalDate.now(), LocalDateTime.now());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/calendar/events")
                .header("X-USER-ID","marco")
                .content(objectMapper().writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("제목은 필수값 입니다.")))
                .andExpect(jsonPath("$.message", containsString("NotBlank")))
                .andExpect(result -> Assertions.assertThat(result.getResolvedException()).isInstanceOf(ValidationFailedException.class))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("validationError-eventDt is null")
    void createEvent_validationError_eventDtIsNull() throws Exception {
        EventDto eventDto = new EventDto(1l,"Spring study", null, LocalDateTime.now());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/calendar/events")
                .header("X-USER-ID","marco")
                .content(objectMapper().writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("날짜는 필수값 입니다.")))
                .andExpect(jsonPath("$.message", containsString("NotNull")))
                .andExpect(result -> Assertions.assertThat(result.getResolvedException()).isInstanceOf(ValidationFailedException.class))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("validationError-eventDt previous date")
    void createEvent_validationError_eventDt_previousDate() throws Exception {
        EventDto eventDto = new EventDto(1l,"Spring study", LocalDate.now().minusDays(10), LocalDateTime.now());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/calendar/events")
                .header("X-USER-ID","marco")
                .content(objectMapper().writeValueAsString(eventDto))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("오늘 날짜부터 선택할 수 있습니다.")))
                .andExpect(jsonPath("$.message", containsString("FutureOrPresent")))
                .andExpect(result -> Assertions.assertThat(result.getResolvedException()).isInstanceOf(ValidationFailedException.class))
                .andDo(print())
                .andReturn();
    }

    @Test
    @DisplayName("이벤트 삭제")
    void deleteEvent() throws Exception {
        doNothing().when(eventService).deleteOne(anyLong());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/calendar/events/{event-id}",1)
                .header("X-USER-ID","marco");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("일단위 삭제")
    void deleteEventByDaily() throws Exception {
        doNothing().when(eventService).deleteEventByDaily(any());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/api/calendar/events/daily/{eventAt}",LocalDate.now())
                .header("X-USER-ID","marco");

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("일단위 조회")
    void getEventsByDaily() throws Exception {

        List<EventDto> eventDtos = List.of(
                new EventDto(1l,"spring study1",LocalDate.now(), LocalDateTime.now()),
                new EventDto(2l,"spring study2",LocalDate.now(), LocalDateTime.now()),
                new EventDto(3l,"spring study3",LocalDate.now(), LocalDateTime.now()),
                new EventDto(4l,"spring study4",LocalDate.now(), LocalDateTime.now()),
                new EventDto(5l,"spring study5",LocalDate.now(), LocalDateTime.now())
        );


        when(eventService.getEventListBydaily(anyString(),anyString(),anyString())).thenReturn(eventDtos);


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/calendar/events")
                .param("year", String.valueOf(LocalDate.now().getYear()))
                .param("month", String.valueOf(LocalDate.now().getMonthValue()))
                .param("day", String.valueOf(LocalDate.now().getDayOfMonth()))
                .header("X-USER-ID","marco");

        mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.length()").value(5))
                .andDo(print());

    }

    @Test
    @DisplayName("월단위 조회")
    void getEventsByMonthly() throws Exception {

        List<EventDto> eventDtos = List.of(
                new EventDto(1l,"spring study1",LocalDate.now(),  LocalDateTime.now()),
                new EventDto(2l,"spring study2",LocalDate.now(), LocalDateTime.now()),
                new EventDto(3l,"spring study3",LocalDate.now(), LocalDateTime.now()),
                new EventDto(4l,"spring study4",LocalDate.now(), LocalDateTime.now()),
                new EventDto(5l,"spring study5",LocalDate.now(), LocalDateTime.now())
        );


        when(eventService.getEventListByMonthly(anyString(),anyString())).thenReturn(eventDtos);


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/calendar/events")
                .param("year", String.valueOf(LocalDate.now().getYear()))
                .param("month", String.valueOf(LocalDate.now().getMonthValue()))
                .header("X-USER-ID","marco");

        mockMvc.perform(requestBuilder)
                .andExpect(jsonPath("$.length()").value(5))
                .andDo(print());
    }

    @Test
    @DisplayName("일단위 조회 - missing parameters")
    void getEventsByDaily_missingParamers() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/calendar/events")
                .header("X-USER-ID","marco");

        MvcResult mvcResult= mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn();

        Assertions.assertThat(mvcResult.getResolvedException()).isInstanceOf(MissingServletRequestParameterException.class);

    }


    @Test
    @DisplayName("401-Unauthorized")
    void unauthorizedTest() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/calendar/events/1");

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();

        Assertions.assertThat(mvcResult.getResolvedException()).isInstanceOf(UnauthorizedUserException.class);
    }

    @Test
    @DisplayName("403-Forbidden")
    void fobiddenTest() throws Exception {
        when(eventService.getEvent(anyLong())).thenThrow(new InvalidEventOwnerException());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/calendar/events/1")
                .header("X-USER-ID","marco");

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();

        Assertions.assertThat(mvcResult.getResolvedException()).isInstanceOf(InvalidEventOwnerException.class);
    }

    @Test
    @DisplayName("404-not found")
    void notfoundTest() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/api/calendar/any-url")
                .header("X-USER-ID","marco");

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound())
                .andDo(print())
                .andReturn();

        Assertions.assertThat(mvcResult.getResolvedException()).isInstanceOf(NoHandlerFoundException.class);
    }




}