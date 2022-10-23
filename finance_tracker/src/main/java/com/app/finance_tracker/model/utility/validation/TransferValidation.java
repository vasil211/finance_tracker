package com.app.finance_tracker.model.utility.validation;

import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.exceptions.BadRequestException;
import org.springframework.stereotype.Component;

@Component
public class TransferValidation {

    public void checkIfAccountBelongsToUser(Account sender, long userId){
        if(sender.getUser().getId() != userId){
            throw new BadRequestException("Account does not belong to user");
        }
    }
}
