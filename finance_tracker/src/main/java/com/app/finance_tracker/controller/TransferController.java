package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.DateFilterDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferForReturnDTO;
import com.app.finance_tracker.service.TransferService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransferController extends AbstractController {

    @Autowired
    private TransferService transferService;

    // send
    @PostMapping("/transfer")
    public ResponseEntity<TransferForReturnDTO> sendTransfer(@RequestBody TransferDTO transferDTO, HttpServletRequest request){
        long id = checkIfLoggedAndReturnUserId(request);
        TransferForReturnDTO transfer = transferService.createTransfer(transferDTO,id);
        return new ResponseEntity<>(transfer, HttpStatus.CREATED);

    }

    @GetMapping("/transfer/{id}")
    public ResponseEntity<TransferForReturnDTO> getTransferById(@PathVariable long id, HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        TransferForReturnDTO transfer = transferService.getTransferById(id, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

    @GetMapping("/transfer/sent/{id}")
    public ResponseEntity<List<TransferForReturnDTO>> getAllSentTransfers(@PathVariable(name = "id") long accountId,
                                                                          HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        List<TransferForReturnDTO> transfer = transferService.getAllSentTransfers(accountId, userId);
        return new ResponseEntity<>(transfer, HttpStatus.OK);
    }

//    // get all received
//    @GetMapping("/transfer/received")
//    public ResponseEntity<List<TransferForReturnDTO>> getAllReceivedTransfers(HttpServletRequest request){
//        long userId = checkIfLoggedAndReturnUserId(request);
//        List<TransferForReturnDTO> transfer = transferService.getAllReceivedTransfers(userId);
//        return new ResponseEntity<>(transfer, HttpStatus.OK);
//    }
//
//    // get all received from account
//    @GetMapping("/transfer/received/{id}")
//    public ResponseEntity<List<TransferForReturnDTO>> getAllReceivedTransfersFromUser(@PathVariable long id,
//                                                                                      HttpServletRequest request){
//        long userId = checkIfLoggedAndReturnUserId(request);
//        List<TransferForReturnDTO> transfer = transferService.getAllReceivedTransfersFromUser(id, userId);
//        return new ResponseEntity<>(transfer, HttpStatus.OK);
//    }
//
//    // get all with filter by date
//    @GetMapping("/transfer/filter")
//    public ResponseEntity<List<TransferForReturnDTO>> getAllTransfersWithFilter(@RequestBody DateFilterDTO dateFilterDTO,
//                                                                                HttpServletRequest request){
//        long userId = checkIfLoggedAndReturnUserId(request);
//       List<TransferForReturnDTO> transfer = transferService.getAllTransfersWithFilter(dateFilterDTO, userId);
//        return new ResponseEntity<>(transfer, HttpStatus.OK);

    //    // todo get all send with filter by date
    //    // todo get all received with filter by date
    }
}
