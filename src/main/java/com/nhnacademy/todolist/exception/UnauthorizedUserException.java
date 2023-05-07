package com.nhnacademy.todolist.exception;

public class UnauthorizedUserException extends RuntimeException {
    private static final String MESSAGE = "Unauthorized";
    public UnauthorizedUserException(){
        super(MESSAGE);
    }
}
