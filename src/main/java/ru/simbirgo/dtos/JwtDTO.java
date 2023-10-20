package ru.simbirgo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtDTO {
    private Long id;
    private String token;
    private String username;
    private Boolean isAdmin;
    private String type = "Bearer";
    private String refreshToken;

    public JwtDTO(Long id, String token, String username, Boolean isAdmin, String refreshToken){
        this.id = id;
        this.token = token;
        this.username = username;
        this.isAdmin = isAdmin;
        this.refreshToken = refreshToken;
    }


}
