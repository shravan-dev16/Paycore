package com.shravan.paycore.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal balance;
    @OneToOne
    @JoinColumn(name= "user_id")
    private User user;
}
