package com.example.PostalProbe.exceptions;

public class StateNotChangedException extends RuntimeException{

    public StateNotChangedException(String message) {
        super(message);
    }

    public StateNotChangedException(String message, Throwable cause) {
        super(message, cause);
    }
}
