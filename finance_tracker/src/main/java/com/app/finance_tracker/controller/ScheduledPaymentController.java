package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentCreateDto;
import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentResponseDto;
import com.app.finance_tracker.model.utility.service.ScheduledPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ScheduledPaymentController extends MasterControllerForExceptionHandlers{

    @Autowired
    private ScheduledPaymentService scheduledPaymentService;

    @PostMapping("/{id}/scheduled_payments/create")
    public ResponseEntity<ScheduledPaymentResponseDto> schedulePayment (@PathVariable long id, @RequestBody ScheduledPaymentCreateDto scheduledPaymentCreateDto){
        //check if user is logged
        //this id is for account
        ScheduledPaymentResponseDto dto = scheduledPaymentService.createScheduledPayment(id,scheduledPaymentCreateDto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @GetMapping("{accountId}/scheduled_payments/{id}")
    public ResponseEntity<ScheduledPaymentResponseDto> getSchedulePayment(@PathVariable long accountId, @PathVariable long id){
        //check if user is logged
        return new ResponseEntity<>(scheduledPaymentService.getPaymentById(accountId,id),HttpStatus.OK);
    }

    @GetMapping("/{accountId}/scheduled_payments")
    public ResponseEntity<List<ScheduledPaymentResponseDto>> getAllScheduledPayments(@PathVariable long id){
        List<ScheduledPaymentResponseDto> list = scheduledPaymentService.getAllScheduledPaymentsByAccId(id);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }
}
