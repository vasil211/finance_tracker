package com.app.finance_tracker.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name  = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @ManyToOne
    @JoinColumn(name="currency_id", nullable=false)
    private Currency currency;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    @Column
    private double balance;

    public void removeFromBalance(double amount) {
        this.balance-= amount;
    }
}
