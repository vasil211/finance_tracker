package com.app.finance_tracker.model.dto.categoryDTO;

import lombok.Data;

@Data
public class CategoryForReturnDTO {
    private long id;
    private String name;
    private String iconURL;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("id: ").append(id);
        sb.append(", name: '").append(name).append('\'');
        sb.append(", iconURL: '").append(iconURL).append('\'');
        return sb.toString();
    }
}
