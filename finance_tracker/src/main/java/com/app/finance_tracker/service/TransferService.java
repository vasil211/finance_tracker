package com.app.finance_tracker.service;

import com.app.finance_tracker.model.dto.currencyDTO.CurrencyExchangeDto;
import com.app.finance_tracker.model.dto.transferDTO.TransferFilteredDto;
import com.app.finance_tracker.model.exceptions.BadRequestException;
import com.app.finance_tracker.model.dto.currencyDTO.CurrencyForTransferDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferForReturnDTO;
import com.app.finance_tracker.model.dto.userDTO.UserForTransferDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Currency;
import com.app.finance_tracker.model.entities.Transfer;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class TransferService extends AbstractService {

    @Autowired
    CurrencyExchangeService currencyExchangeService;


    public TransferForReturnDTO createTransfer(TransferDTO transferDTO, long userId) {
        if (transferDTO.getAmount() <= 0) {
            throw new BadRequestException("Amount must be positive");
        }
        Account sender = getAccountById(transferDTO.getSenderAccountId());
        transferValidation.checkIfAccountBelongsToUser(sender, userId);

        if (sender.getBalance() < transferDTO.getAmount()) {
            throw new BadRequestException("Not enough money on account");
        }
        Account receiver = getAccountById(transferDTO.getReceiverAccountId());

        if (sender.getId()== receiver.getId()){
            throw new BadRequestException("Cannot send money to same account");
        }

        Transfer transfer = doTransfer(transferDTO, sender, receiver);

        return mapTransferForReturnDTO(transfer);
    }

    @Transactional
    private Transfer doTransfer(TransferDTO transferDTO, Account sender, Account receiver) {
        // exchange currency if needed
        double amount = transferDTO.getAmount();
        if (sender.getCurrency().getId() != receiver.getCurrency().getId()){
            Currency senderCurrency = sender.getCurrency();
            Currency receiverCurrency = receiver.getCurrency();
            CurrencyExchangeDto dto = currencyExchangeService.getExchangedCurrency(senderCurrency.getCode(), receiverCurrency.getCode(), amount);
            amount=dto.getResult();
        }
        sender.removeFromBalance(transferDTO.getAmount());
        accountRepository.save(sender);
        receiver.increaseBalance(amount);
        accountRepository.save(receiver);
        Transfer transfer = new Transfer();
        transfer.setAmount(amount);
        transfer.setCurrency(receiver.getCurrency());
        transfer.setReceiver(receiver);
        transfer.setSender(sender);
        transfer.setDate(LocalDateTime.now());
        transfer.setDescription(transferDTO.getDescription());
        transferRepository.save(transfer);
        return transfer;
    }

    public TransferForReturnDTO getTransferById(long id, long userId) {
        Transfer transfer = getTransferById(id);
        if (transfer.getSender().getUser().getId() != userId && transfer.getReceiver().getUser().getId() != userId) {
            throw new BadRequestException("Transfer does not belong to user");
        }
        return mapTransferForReturnDTO(transfer);
    }

    public List<TransferForReturnDTO> getAllSentTransfers(long accountId, long userId) {
        Account account = getAccountById(accountId);
        if (account.getUser().getId() != userId) {
            throw new BadRequestException("Account does not belong to user");
        }

        List<Transfer> transfers = transferRepository.findAllBySenderId(accountId);
        List<TransferForReturnDTO> transferForReturnDTOS = new ArrayList<>();
        for (Transfer transfer : transfers) {
            transferForReturnDTOS.add(mapTransferForReturnDTO(transfer));
        }
        return transferForReturnDTOS;
    }

    public List<TransferForReturnDTO> getAllReceivedTransfers(long accountId, long userId) {
        checkIfAccountBelongsToUser(accountId, userId);
        List<Transfer> transfers = transferRepository.findAllByReceiverId(accountId);
        List<TransferForReturnDTO> transferForReturnDTOS = new ArrayList<>();
        for (Transfer transfer : transfers) {
            transferForReturnDTOS.add(mapTransferForReturnDTO(transfer));
        }
        return transferForReturnDTOS;
    }

    public List<TransferForReturnDTO> getAllReceivedTransfersFromAccount(long id, long fromId, long userId) {
        checkIfAccountBelongsToUser(id, userId);
        List<Transfer> transfers = transferRepository.findAllByReceiverIdAndSenderId(id, fromId);
        List<TransferForReturnDTO> transferForReturnDTOS = new ArrayList<>();
        for (Transfer transfer : transfers) {
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
        transferForReturnDTO.setDate(transfer.getDate());
        transferForReturnDTO.setDescription(transfer.getDescription());
        return transferForReturnDTO;
    }

    //get received transfers from one date to another
    /*public List<TransferForReturnDTO> getAllTransfersWithFilter(long accountId, Date fromDate, Date toDate) {
        List<Transfer> transfers = transferDAO.getAllTransfersWithFilterByDate(accountId, fromDate, toDate);
        List<TransferForReturnDTO> transferForReturnDTOS = new ArrayList<>();
        for(Transfer transfer : transfers){
            transferForReturnDTOS.add(mapTransferForReturnDTO(transfer));
        }
        return transferForReturnDTOS;
    }*/


    public List<TransferForReturnDTO> getAllTransfersFiltered(TransferFilteredDto filteredDto, long userID) {
        List<Transfer> transfers;
        List<Long> allOwnAccounts;
        if (filteredDto.getOwnAccountsIds().size() == 0) {
            allOwnAccounts = accountRepository.findAllByUserId(userID).stream().map(Account::getId).toList();
        } else {
            allOwnAccounts = filteredDto.getOwnAccountsIds();
        }
        transfers = transferDAO.getAllTransfersFiltered(filteredDto.getToAccountsIds(),
                filteredDto.getFromAccountsIds(), allOwnAccounts, filteredDto.getFromDate(),
                filteredDto.getToDate(), filteredDto.getFromAmount(), filteredDto.getToAmount(),
                filteredDto.getCurrenciesIds(), filteredDto.getChoice());

        List<TransferForReturnDTO> transferForReturnDTOS = new ArrayList<>();
        for (Transfer transfer : transfers) {
            transferForReturnDTOS.add(mapTransferForReturnDTO(transfer));
        }
        return transferForReturnDTOS;
    }
    @SneakyThrows
    public byte[] downloadPdf(HttpServletResponse resp, TransferFilteredDto filteredDto, long userID) {
        List<TransferForReturnDTO> transfers = getAllTransfersFiltered(filteredDto, userID);
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("iTextHelloWorld.pdf"));
        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        for (TransferForReturnDTO transfer : transfers) {
            String str = transfer.toString();
            document.add(new Paragraph("\n"));
            Chunk chunk = new Chunk(str, font);
            document.add(chunk);
            System.out.println(transfer.toString());
        }

        document.close();
        File f = new File("iTextHelloWorld.pdf");
        if (!f.exists()) {
            throw new NotFoundException("File does not exist!");
        }
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
        resp.setContentLength((int) f.length());
        byte[] bytes = IOUtils.toByteArray(new FileInputStream(f));
        System.out.println(f.delete());
        return bytes;

    }
}
