package com.example.demo.exception;

public class ToDoNotFoundException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = -186139195386774361L;

    public ToDoNotFoundException(Long id) {
        super(String.format("Can not find todo with id %d", id));
    }

    public ToDoNotFoundException(String text) {
        super(String.format("Can not find todo with text %s", text));
    }
}
