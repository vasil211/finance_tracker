package com.app.finance_tracker.service;

import com.app.finance_tracker.model.exceptions.BadRequestException;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.app.finance_tracker.model.exceptions.UnauthorizedException;
import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentCreateDto;
import com.app.finance_tracker.model.dto.scheduledpaymentDTO.ScheduledPaymentResponseDto;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Category;
import com.app.finance_tracker.model.entities.ScheduledPayment;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledPaymentService extends AbstractService{
    public ScheduledPaymentResponseDto createScheduledPayment(long id, ScheduledPaymentCreateDto scheduledPaymentCreateDto, long userId) {
        //validate user as well
        checkIfAccountBelongsToUser(id, userId);
        Account account= getAccountById(id);
        if (!isValidAmount(scheduledPaymentCreateDto.getAmount())){
            throw new BadRequestException("Money should be higher than 0");
        }
        if (scheduledPaymentCreateDto.getDueDate().before(Date.valueOf(LocalDate.now()))){
            throw new BadRequestException("Invalid date input");
        }
        Category category = getCategoryById(scheduledPaymentCreateDto.getCategoryId());

        ScheduledPayment scheduledPayment = new ScheduledPayment();
        setFields(scheduledPaymentCreateDto,scheduledPayment);
        scheduledPayment.setAccount(account);
        scheduledPayment.setCategory(category);
        scheduledPaymentRepository.save(scheduledPayment);
        return  modelMapper.map(scheduledPayment,ScheduledPaymentResponseDto.class);
    }

    private void setFields(ScheduledPaymentCreateDto dto,ScheduledPayment scheduledPayment) {
        scheduledPayment.setAmount(dto.getAmount());
        scheduledPayment.setDueDate(dto.getDueDate());
        scheduledPayment.setTitle(dto.getTitle());
    }

    public ScheduledPaymentResponseDto getPaymentById(long accountId,long id, long userId) {
        checkIfAccountBelongsToUser(accountId, userId);
        if (!accountRepository.existsById(accountId)){
            throw new NotFoundException("Account does not exist");
        }
        ScheduledPayment scheduledPayment= getScheduledPaymentById(id);
        if (scheduledPayment.getAccount().getId()!=accountId){
            throw new UnauthorizedException("Dont have permission to view this page");
        }
        return modelMapper.map(scheduledPayment,ScheduledPaymentResponseDto.class);
    }

    public List<ScheduledPaymentResponseDto> getAllScheduledPaymentsByAccId(long id, long userId) {
        checkIfAccountBelongsToUser(id, userId);
        if (!accountRepository.existsById(id)){
            throw new NotFoundException("Account does not exist");
        }
        List<ScheduledPaymentResponseDto> list  = scheduledPaymentRepository.findAllByAccountId(id);
        return list;
    }

    public void deleteScheduledPayment(long accountId, long id, long userId) {
        checkIfAccountBelongsToUser(accountId, userId);
        ScheduledPayment payment = getScheduledPaymentById(id);
        scheduledPaymentRepository.delete(payment);
    }

    public ScheduledPaymentResponseDto editPayment(long accountId, long id, ScheduledPaymentCreateDto scheduledPaymentEditDto, long userId) {
        checkIfAccountBelongsToUser(accountId, userId);
        ScheduledPayment scheduledPayment = getScheduledPaymentById(id);
        if (scheduledPayment.getCategory().getId() != scheduledPaymentEditDto.getCategoryId()){
            Category category = getCategoryById(scheduledPaymentEditDto.getCategoryId());
            scheduledPayment.setCategory(category);
        }
        setFields(scheduledPaymentEditDto,scheduledPayment);
        scheduledPaymentRepository.save(scheduledPayment);
        return modelMapper.map(scheduledPayment,ScheduledPaymentResponseDto.class);
    }
}
