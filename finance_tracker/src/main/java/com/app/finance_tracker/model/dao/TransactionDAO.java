package com.app.finance_tracker.model.dao;

import com.app.finance_tracker.model.entities.Transaction;
import com.app.finance_tracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Component
public class TransactionDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AccountService accountService;

    public List<Transaction> getFilteredTransactions(List<Long> accountsIds, double fromAmount, double toAmount,
                                                     LocalDate fromDate, LocalDate toDate,List<Long> categoryIds){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT id,account_id,amount,category_id,created_at,description FROM transactions WHERE account_id");
        if (accountsIds.size()!=1){
            sb.append(" IN (");
            for (int i = 0; i < accountsIds.size(); i++) {
                sb.append(accountsIds.get(i));
                if (i<accountsIds.size()-1){
                    sb.append(",");
                }
            }
            sb.append(")");
        }
        else {
            sb.append(" = ").append(accountsIds.get(0));
        }
        if (categoryIds.size()!=0){
            sb.append(" AND category_id");
            if (categoryIds.size()!=1){
                sb.append(" IN (");
                for (int i = 0; i < categoryIds.size(); i++) {
                    sb.append(categoryIds.get(i));
                    if (i<categoryIds.size()-1){
                        sb.append(",");
                    }
                }
                sb.append(")");
            }
            else {
                sb.append(" = ").append(categoryIds.get(0));
            }
        }
        if (fromDate == null) {
            fromDate = LocalDate.of(1970, 1, 1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        sb.append(" AND CAST(created_at AS DATE) BETWEEN \"").append(fromDate).append("\" AND \"")
                .append(toDate).append("\"");
        if (toAmount==0){
            toAmount=Double.MAX_VALUE;
        }
        sb.append(" AND amount BETWEEN ").append(fromAmount).append(" AND ").append(toAmount);
        sb.append(" ORDER BY created_at");

        return jdbcTemplate.query(
                sb.toString(),
                (rs, rowNum) -> new Transaction(
                    rs.getLong("id"),
                    accountService.getAccountById(rs.getLong("account_id")),
                    rs.getDouble("amount"),
                    accountService.getCategoryById(rs.getLong("category_id")),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getString("description")));
    }

    public List<Transaction> getTransactionsByUserId(long userId){
        String sql = "SELECT t.id,t.account_id,t.amount,t.category_id,t.created_at,t.description FROM transactions AS t " +
                "JOIN accounts AS a ON (a.id=t.account_id) WHERE a.user_id = ?";
        return jdbcTemplate.query(sql,new Object[]{userId},(rs, rowNum) -> new Transaction(
                rs.getLong("id"),
                accountService.getAccountById(rs.getLong("id")),
                rs.getDouble("amount"),
                accountService.getCategoryById(rs.getLong("category_id")),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getString("description")));
    }
}
