package com.courselink.api.exception;

public class BookingSlotNotFoundException extends RuntimeException {
    public BookingSlotNotFoundException(String message) {
        super(message);
    }
}
