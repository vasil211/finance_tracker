package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.transferDTO.TransferDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferFilteredDto;
import com.app.finance_tracker.model.dto.transferDTO.TransferForReturnDTO;
import com.app.finance_tracker.service.CurrencyExchangeService;
import com.app.finance_tracker.service.TransferService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class TransferController extends AbstractController {

    @Autowired
    private TransferService transferService;

    @PostMapping("/transfers")
    public ResponseEntity<TransferForReturnDTO> sendTransfer(@RequestBody TransferDTO transferDTO,
                                                             HttpServletRequest request) {
        long id = checkIfLoggedAndReturnUserId(request);
        TransferForReturnDTO transfer = transferService.createTransfer(transferDTO, id);
        return new ResponseEntity<>(transfer, HttpStatus.CREATED);

    }

    @GetMapping("/transfers/{id}")
    public ResponseEntity<TransferForReturnDTO> getTransferById(@PathVariable long id, HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        TransferForReturnDTO transfer = transferService.getTransferById(id, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("/accounts/transfers/sent")
    public ResponseEntity<List<TransferForReturnDTO>> getAllSentTransfers(@RequestParam long accountId,
                                                                          HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransferForReturnDTO> transfer = transferService.getAllSentTransfers(accountId, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("/accounts/transfers/received")
    public ResponseEntity<List<TransferForReturnDTO>> getAllReceivedTransfers(HttpServletRequest request,
                                                                              @RequestParam long accountId) {
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransferForReturnDTO> transfer = transferService.getAllReceivedTransfers(accountId, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @PostMapping("/accounts/transfers/filtered")
    public ResponseEntity<List<TransferForReturnDTO>> getAllTransfersFiltered(HttpServletRequest request,
                                                                              @RequestBody TransferFilteredDto filteredDto){
        long id = checkIfLoggedAndReturnUserId(request);
        List<TransferForReturnDTO> transfers = transferService.getAllTransfersFiltered(filteredDto, id);
        return ResponseEntity.ok(transfers);
    }

    @PostMapping("/accounts/transfers/downloadPdf")
    public ResponseEntity downloadPdf(HttpServletRequest request , HttpServletResponse response, @RequestBody TransferFilteredDto filteredDto){
        long userId = checkIfLoggedAndReturnUserId(request);
        transferService.downloadPdf(response, filteredDto, userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
