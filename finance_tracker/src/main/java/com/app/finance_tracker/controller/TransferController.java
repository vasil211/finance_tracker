package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.currencyDTO.CurrencyExchangeDto;
import com.app.finance_tracker.model.dto.transferDTO.TransferDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferFilteredDto;
import com.app.finance_tracker.model.dto.transferDTO.TransferForReturnDTO;
import com.app.finance_tracker.service.CurrencyExchangeService;
import com.app.finance_tracker.service.TransferService;
import jakarta.mail.Multipart;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

@RestController
public class TransferController extends AbstractController {

    @Autowired
    private CurrencyExchangeService currencyExchangeService;
    @Autowired
    private TransferService transferService;


    // send
    @PostMapping("/transfers")
    public ResponseEntity<TransferForReturnDTO> sendTransfer(@RequestBody TransferDTO transferDTO, HttpServletRequest request) {
        long id = checkIfLoggedAndReturnUserId(request);
        TransferForReturnDTO transfer = transferService.createTransfer(transferDTO, id);
        return new ResponseEntity<>(transfer, HttpStatus.CREATED);

    }

    //get transfer by id
    @GetMapping("/transfers/{id}")
    public ResponseEntity<TransferForReturnDTO> getTransferById(@PathVariable long id, HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        TransferForReturnDTO transfer = transferService.getTransferById(id, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("/accounts/{id}/transfers/sent")
    public ResponseEntity<List<TransferForReturnDTO>> getAllSentTransfers(@PathVariable(name = "id") long accountId,
                                                                          HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransferForReturnDTO> transfer = transferService.getAllSentTransfers(accountId, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("/accounts/{id}/transfer/received")
    public ResponseEntity<List<TransferForReturnDTO>> getAllReceivedTransfers(HttpServletRequest request, @PathVariable long id) {
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransferForReturnDTO> transfer = transferService.getAllReceivedTransfers(id, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("/accounts/{id}/transfers/received/{fromId}")
    public ResponseEntity<List<TransferForReturnDTO>> getAllReceivedTransfersFromUser(@PathVariable long id, @PathVariable long fromId,
                                                                                      HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransferForReturnDTO> transfer = transferService.getAllReceivedTransfersFromAccount(id, fromId, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }
    @GetMapping("/accounts/transfers/filtered")
    public ResponseEntity<List<TransferForReturnDTO>> getAllTransfersFiltered(HttpServletRequest request, @RequestBody TransferFilteredDto filteredDto){
        long id = checkIfLoggedAndReturnUserId(request);
        List<TransferForReturnDTO> transfers = transferService.getAllTransfersFiltered(filteredDto, id);
        return ResponseEntity.ok(transfers);
    }
    @SneakyThrows
    @PostMapping("/accounts/transfers/downloadPdf")
    public ResponseEntity downloadPdf(HttpServletRequest request , HttpServletResponse response, @RequestBody TransferFilteredDto filteredDto){
        long userId = checkIfLoggedAndReturnUserId(request);
        transferService.downloadPdf(response, filteredDto, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
