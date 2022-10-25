package com.app.finance_tracker.model.dao;

import com.app.finance_tracker.model.entities.Account;
import com.app.finance_tracker.model.entities.Transfer;
import com.app.finance_tracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TransferDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AccountService accountService;

    public List<Transfer> getAllTransfersFiltered(List<Long> toAccountsIds, List<Long> fromAccountsIds,
                                                  List<Long> ownAccountsIds, LocalDate fromDate, LocalDate toDate,
                                                  double fromAmount, double toAmount, List<Long> currencies, String choice) {
        StringBuilder sb = new StringBuilder();
        switch (choice) {
            case "all" -> {
                initialSelectWithToYourAccount(fromAccountsIds, ownAccountsIds, fromDate, toDate, fromAmount,
                        toAmount, currencies, sb);
                sb.append(" UNION ");
                initialSelectWithFromYourAccount(toAccountsIds, ownAccountsIds, fromDate, toDate, fromAmount,
                        toAmount, currencies, sb);
            }
            case "sent" ->
                    initialSelectWithFromYourAccount(toAccountsIds, ownAccountsIds, fromDate, toDate, fromAmount,
                            toAmount, currencies, sb);
            case "received" ->
                    initialSelectWithToYourAccount(fromAccountsIds, ownAccountsIds, fromDate, toDate, fromAmount,
                            toAmount, currencies, sb);
            default -> throw new IllegalStateException("Unexpected value: " + choice);
        }
        // todo remove sout
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
    private void initialSelectWithFromYourAccount(List<Long> toAccountsIds, List<Long> ownAccountsIds,
                                                  LocalDate fromDate, LocalDate toDate, double fromAmount,
                                                  double toAmount, List<Long> currencies, StringBuilder sb) {
        sb.append("SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer FROM transfers WHERE");
        // if toAccountsIds is empty, then we don't need to add it to where
        sb.append(" from_user_account_id");
        if (ownAccountsIds.size() == 1) {
            sb.append(" = ").append(ownAccountsIds.get(0));
        } else {
            appendFromAccount(ownAccountsIds, sb);
        }
        // if toAccountsIds is empty, then we don't need to add it to where
        sb.append(" AND to_user_account_id");
        if (toAccountsIds.size() == 1) {
            sb.append(" = ").append(toAccountsIds.get(0));
        } else {
            appendToAccount(toAccountsIds, sb);
        }
        appendCurrencyAndDate(fromDate, toDate, fromAmount, toAmount, currencies, sb);
    }
    private void initialSelectWithToYourAccount(List<Long> fromAccountsIds, List<Long> ownAccountsIds,
                                                LocalDate fromDate, LocalDate toDate, double fromAmount,
                                                double toAmount, List<Long> currencies, StringBuilder sb) {
        sb.append("SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer FROM transfers WHERE");
        // if fromAccountsIds is empty, then we don't need to add it to where
        sb.append(" to_user_account_id");
        if (ownAccountsIds.size() == 1) {
            sb.append(" = ").append(ownAccountsIds.get(0));
        } else {
            appendToAccount(ownAccountsIds, sb);
        }
        // if fromAccountsIds is empty, then we don't need to add it to where
        sb.append(" AND from_user_account_id");
        if (fromAccountsIds.size() == 1) {
            sb.append(" = ").append(fromAccountsIds.get(0));
        } else {
            appendFromAccount(fromAccountsIds, sb);
        }
        appendCurrencyAndDate(fromDate, toDate, fromAmount, toAmount, currencies, sb);
    }
    private static void appendFromAccount(List<Long> fromAccountsIds, StringBuilder sb) {
        sb.append(" IN {");
        for (int i = 0; i < fromAccountsIds.size(); i++) {
            sb.append(fromAccountsIds.get(i));
            if (i < fromAccountsIds.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("}");
    }
    private void appendToAccount(List<Long> toAccountsIds, StringBuilder sb) {
        sb.append(" IN {");
        for (int i = 0; i < toAccountsIds.size(); i++) {
            sb.append(toAccountsIds.get(i));
            if (i < toAccountsIds.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("}");
    }
    private void appendCurrencyAndDate(LocalDate fromDate, LocalDate toDate, double fromAmount,
                                       double toAmount, List<Long> currencies, StringBuilder sb) {
        sb.append(" AND currency_id");
        if (currencies.size() == 1) {
            sb.append(" = ").append(currencies.get(0));
        } else {
            for (int i = 0; i < currencies.size(); i++) {
                sb.append(currencies.get(i));
                if (i < currencies.size() - 1) {
                    sb.append(",");
                }
            }
            sb.append("}");
        }
        sb.append(" AND CAST(date_of_transfer AS DATE) BETWEEN \"").append(fromDate).append("\" AND \"")
                .append(toDate).append("\"");
        sb.append(" AND amount BETWEEN ").append(fromAmount).append(" AND ").append(toAmount);
        sb.append(" ORDER BY date_of_transfer DESC");
    }
}
