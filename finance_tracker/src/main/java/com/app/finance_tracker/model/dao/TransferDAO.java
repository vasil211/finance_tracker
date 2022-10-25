package com.app.finance_tracker.model.dao;

import com.app.finance_tracker.model.entities.Currency;
import com.app.finance_tracker.model.entities.Transfer;
import com.app.finance_tracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class TransferDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountService accountService;

    /*public List<Transfer> getAllTransfersWithFilterByDate(long accountId, Date fromDate, Date toDate) {
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
    }*/

    public List<Transfer> getAllTransfersFiltered(List<Integer> receivedIds, List<Integer> sentIds, LocalDate fromDate, LocalDate toDate,
                                                  double fromAmount, double toAmount, List<Integer> currencies) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer FROM transfers WHERE");

        sb.append(" to_user_account_id");
        if (receivedIds.size()== 1){
            sb.append(" = " + receivedIds.get(0));
        }
        else {
            sb.append(" IN {");
            for (int i = 0; i < receivedIds.size(); i++) {
                sb.append("" + receivedIds.get(i));
                if (i < receivedIds.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("}");
        }
        sb.append(" AND from_user_account_id");
        if (sentIds.size()==1){
            sb.append(" = " +sentIds.get(0));
        }
        else {
            sb.append(" IN {");
            for (int i = 0; i < sentIds.size(); i++) {
                sb.append("" + sentIds.get(i));
                if (i < sentIds.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("}");
        }
        sb.append(" AND currency_id");
        if (currencies.size()==1){
            sb.append(" = " + currencies.get(0));
        }
        else {
            for (int i = 0; i < currencies.size(); i++) {
                sb.append("" + currencies.get(i));
                if (i < currencies.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("}");
        }

        sb.append(" AND CAST(date_of_transfer AS DATE) BETWEEN \"" +fromDate +"\" AND \"" + toDate+"\"");
        sb.append(" AND amount BETWEEN " + fromAmount + " AND " + toAmount);
        System.out.println(sb.toString());
        return jdbcTemplate.query(
                sb.toString(),
                (rs, rowNum) -> new Transfer(
                        rs.getLong("id"),
                        rs.getDouble("amount"),
                        accountService.getCurrencyById(rs.getInt("currency_id")),
                        accountService.getAccountById(rs.getInt("from_user_account_id")),
                        accountService.getAccountById(rs.getInt("to_user_account_id")),
                        rs.getTimestamp("date_of_transfer").toLocalDateTime()));
    }
}
