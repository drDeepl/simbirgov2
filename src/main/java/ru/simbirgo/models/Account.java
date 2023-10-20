package ru.simbirgo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor

@Table(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    private Double balance;
    private Boolean isAdmin;

    public Account(String username, String password, Double balance, Boolean isAdmin){
        this.username = username;
        this.password = password;
        this.balance=balance;
        this.isAdmin = isAdmin;
    }

}
