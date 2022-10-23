package com.app.finance_tracker.model.dao;

import com.app.finance_tracker.model.entities.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

@Component
public class TransferDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Transfer> findAll() {

        String sql = "SELECT * FROM CUSTOMER";

        List<Transfer> transfers = jdbcTemplate.query(
                sql,
                new CustomerRowMapper());

        return customers;

    }
}
