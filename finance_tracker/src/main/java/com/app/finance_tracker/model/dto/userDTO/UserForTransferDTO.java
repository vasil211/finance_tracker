package com.app.finance_tracker.model.dto.userDTO;

import lombok.Data;

import java.util.StringJoiner;

@Data
public class UserForTransferDTO {
    private long id;
    private String username;
    private String firstName;
    private String lastName;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(firstName).append(' ').append(lastName);
        return sb.toString();
    }
}
