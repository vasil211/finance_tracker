package com.app.finance_tracker.model.Exeptionls;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
