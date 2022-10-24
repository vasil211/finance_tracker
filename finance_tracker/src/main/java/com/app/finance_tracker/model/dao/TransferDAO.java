package com.app.finance_tracker.model.dao;

import com.app.finance_tracker.model.entities.Transfer;
import com.app.finance_tracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TransferDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountService accountService;

    public List<Transfer> getAllTransfersWithFilter(long accountId, Date fromDate, Date toDate) {
        return jdbcTemplate.query(
                "SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer" +
                        " FROM transfers WHERE from_user_account_id = ? AND CAST(date_of_transfer AS DATE) BETWEEN ? AND ?",
                new Object[]{accountId, fromDate, toDate},
                (rs, rowNum) -> new Transfer(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        accountService.getCurrencyById(rs.getInt("currency_id")),
                        accountService.getAccountById(rs.getInt("from_user_account_id")),
                        accountService.getAccountById(rs.getInt("to_user_account_id")),
                        rs.getTimestamp("date_of_transfer").toLocalDateTime()));
    }
}
