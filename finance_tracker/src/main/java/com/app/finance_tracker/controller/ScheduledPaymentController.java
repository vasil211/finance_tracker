package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentCreateDto;
import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentResponseDto;
import com.app.finance_tracker.model.entities.ScheduledPayment;
import com.app.finance_tracker.service.ScheduledPaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ScheduledPaymentController extends AbstractController {

    @Autowired
    private ScheduledPaymentService scheduledPaymentService;

    @PostMapping("/accounts/scheduled_payments")
    public ResponseEntity<ScheduledPaymentResponseDto> schedulePayment (
            @RequestBody ScheduledPaymentCreateDto scheduledPaymentCreateDto, HttpServletRequest req){
        long userId = checkIfLoggedAndReturnUserId(req);
        ScheduledPaymentResponseDto dto =
                scheduledPaymentService.createScheduledPayment(scheduledPaymentCreateDto, userId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
    @GetMapping("/accounts/scheduled_payments/{id}")
    public ResponseEntity<ScheduledPaymentResponseDto> getSchedulePayment(@PathVariable long id,
                                                                          @RequestParam long accountId,
                                                                          HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        return new ResponseEntity<>(scheduledPaymentService.getPaymentById(accountId, id, userId),HttpStatus.OK);
    }

    @GetMapping("/accounts/scheduled_payments")
    public ResponseEntity<List<ScheduledPaymentResponseDto>> getAllScheduledPayments(@RequestParam long accountId,
                                                                                     HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        List<ScheduledPaymentResponseDto> list = scheduledPaymentService.getAllScheduledPaymentsByAccId(accountId, userId);
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @DeleteMapping("/accounts/scheduled_payments")
    public ResponseEntity<String> deleteSchedulePayment(@RequestParam long id, HttpServletRequest request){
        long userId = checkIfLoggedAndReturnUserId(request);
        String message = scheduledPaymentService.deleteScheduledPayment(id, userId);
        return ResponseEntity.ok(message);
    }
    @PutMapping("/accounts/scheduled_payments/{id}")
    public ResponseEntity<ScheduledPaymentResponseDto> editPayment(
            @RequestBody ScheduledPaymentCreateDto scheduledPaymentEditDto, HttpServletRequest request,
                                                                                @PathVariable long id){
        long userId = checkIfLoggedAndReturnUserId(request);
        ScheduledPaymentResponseDto dto = scheduledPaymentService.editPayment(id,scheduledPaymentEditDto, userId);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }
}
