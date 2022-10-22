package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentCreateDto;
import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentResponseDto;
import com.app.finance_tracker.model.utility.service.ScheduledPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ScheduledPaymentController extends AbstractController {

    @Autowired
    private ScheduledPaymentService scheduledPaymentService;

    @PostMapping("/accounts/{id}/scheduled_payments")
    public ResponseEntity<ScheduledPaymentResponseDto> schedulePayment (@PathVariable long id,
                                                                        @RequestBody ScheduledPaymentCreateDto scheduledPaymentCreateDto, HttpServletRequest req){
        checkIfLogged(req);
        //check if user is logged
        checkIfAccountBelongsToUser(id,req);
        ScheduledPaymentResponseDto dto = scheduledPaymentService.createScheduledPayment(id,scheduledPaymentCreateDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @GetMapping("/accounts/{accountId}/scheduled_payments/{id}")
    public ResponseEntity<ScheduledPaymentResponseDto> getSchedulePayment(@PathVariable long accountId, @PathVariable long id, HttpServletRequest request){
        //check if user is logged
        checkIfLogged(request);
        checkIfAccountBelongsToUser(accountId,request);
        return new ResponseEntity<>(scheduledPaymentService.getPaymentById(accountId,id),HttpStatus.OK);
    }

    @GetMapping("/accounts/{id}/scheduled_payments")
    public ResponseEntity<List<ScheduledPaymentResponseDto>> getAllScheduledPayments(@PathVariable long id,HttpServletRequest request){
        checkIfLogged(request);
        checkIfAccountBelongsToUser(id,request);
        List<ScheduledPaymentResponseDto> list = scheduledPaymentService.getAllScheduledPaymentsByAccId(id);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }
}
