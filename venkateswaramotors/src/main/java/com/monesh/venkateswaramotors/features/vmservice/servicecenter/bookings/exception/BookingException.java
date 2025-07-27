package com.monesh.venkateswaramotors.features.vmservice.servicecenter.bookings.exception;

public class BookingException extends RuntimeException {

    public BookingException(String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
} 