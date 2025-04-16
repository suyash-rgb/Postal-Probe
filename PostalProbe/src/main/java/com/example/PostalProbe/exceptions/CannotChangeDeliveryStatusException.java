package com.example.PostalProbe.exceptions;

public class CannotChangeDeliveryStatusException extends RuntimeException{

    public CannotChangeDeliveryStatusException(String message) {
        super(message);
    }

    public CannotChangeDeliveryStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
