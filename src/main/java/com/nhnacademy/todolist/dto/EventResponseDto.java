package com.nhnacademy.todolist.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@Setter
public class EventResponseDto {
    private long id;
    private String userId;
    private String subject;
    private LocalDate eventAt;
    private LocalDateTime createdAt;
}
