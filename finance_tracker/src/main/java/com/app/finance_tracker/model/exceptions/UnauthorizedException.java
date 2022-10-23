package com.app.finance_tracker.model.exceptions;

public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String msg) {
        super(msg);
    }
}
