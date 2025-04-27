package com.example.PostalProbe.exceptions;

public class CircleDoesNotExistException extends RuntimeException{

    public CircleDoesNotExistException(String message){
        super(message);
    }

    public CircleDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
