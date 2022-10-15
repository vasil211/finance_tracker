package com.app.finance_tracker.model.Exeptionls;

public class NotFoundException extends  RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
