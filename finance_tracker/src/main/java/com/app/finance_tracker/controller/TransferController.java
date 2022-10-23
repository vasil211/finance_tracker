package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.DateFilterDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferForReturnDTO;
import com.app.finance_tracker.service.TransferService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class TransferController extends AbstractController {

    @Autowired
    private TransferService transferService;

    // send
    @PostMapping("/transfer")
    public ResponseEntity<TransferForReturnDTO> sendTransfer(@RequestBody TransferDTO transferDTO, HttpServletRequest request) {
        long id = checkIfLoggedAndReturnUserId(request);
        TransferForReturnDTO transfer = transferService.createTransfer(transferDTO, id);
        return new ResponseEntity<>(transfer, HttpStatus.CREATED);

    }

    //get transfer by id
    @GetMapping("/transfer/{id}")
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

    //get all received
    @GetMapping("accounts/{id}/transfer/received")
    public ResponseEntity<List<TransferForReturnDTO>> getAllReceivedTransfers(HttpServletRequest request, @PathVariable long id) {
        long userId = checkIfLoggedAndReturnUserId(request);
        checkIfAccountBelongsToUser(id, request);
        List<TransferForReturnDTO> transfer = transferService.getAllReceivedTransfers(id);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    //get all received from account
    @GetMapping("accounts/{id}/transfers/received/{fromId}")
    public ResponseEntity<List<TransferForReturnDTO>> getAllReceivedTransfersFromUser(@PathVariable long id,@PathVariable long fromId,
                                                                                      HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        checkIfAccountBelongsToUser(id,request);
        List<TransferForReturnDTO> transfer = transferService.getAllReceivedTransfersFromAccount(id, fromId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    // get all with filter by date
    /*@GetMapping("accounts/{id}/transfers/filter")
    public ResponseEntity<List<TransferForReturnDTO>> getAllTransfersWithFilter(@PathVariable long id,
                                                                                @RequestParam("from_date") @DateTimeFormat(pattern="yyyy-MM-dd") Date fromDte,
                                                                                @RequestParam("to_date") @DateTimeFormat(pattern="yyyy-MM-dd") Date toDate,
                                                                                HttpServletRequest request) {
        long userId = checkIfLoggedAndReturnUserId(request);
        checkIfAccountBelongsToUser(id,request);
        List<TransferForReturnDTO> transfer = transferService.getAllTransfersWithFilter(id,fromDte,toDate);
        return new ResponseEntity<>(transfer, HttpStatus.OK);

        // todo get all send with filter by date
        // todo get all received with filter by date
    }*/
}
