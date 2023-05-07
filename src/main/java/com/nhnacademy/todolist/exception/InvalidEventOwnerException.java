package com.nhnacademy.todolist.exception;

public class InvalidEventOwnerException extends RuntimeException {
    private static final String MESSAGE="잘못된 이벤트 소유자";
    public InvalidEventOwnerException(){
        super(MESSAGE);
    }

}
