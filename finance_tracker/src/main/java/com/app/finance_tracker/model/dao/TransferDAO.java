package com.app.finance_tracker.model.dao;


import com.app.finance_tracker.model.entities.Transfer;
import com.app.finance_tracker.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
@Component
public class TransferDAO {
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    @Autowired
    private AccountService accountService;

    public List<Transfer> getAllSent(List<Long> fromAccountsIds, List<Long> toAccountsIds, LocalDate fromDate,
                                     LocalDate toDate, double fromAmount, double toAmount, List<Long> currencies) {

        MapSqlParameterSource map = new MapSqlParameterSource();
        if(toAmount == 0){
            toAmount = Double.MAX_VALUE;
        }
        if (fromDate == null) {
            fromDate = LocalDate.of(1970, 1, 1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        map.addValue("from", fromAccountsIds);
        map.addValue("fromDate", fromDate);
        map.addValue("toDate", toDate);
        map.addValue("fromAmount", fromAmount);
        map.addValue("toAmount", toAmount);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer,description " +
                "FROM transfers WHERE from_user_account_id IN (:from)");
        if (toAccountsIds.size() > 0) {
            sb.append(" AND to_user_account_id IN (:to)");
            map.addValue("to", toAccountsIds);
        }
        if (currencies.size() > 0) {
            sb.append(" AND currency_id IN (:currency)");
            map.addValue("currency", currencies);
        }
        sb.append(" AND CAST(date_of_transfer as DATE) BETWEEN :fromDate AND :toDate AND amount BETWEEN :fromAmount AND :toAmount " +
                "ORDER BY date_of_transfer DESC");

        String query = sb.toString();

        return namedJdbcTemplate.query(
                query,
                map,
                (rs, rowNum) -> new Transfer(
                        rs.getLong("id"),
                        rs.getDouble("amount"),
                        accountService.getCurrencyById(rs.getInt("currency_id")),
                        accountService.getAccountById(rs.getInt("from_user_account_id")),
                        accountService.getAccountById(rs.getInt("to_user_account_id")),
                        rs.getTimestamp("date_of_transfer").toLocalDateTime(),
                        rs.getString("description")));
    }

    public List<Transfer> getAllReceived(List<Long> fromAccountsIds, List<Long> toAccountsIds, LocalDate fromDate,
                                         LocalDate toDate, double fromAmount, double toAmount, List<Long> currencies) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        if(toAmount == 0){
            toAmount = Double.MAX_VALUE;
        }
        if (fromDate == null) {
            fromDate = LocalDate.of(1970, 1, 1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        map.addValue("to", fromAccountsIds);
        map.addValue("fromDate", fromDate);
        map.addValue("toDate", toDate);
        map.addValue("fromAmount", fromAmount);
        map.addValue("toAmount", toAmount);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer,description " +
                "FROM transfers WHERE to_user_account_id IN (:to)");
        if (toAccountsIds.size() > 0) {
            sb.append(" AND from_user_account_id IN (:to)");
            map.addValue("from", toAccountsIds);
        }
        if (currencies.size() > 0) {
            sb.append(" AND currency_id IN (:currency)");
            map.addValue("currency", currencies);
        }
        sb.append(" AND CAST(date_of_transfer as DATE) BETWEEN :fromDate AND :toDate AND amount BETWEEN :fromAmount AND :toAmount " +
                "ORDER BY date_of_transfer DESC");

        return namedJdbcTemplate.query(
                sb.toString(),
                map,
                (rs, rowNum) -> new Transfer(
                        rs.getLong("id"),
                        rs.getDouble("amount"),
                        accountService.getCurrencyById(rs.getInt("currency_id")),
                        accountService.getAccountById(rs.getInt("from_user_account_id")),
                        accountService.getAccountById(rs.getInt("to_user_account_id")),
                        rs.getTimestamp("date_of_transfer").toLocalDateTime(),
                        rs.getString("description")));
    }

    public List<Transfer> getAll(List<Long> ownAccountsIds, List<Long> otherAccountsIds, LocalDate fromDate,
                                 LocalDate toDate, double fromAmount, double toAmount, List<Long> currencies) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        if(toAmount == 0){
            toAmount = Double.MAX_VALUE;
        }
        if (fromDate == null) {
            fromDate = LocalDate.of(1970, 1, 1);
        }
        if (toDate == null) {
            toDate = LocalDate.now();
        }
        map.addValue("own", ownAccountsIds);
        map.addValue("fromDate", fromDate);
        map.addValue("toDate", toDate);
        map.addValue("fromAmount", fromAmount);
        map.addValue("toAmount", toAmount);

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer,description " +
                "FROM transfers WHERE from_user_account_id IN (:own)");
        if (otherAccountsIds.size() > 0) {
            sb.append(" AND to_user_account_id IN (:to)");
            map.addValue("to", otherAccountsIds);
        }
        if (currencies.size() > 0) {
            sb.append(" AND currency_id IN (:currency)");
            map.addValue("currency", currencies);
        }
        sb.append(" AND CAST(date_of_transfer as DATE) BETWEEN :fromDate AND :toDate AND amount BETWEEN :fromAmount AND :toAmount ");
        sb.append(" UNION ");

        sb.append("SELECT id,amount,currency_id,from_user_account_id,to_user_account_id,date_of_transfer,description " +
                "FROM transfers WHERE to_user_account_id IN (:own)");
        if (otherAccountsIds.size() > 0) {
            sb.append(" AND from_user_account_id IN (:from)");
            map.addValue("from", otherAccountsIds);
        }
        if (currencies.size() > 0) {
            sb.append(" AND currency_id IN (:currency)");
        }
        sb.append(" AND CAST(date_of_transfer as DATE) BETWEEN :fromDate AND :toDate AND amount BETWEEN :fromAmount AND :toAmount " +
                "ORDER BY date_of_transfer DESC");
        System.out.println(sb.toString());
        return namedJdbcTemplate.query(
                sb.toString(),
                map,
                (rs, rowNum) -> new Transfer(
                        rs.getLong("id"),
                        rs.getDouble("amount"),
                        accountService.getCurrencyById(rs.getInt("currency_id")),
                        accountService.getAccountById(rs.getInt("from_user_account_id")),
                        accountService.getAccountById(rs.getInt("to_user_account_id")),
                        rs.getTimestamp("date_of_transfer").toLocalDateTime(),
                        rs.getString("description")));
    }
}

