package ru.simbirgo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInDTO {

    private String username;


    private String password;
}
