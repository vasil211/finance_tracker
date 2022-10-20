package com.app.finance_tracker.model.utility.service;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.Exeptionls.UnauthorizedException;
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
    public ScheduledPaymentResponseDto createScheduledPayment(long id, ScheduledPaymentCreateDto scheduledPaymentCreateDto) {
        //validate user as well
        Account account= getAccountById(id);
        if (!isValidAmount(scheduledPaymentCreateDto.getAmount())){
            throw new BadRequestException("Money should be higher than 0");
        }
        if (scheduledPaymentCreateDto.getDueDate().before(Date.valueOf(LocalDate.now()))){
            throw new BadRequestException("Invalid date input");
        }
        Category category = getCategoryById(scheduledPaymentCreateDto.getCategoryId());

        ScheduledPayment scheduledPayment = setFields(scheduledPaymentCreateDto);
        scheduledPayment.setAccount(account);
        scheduledPayment.setCategory(category);
        scheduledPaymentRepository.save(scheduledPayment);
        return  modelMapper.map(scheduledPayment,ScheduledPaymentResponseDto.class);
    }

    private ScheduledPayment setFields(ScheduledPaymentCreateDto dto) {
        ScheduledPayment scheduledPayment = new ScheduledPayment();
        scheduledPayment.setAmount(dto.getAmount());
        scheduledPayment.setDueDate(dto.getDueDate());
        scheduledPayment.setTitle(dto.getTitle());
        return scheduledPayment;
    }

    public ScheduledPaymentResponseDto getPaymentById(long accountId,long id) {
        if (!accountRepository.existsById(accountId)){
            throw new NotFoundException("Account does not exist");
        }
        //check if account's userId equals userId from session
        ScheduledPayment scheduledPayment= findScheduledPaymentById(id);
        if (scheduledPayment.getAccount().getId()!=accountId){
            throw new UnauthorizedException("Dont have permission to view this page");
        }
        return modelMapper.map(scheduledPayment,ScheduledPaymentResponseDto.class);
    }

    public List<ScheduledPaymentResponseDto> getAllScheduledPaymentsByAccId(long id) {
        //check if account's userId equals userId from session
        if (!accountRepository.existsById(id)){
            throw new NotFoundException("Account does not exist");
        }
        List<ScheduledPaymentResponseDto> list  = scheduledPaymentRepository.findAllByAccountId(id);
        return list;
    }
}
