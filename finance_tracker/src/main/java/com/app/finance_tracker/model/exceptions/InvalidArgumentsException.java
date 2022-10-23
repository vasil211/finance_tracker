package com.app.finance_tracker.model.exceptions;

public class InvalidArgumentsException extends RuntimeException{
    public InvalidArgumentsException(String msg){
        super(msg);
    }
}
