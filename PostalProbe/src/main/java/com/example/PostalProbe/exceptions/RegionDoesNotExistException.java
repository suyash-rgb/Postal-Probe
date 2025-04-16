package com.example.PostalProbe.exceptions;

public class RegionDoesNotExistException extends RuntimeException{

    public RegionDoesNotExistException(String message){
        super(message);
    }

    public RegionDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
