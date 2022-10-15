package com.app.finance_tracker.model.Exeptionls;

public class InvalidArgumentsException extends RuntimeException{
    public InvalidArgumentsException(String msg){
        super(msg);
    }
}
