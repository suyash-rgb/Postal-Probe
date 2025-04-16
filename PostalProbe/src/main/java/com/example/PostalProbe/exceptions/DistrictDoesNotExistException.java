package com.example.PostalProbe.exceptions;

public class DistrictDoesNotExistException extends RuntimeException{

    public DistrictDoesNotExistException(String message){
        super(message);
    }

    public DistrictDoesNotExistException(String message, Throwable cause){
        super(message, cause);
    }
}
