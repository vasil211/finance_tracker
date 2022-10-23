package com.app.finance_tracker.model.exceptions;

public class NotFoundException extends  RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
