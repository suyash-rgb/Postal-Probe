package com.example.PostalProbe.exceptions;

public class PincodeNotFoundException extends RuntimeException{

    public PincodeNotFoundException(String message) {
        super(message);
    }

    public PincodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
