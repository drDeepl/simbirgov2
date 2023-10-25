package ru.simbirgo.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.simbirgo.services.AccountDetailsImpl;
import io.jsonwebtoken.*;

import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationms}")
    private int jwtExpirationMs;

    public String generateJwtToken(AccountDetailsImpl accountPrincipal) {
        boolean isAdminAccount = accountPrincipal.getAuthorities().toArray()[0] == "ROLE_ADMIN";
        long accountId = accountPrincipal.getId();
        return generateTokenFromUsername(accountPrincipal.getUsername(), isAdminAccount, accountId);
    }

    public String generateTokenFromUsername(String username, boolean isAdmin, Long accountId) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date((new Date()).getTime() + jwtExpirationMs);
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("id", accountId);
        claims.put("isAdmin", isAdmin);
        return Jwts.builder()
                .setSubject(username).setIssuedAt(createdDate)
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    public boolean getIsAdminFromAccountDetails(AccountDetailsImpl accountDetails) {
        String accountRole = accountDetails.getAuthorities().toArray()[0].toString();
        boolean isAdmin = StringUtils.equals(accountRole, "ROLE_ADMIN");
        return isAdmin;
    }

    public Long getAccountIdFromJWT(String token){

       String accountId = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().get("id").toString();

       return Long.parseLong(accountId);
    }


}