package com.example.PostalProbe.exceptions;

public class DivisionDoesNotExistException extends RuntimeException{

    public DivisionDoesNotExistException(String message){
        super(message);
    }

    public DivisionDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
