package com.app.finance_tracker.model.dto.transferDTO;

import com.app.finance_tracker.model.entities.Transfer;
import com.app.finance_tracker.model.exceptions.NotFoundException;
import com.app.finance_tracker.model.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.swing.tree.RowMapper;
import javax.swing.tree.TreePath;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class TransferRowMapper{
        /*implements RowMapper<Transfer> {*/
@Autowired
private CurrencyRepository currencyRepository;

    /*public Transfer mapRow(ResultSet rs) throws SQLException {

        Transfer transfer = new Transfer();
        transfer.setId(rs.getLong("id"));
        transfer.setAmount(rs.getDouble("amount"));
        transfer.setCurrency(currencyRepository.findById(rs.getLong("currency_id"))
                .orElseThrow(() -> new NotFoundException("Currency not found")));
        customer.setCreatedDate(rs.getTimestamp("created_date").toLocalDateTime());

        return customer;

    }*/

    /*@Override
    public int[] getRowsForPaths(TreePath[] path) {
        return new int[0];
    }*/
}
