package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.Exeptionls.BadRequestException;
import com.app.finance_tracker.model.Exeptionls.InvalidArgumentsException;
import com.app.finance_tracker.model.Exeptionls.NotFoundException;
import com.app.finance_tracker.model.Exeptionls.UnauthorizedException;
import com.app.finance_tracker.model.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

public abstract class MasterControllerForExceptionHandlers {


    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ErrorDTO handleNotFoundException(Exception e) {
        return buildError(e,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ErrorDTO BadRequestException(Exception e) {
        return buildError(e,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidArgumentsException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    protected ErrorDTO InvalidArgumentsException(Exception e) {
        return buildError(e,HttpStatus.NOT_ACCEPTABLE);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    protected ErrorDTO handleUnauthorizedException(Exception e) {
        return buildError(e,HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected ErrorDTO handleAllOtherExceptions(Exception e) {
        return buildError(e,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorDTO buildError(Exception e,HttpStatus status){
        e.printStackTrace();
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setMessage(e.getMessage());
        errorDTO.setTime(LocalDateTime.now());
        errorDTO.setStatus(status.value());
        return errorDTO;
    }
}
