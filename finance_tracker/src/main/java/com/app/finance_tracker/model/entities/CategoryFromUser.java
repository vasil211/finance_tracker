package com.app.finance_tracker.model.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name  = "categories_for_user")
public class CategoryFromUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;
    @ManyToOne
    @JoinColumn(name="icon_id", nullable=false)
    private Icon icon;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
}
