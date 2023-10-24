package ru.simbirgo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDTO {

    private Long id;
    private String username;
    private Boolean isAdminl;
    private Double balance;

}
