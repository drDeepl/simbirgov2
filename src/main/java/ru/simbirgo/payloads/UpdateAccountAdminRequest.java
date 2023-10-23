package ru.simbirgo.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateAccountAdminRequest {
    private String username;
    private String password;
    private Double balance;
    private Boolean isAdmin;
}
