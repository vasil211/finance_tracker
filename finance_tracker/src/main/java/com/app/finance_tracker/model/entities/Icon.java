package com.app.finance_tracker.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name  = "icons")
public class Icon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String url;

}
