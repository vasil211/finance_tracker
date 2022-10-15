package com.app.finance_tracker.model.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorDTO {

        private int status;
        private LocalDateTime time;
        private String message;
}
