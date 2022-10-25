package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentCreateDto;
import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentResponseDto;
import com.app.finance_tracker.service.ScheduledPaymentService;
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
        long userId = checkIfLoggedAndReturnUserId(req);
        ScheduledPaymentResponseDto dto = scheduledPaymentService.createScheduledPayment(id,scheduledPaymentCreateDto, userId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @GetMapping("/accounts/{accountId}/scheduled_payments/{id}")
    public ResponseEntity<ScheduledPaymentResponseDto> getSchedulePayment(@PathVariable long accountId, @PathVariable long id, HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        return new ResponseEntity<>(scheduledPaymentService.getPaymentById(accountId,id, userId),HttpStatus.OK);
    }

    @GetMapping("/accounts/{id}/scheduled_payments")
    public ResponseEntity<List<ScheduledPaymentResponseDto>> getAllScheduledPayments(@PathVariable long id,HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        List<ScheduledPaymentResponseDto> list = scheduledPaymentService.getAllScheduledPaymentsByAccId(id, userId);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @DeleteMapping("/accounts/{accountId}/scheduled_payments/{id}")
    public ResponseEntity<String> deleteSchedulePayment(@PathVariable long accountId, @PathVariable long id, HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        scheduledPaymentService.deleteScheduledPayment(accountId,id, userId);
        return ResponseEntity.ok("Scheduled payment with id " + id +" deleted");
    }
    @PutMapping("/accounts/{accountId}/scheduled_payments/{id}")
    public ResponseEntity<ScheduledPaymentResponseDto> editPayment(@RequestBody ScheduledPaymentCreateDto scheduledPaymentEditDto, HttpServletRequest request,
                                                                   @PathVariable long accountId, @PathVariable long id){
        long userId = checkIfLoggedAndReturnUserId(request);
        ScheduledPaymentResponseDto dto = scheduledPaymentService.editPayment(accountId,id,scheduledPaymentEditDto, userId);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }
}
