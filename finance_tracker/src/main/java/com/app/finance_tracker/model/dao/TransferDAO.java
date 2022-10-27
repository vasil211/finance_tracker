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
            case "sent" -> initialSelectWithFromYourAccount(toAccountsIds, ownAccountsIds, fromDate, toDate, fromAmount,
                    toAmount, currencies, sb);
            case "received" ->
                    initialSelectWithToYourAccount(fromAccountsIds, ownAccountsIds, fromDate, toDate, fromAmount,
                            toAmount, currencies, sb);

            default -> throw new IllegalStateException("Unexpected value: " + choice);
        }
        sb.append(" ORDER BY date_of_transfer DESC");
        return jdbcTemplate.query(
                sb.toString(),
                (rs, rowNum) -> new Transfer(
                        rs.getLong("id"),
                        rs.getDouble("amount"),
                        accountService.getCurrencyById(rs.getInt("currency_id")),
                        accountService.getAccountById(rs.getInt("from_user_account_id")),
                        accountService.getAccountById(rs.getInt("to_user_account_id")),
                        rs.getTimestamp("date_of_transfer").toLocalDateTime(),
                        rs.getString("description")));
    }

    private void initialSelectWithFromYourAccount(List<Long> toAccountsIds, List<Long> ownAccountsIds,
                                                  LocalDate fromDate, LocalDate toDate, double fromAmount,
                                                  double toAmount, List<Long> currencies, StringBuilder sb) {
        sb.append("SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer FROM transfers WHERE");
        sb.append(" from_user_account_id");
        if (ownAccountsIds.size() == 1) {
            sb.append(" = ").append(ownAccountsIds.get(0));
        } else {
            appendAccounts(ownAccountsIds, sb);
        }
        if (toAccountsIds.size() != 0) {
            sb.append(" AND to_user_account_id");
            if (toAccountsIds.size() == 1) {
                sb.append(" = ").append(toAccountsIds.get(0));
            } else {
                appendAccounts(toAccountsIds, sb);
            }
        }
        appendCurrencyAndDate(fromDate, toDate, fromAmount, toAmount, currencies, sb);
    }

    private void initialSelectWithToYourAccount(List<Long> fromAccountsIds, List<Long> ownAccountsIds,
                                                LocalDate fromDate, LocalDate toDate, double fromAmount,
                                                double toAmount, List<Long> currencies, StringBuilder sb) {
        sb.append("SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer FROM transfers WHERE");
        sb.append(" to_user_account_id");
        if (ownAccountsIds.size() == 1) {
            sb.append(" = ").append(ownAccountsIds.get(0));
        } else {
            appendAccounts(ownAccountsIds, sb);
        }
        if (fromAccountsIds.size() != 0) {
            sb.append(" AND from_user_account_id");
            if (fromAccountsIds.size() == 1) {
                sb.append(" = ").append(fromAccountsIds.get(0));
            } else {
                appendAccounts(fromAccountsIds, sb);
            }
        }
        appendCurrencyAndDate(fromDate, toDate, fromAmount, toAmount, currencies, sb);
    }

    private static void appendAccounts(List<Long> accountIds, StringBuilder sb) {
        sb.append(" IN {");
        for (int i = 0; i < accountIds.size(); i++) {
            sb.append(accountIds.get(i));
            if (i < accountIds.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("}");
    }

    private void appendCurrencyAndDate(LocalDate fromDate, LocalDate toDate, double fromAmount,
                                       double toAmount, List<Long> currencies, StringBuilder sb) {
        if (currencies.size() != 0) {
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
        }
        if (fromDate == null) {
            fromDate = LocalDate.of(1970, 1, 1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        sb.append(" AND CAST(date_of_transfer AS DATE) BETWEEN \"").append(fromDate).append("\" AND \"")
                .append(toDate).append("\"");

        if (toAmount == 0) {
            toAmount = Double.MAX_VALUE;
        }

        sb.append(" AND amount BETWEEN ").append(fromAmount).append(" AND ").append(toAmount);
    }
}
