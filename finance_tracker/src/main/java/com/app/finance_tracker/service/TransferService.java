package com.app.finance_tracker.service;

import com.app.finance_tracker.model.dto.DateFilterDTO;
import com.app.finance_tracker.model.exceptions.BadRequestException;
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
import java.util.ArrayList;
import java.util.List;

@Controller
public class TransferService extends AbstractService {
    @Transactional
    public TransferForReturnDTO createTransfer(TransferDTO transferDTO, long userId) {
        if (transferDTO.getAmount() <= 0) {
            throw new BadRequestException("Amount must be positive");
        }
        Account sender = getAccountById(transferDTO.getSenderAccountId());
        transferValidation.checkIfAccountBelongsToUser(sender, userId);
        Currency currency = getCurrencyById(transferDTO.getCurrencyId());

        // todo check if currency is same, and exchange if not
        if(sender.getBalance() < transferDTO.getAmount()){
            throw new BadRequestException("Not enough money on account");
        }
        Account receiver = getAccountById(transferDTO.getReceiverAccountId());

        Transfer transfer = doTransfer(transferDTO, sender, receiver, currency);

        return mapTransferForReturnDTO(transfer);
    }

    @Transactional
    private Transfer doTransfer(TransferDTO transferDTO, Account sender, Account receiver, Currency currency) {
        // todo exchange currency if needed (if currency is different)
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
        return transfer;
    }

    public TransferForReturnDTO getTransferById(long id, long userId) {
        Transfer transfer = findTransferById(id);
        if (transfer.getSender().getUser().getId() != userId && transfer.getReceiver().getUser().getId() != userId) {
            throw new BadRequestException("Transfer does not belong to user");
        }
        return mapTransferForReturnDTO(transfer);
    }
    public List<TransferForReturnDTO> getAllSentTransfers(long accountId,long userId) {
        Account account = getAccountById(accountId);
        if(account.getUser().getId() != userId){
            throw new BadRequestException("Account does not belong to user");
        }

        List<Transfer> transfers = transferRepository.findAllSentTransfers(accountId);
        List<TransferForReturnDTO> transferForReturnDTOS = new ArrayList<>();
        for(Transfer transfer : transfers){
            transferForReturnDTOS.add(mapTransferForReturnDTO(transfer));
        }
        return transferForReturnDTOS;
    }

    public List<TransferForReturnDTO> getAllReceivedTransfers(long userId) {
        List<Transfer> transfers = transferRepository.findAllByReceiverUserId(userId);
        List<TransferForReturnDTO> transferForReturnDTOS = new ArrayList<>();
        for(Transfer transfer : transfers){
            transferForReturnDTOS.add(mapTransferForReturnDTO(transfer));
        }
        return transferForReturnDTOS;
    }

    public List<TransferForReturnDTO> getAllReceivedTransfersFromUser(long senderId, long userId) {
        List<Transfer> transfers = transferRepository.findAllByReceiverUserIdAndSenderUserId(senderId, userId);
        List<TransferForReturnDTO> transferForReturnDTOS = new ArrayList<>();
        for(Transfer transfer : transfers){
            transferForReturnDTOS.add(mapTransferForReturnDTO(transfer));
        }
        return transferForReturnDTOS;
    }
    private TransferForReturnDTO mapTransferForReturnDTO(Transfer transfer) {
        TransferForReturnDTO transferForReturnDTO = new TransferForReturnDTO();
        transferForReturnDTO.setId(transfer.getId());
        transferForReturnDTO.setAmount(transfer.getAmount());
        transferForReturnDTO.setCurrency(modelMapper.map(transfer.getCurrency(), CurrencyForTransferDTO.class));
        transferForReturnDTO.setReceiver(modelMapper.map(transfer.getReceiver().getUser(), UserForTransferDTO.class));
        transferForReturnDTO.setSender(modelMapper.map(transfer.getSender().getUser(), UserForTransferDTO.class));
        return transferForReturnDTO;
    }

    public List<TransferForReturnDTO> getAllTransfersWithFilter(DateFilterDTO dateFilterDTO, long userId) {

    }
}
