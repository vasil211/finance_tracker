package com.app.finance_tracker.model.dto;

import lombok.Data;

@Data
public class UserWithoutPasswordDTO {
    private long id;
    private String username;
    private String email;

}
