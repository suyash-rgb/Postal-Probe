package com.example.PostalProbe.exceptions;

public class StateDoesNotExistException extends RuntimeException{

    public StateDoesNotExistException(String message){
        super(message);
    }

    public StateDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
