package com.banking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "iban", unique = true, nullable = false)
    private String iban;

    @Column(name = "balance_ron", nullable = false)
    private double balance;

    public void addFunds(double amount) {
        this.balance += amount;
    }

    public void subtractFunds(double amount) {
        this.balance -= amount;
    }
}