package com.app.finance_tracker.controller;

import com.app.finance_tracker.model.exceptions.BadRequestException;
import com.app.finance_tracker.model.exceptions.InvalidArgumentsException;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.app.finance_tracker.model.exceptions.UnauthorizedException;
import com.app.finance_tracker.model.dto.ErrorDTO;
import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

public abstract class AbstractController {

    @Autowired
    private AccountService accountService;

    public static final String LOGGED = "LOGGED";
    public static final String USER_ID = "USER_ID";
    public static final String REMOTE_IP = "REMOTE_IP";

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

    public void logUser(HttpServletRequest req, long id){
        HttpSession session = req.getSession();
        session.setAttribute(LOGGED, true);
        session.setAttribute(USER_ID, id);
        session.setAttribute(REMOTE_IP, req.getRemoteAddr());
    }

    public long checkIfLoggedAndReturnUserId(HttpServletRequest req){
        checkIfLogged(req);
        HttpSession session = req.getSession();
        return (long) session.getAttribute(USER_ID);
    }
    public void checkIfLogged(HttpServletRequest req){
        HttpSession session = req.getSession();
        String ip = req.getRemoteAddr();
        if( session.isNew() ||
                session.getAttribute(LOGGED) == null ||
                (!(boolean) session.getAttribute(LOGGED)) ||
                !session.getAttribute(REMOTE_IP).equals(ip)){
            throw new UnauthorizedException("You have to login!");
        }
    }

    protected void checkIfAccountBelongsToUser(long accountId, HttpServletRequest request){
        Account account = accountService.getAccountById(accountId);
        if(account.getUser().getId() != (int) request.getSession().getAttribute(USER_ID)){
            throw new UnauthorizedException("You are not authorized to update this account");
        }
    }
}
