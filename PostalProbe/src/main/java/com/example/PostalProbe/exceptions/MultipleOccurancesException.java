package com.example.PostalProbe.exceptions;

import ch.qos.logback.classic.spi.IThrowableProxy;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MultipleOccurancesException extends RuntimeException{

    public MultipleOccurancesException(String message){
        super(message);
    }

    public MultipleOccurancesException(String message, Throwable cause){
        super(message, cause);
    }
}
