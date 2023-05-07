package com.nhnacademy.todolist.domain;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class Event {
    private long id;
    private final String userId;
    private final String subject;
    private final LocalDate eventAt;
    private final LocalDateTime createdAt;

    public Event(String userId, String subject, LocalDate eventAt) {
        this.userId = userId;
        this.subject = subject;
        this.eventAt = eventAt;
        this.createdAt = LocalDateTime.now();
    }

    public void setId(long id) {
        this.id = id;
    }
}
