package com.app.finance_tracker.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DateFilterDTO {

    private LocalDateTime from;
    private LocalDateTime to;
}
