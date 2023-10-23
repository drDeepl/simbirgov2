package ru.simbirgo.payloads;

import lombok.Data;

@Data
public class SignUpAdminRequest {
    private String username;
    private String password;
    private Boolean isAdmin;
    private Double balance;
}
