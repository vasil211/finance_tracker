package com.app.finance_tracker.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForTransferDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferForReturnDTO;
import com.app.finance_tracker.model.dto.userDTO.UserForTransferDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Currency;
import com.app.finance_tracker.model.entities.Transfer;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class TransferService extends AbstractService {
    @Transactional
    public TransferForReturnDTO sendTransfer(TransferDTO transferDTO, long userId) {
        // check if amount is valid
        if (transferDTO.getAmount() <= 0) {
            throw new BadRequestException("Amount must be positive");
        }
        // check if account belongs to user
        Account sender = getAccountById(transferDTO.getSenderAccountId());
        if(sender.getUser().getId() != userId){
            throw new BadRequestException("Account does not belong to user");
        }
        // check if currency is valid
        Currency currency = getCurrencyById(transferDTO.getCurrencyId());
        // check if there's enough money on account
        // todo check if currency is same, and exchange if not
        if(sender.getBalance() < transferDTO.getAmount()){
            throw new BadRequestException("Not enough money on account");
        }
        // check if receiver account is valid
        Account receiver = getAccountById(transferDTO.getReceiverAccountId());

        // todo exchange currency if needed (if currency is different)

        // todo create transfer transaction
        sender.setBalance(sender.getBalance() - transferDTO.getAmount());
        accountRepository.save(sender);
        receiver.setBalance(receiver.getBalance() + transferDTO.getAmount());
        accountRepository.save(receiver);
        Transfer transfer = new Transfer();
        transfer.setAmount(transferDTO.getAmount());
        transfer.setCurrency(currency);
        transfer.setReceiver(receiver);
        transfer.setSender(sender);
        transfer.setDate(LocalDateTime.now());
        transferRepository.save(transfer);

        TransferForReturnDTO transferForReturnDTO = new TransferForReturnDTO();
        transferForReturnDTO.setId(transfer.getId());
        transferForReturnDTO.setAmount(transfer.getAmount());
        transferForReturnDTO.setCurrency(modelMapper.map(transfer.getCurrency(), CurrencyForTransferDTO.class));
        transferForReturnDTO.setReceiver(modelMapper.map(transfer.getReceiver().getUser(), UserForTransferDTO.class));

        return transferForReturnDTO;
    }
}
