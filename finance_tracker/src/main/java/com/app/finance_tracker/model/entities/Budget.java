package com.app.finance_tracker.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Table(name = "budgets")
@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private double amount;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "from_date")
    private Date fromDate;
    @Column(name = "to_date")
    private Date toDate;

    public void increaseAmount(double amount){
        this.amount+=amount;
    }
    public void decreaseAmount(double amount){
        this.amount-=amount;
    }
}
