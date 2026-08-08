package com.shravan.paycore.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
@Entity
public class Transaction{
    @Id
    private UUID id;
}