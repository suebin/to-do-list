package com.nhnacademy.todolist.dto;

import lombok.Getter;

@Getter
public class EventCreatedResponseDto {
    private final long id;

    public EventCreatedResponseDto(long id) {
        this.id = id;
    }
}
