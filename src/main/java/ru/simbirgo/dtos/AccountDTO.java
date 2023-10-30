package ru.simbirgo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AccountDTO {
    private Long id;
    private String username;
    private Boolean isAdmin;
    private Double balance;
}
