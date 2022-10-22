package com.app.finance_tracker.model.dto.categoryDTO;

import lombok.Data;

@Data
public class CategoryForDaoDTO {
    private  long id;
    private  String name;
    private  long iconId;
    private  long userId;
}
