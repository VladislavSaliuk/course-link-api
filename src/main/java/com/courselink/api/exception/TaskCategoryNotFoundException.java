package com.courselink.api.exception;

public class TaskCategoryNotFoundException extends RuntimeException {

    public TaskCategoryNotFoundException() {

    }
    public TaskCategoryNotFoundException(String message) {
        super(message);
    }
}
