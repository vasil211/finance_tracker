package com.app.finance_tracker.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name  = "accounts")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String abbreviation;
    @Column
    private String full_name;

}
