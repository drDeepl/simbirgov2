package ru.simbirgo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name="accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", nullable = false, unique = true)
    private String username;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="balance")
    private Double balance;

    @Column(name="is_admin", nullable = false, columnDefinition="BOOLEAN DEFAULT false")
    private Boolean isAdmin;


    public Account(String username, String password, Double balance, Boolean isAdmin){
        this.username = username;
        this.password = password;
        this.balance=balance;
        this.isAdmin = isAdmin;
    }

    public Account(String username, Double balance, Boolean isAdmin){
        this.username = username;
        this.balance=balance;
        this.isAdmin = isAdmin;
    }

    public Account(String username, String password){
        this.username = username;
        this.password = password;
    }

}
