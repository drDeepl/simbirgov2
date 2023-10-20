package ru.simbirgo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TokenRefreshDTO {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";

    public TokenRefreshDTO(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
