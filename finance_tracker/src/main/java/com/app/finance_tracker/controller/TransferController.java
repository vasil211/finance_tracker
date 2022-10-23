package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.transferDTO.TransferDTO;
import com.app.finance_tracker.model.dto.transferDTO.TransferForReturnDTO;
import com.app.finance_tracker.service.TransferService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferController extends AbstractController {

    @Autowired
    private TransferService transferService;

    // send
    @PostMapping("/transfer")
    public ResponseEntity<TransferForReturnDTO> sendTransfer(@RequestBody TransferDTO transferDTO, HttpServletRequest request){
        long id = checkIfLoggedAndReturnUserId(request);
        TransferForReturnDTO transfer = transferService.sendTransfer(transferDTO,id);
        return new ResponseEntity<>(transfer, HttpStatus.CREATED);

    }



    // get by id

    // get all send

    // get all received

    // get all received by user

    // get all send by user

    // get all with filter by date
}
