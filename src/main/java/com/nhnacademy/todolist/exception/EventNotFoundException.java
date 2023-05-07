package com.nhnacademy.todolist.exception;

public class EventNotFoundException extends RuntimeException {
    private static final String MESSAGE="이벤트가 존재하지 않습니다. eventId : ";
    public EventNotFoundException(long eventId){
        super(MESSAGE + eventId);
    }
}
