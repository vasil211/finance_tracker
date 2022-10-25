package com.app.finance_tracker.model.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name  = "currencies")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String code;
    @Column
    private String name;
    @Column
    private String symbol;
    @Column
    private String namePlural;

    @OneToMany(mappedBy = "currency")
    private List<Account> accounts;

}
