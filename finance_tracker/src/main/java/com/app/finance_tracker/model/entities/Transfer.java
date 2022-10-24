package com.app.finance_tracker.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name  = "transfers")

public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private double amount;
    @ManyToOne
    @JoinColumn(name = "currency_id", nullable = false)
    private Currency currency;
    @ManyToOne
    @JoinColumn(name = "from_user_account_id", nullable = false)
    private Account sender;
    @ManyToOne
    @JoinColumn(name = "to_user_account_id", nullable = false)
    private Account receiver;
    @Column(name = "date_of_transfer")
    private LocalDateTime date;
}
