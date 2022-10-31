package com.app.finance_tracker.service;

import com.app.finance_tracker.model.exceptions.BadRequestException;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.app.finance_tracker.model.exceptions.UnauthorizedException;
import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentCreateDto;
import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentResponseDto;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.ScheduledPayment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledPaymentService extends AbstractService {
    public ScheduledPaymentResponseDto createScheduledPayment(ScheduledPaymentCreateDto scheduledPaymentCreateDto, long userId) {
        //validate user as well
        checkIfAccountBelongsToUser(scheduledPaymentCreateDto.getAccountId(), userId);
        Account account = getAccountById(scheduledPaymentCreateDto.getAccountId());
        if (!isValidAmount(scheduledPaymentCreateDto.getAmount())) {
            throw new BadRequestException("Money should be higher than 0");
        }
        if (scheduledPaymentCreateDto.getDueDate().isBefore(LocalDate.now())){

            throw new BadRequestException("Invalid date input");
        }
        Category category = getCategoryById(scheduledPaymentCreateDto.getCategoryId());

        ScheduledPayment scheduledPayment = new ScheduledPayment();
        setFields(scheduledPaymentCreateDto, scheduledPayment);
        scheduledPayment.setAccount(account);
        scheduledPayment.setCategory(category);
        scheduledPaymentRepository.save(scheduledPayment);
        return modelMapper.map(scheduledPayment, ScheduledPaymentResponseDto.class);
    }

    private void setFields(ScheduledPaymentCreateDto dto, ScheduledPayment scheduledPayment) {
        scheduledPayment.setAmount(dto.getAmount());
        scheduledPayment.setDueDate(dto.getDueDate());
        scheduledPayment.setTitle(dto.getTitle());
    }

    public ScheduledPaymentResponseDto getPaymentById(long accountId, long id, long userId) {
        checkIfAccountBelongsToUser(accountId, userId);
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("Account does not exist");
        }
        ScheduledPayment scheduledPayment = getScheduledPaymentById(id);
        if (scheduledPayment.getAccount().getId() != accountId) {
            throw new UnauthorizedException("Dont have permission to view this page");
        }
        return modelMapper.map(scheduledPayment, ScheduledPaymentResponseDto.class);
    }

    public List<ScheduledPaymentResponseDto> getAllScheduledPaymentsByAccId(long id, long userId) {
        checkIfAccountBelongsToUser(id, userId);
        if (!accountRepository.existsById(id)) {
            throw new NotFoundException("Account does not exist");
        }
        List<ScheduledPayment> list = scheduledPaymentRepository.findAllByAccountId(id);
        List<ScheduledPaymentResponseDto> responseDtos = list.stream().map(scheduledPayment ->
                modelMapper.map(scheduledPayment, ScheduledPaymentResponseDto.class)).toList();
        return responseDtos;

    }

    public String deleteScheduledPayment(long accountId, long id, long userId) {
        checkIfAccountBelongsToUser(accountId, userId);
        ScheduledPayment payment = getScheduledPaymentById(id);
        scheduledPaymentRepository.delete(payment);
        return "Scheduled payment '"+ payment.getTitle() +"' deleted successfully";
    }

    public ScheduledPaymentResponseDto editPayment(long id, ScheduledPaymentCreateDto scheduledPaymentEditDto, long userId) {
        checkIfAccountBelongsToUser(scheduledPaymentEditDto.getAccountId(), userId);
        ScheduledPayment scheduledPayment = getScheduledPaymentById(id);
        if (scheduledPayment.getCategory().getId() != scheduledPaymentEditDto.getCategoryId()) {
            Category category = getCategoryById(scheduledPaymentEditDto.getCategoryId());
            scheduledPayment.setCategory(category);
        }
        setFields(scheduledPaymentEditDto, scheduledPayment);
        scheduledPaymentRepository.save(scheduledPayment);
        return modelMapper.map(scheduledPayment, ScheduledPaymentResponseDto.class);
    }

    //create method to send email on day of scheduled payment
    @Scheduled(cron = "0 9 * * * *")
    public void doScheduledPayment(){
        //get all scheduled payments
            System.out.println(LocalDate.now());
            List<ScheduledPayment> scheduledPayments = scheduledPaymentRepository.findAll().stream().filter(sp -> sp.getDueDate().equals(LocalDate.now())).toList();
            for (ScheduledPayment sp: scheduledPayments) {
                if (sp.getAccount().getBalance()>=sp.getAmount()){

            }
        }
        //if date is today
            //check for enough money
            //if enough make transaction otherwise dont make
        //send email of result
    }
}
