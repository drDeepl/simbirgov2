package ru.simbirgo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtDTO {

    private String token;
    private String username;

    private String type = "Bearer";
    private String refreshToken;

    public JwtDTO(String token, String username, String refreshToken){
        this.token = token;
        this.username = username;
        this.refreshToken = refreshToken;
    }


}
