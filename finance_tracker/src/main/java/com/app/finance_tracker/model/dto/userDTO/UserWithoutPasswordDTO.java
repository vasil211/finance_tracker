package com.app.finance_tracker.model.dto.userDTO;

import lombok.Data;

@Data
public class UserWithoutPasswordDTO {
    private long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String lastLogin;

}
