package ru.simbirgo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtDTO {

    private String type = "Bearer";
    private String token;
    private String refreshToken;

    public JwtDTO(String token, String refreshToken){
        this.token = token;
        this.refreshToken = refreshToken;
    }


}
