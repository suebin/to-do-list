package com.nhnacademy.todolist.dto;

import lombok.Getter;

@Getter
public class DailyRegisterCountResponseDto {
    private final long count;

    public DailyRegisterCountResponseDto(long count) {
        this.count = count;
    }
}
