package com.kindred.islab1.authentication;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

import static com.kindred.islab1.Constants.ACTIVATION_KEY;
import static com.kindred.islab1.Constants.EXPIRATION_ACTIVATION_TOKEN;

@Component
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key:secret}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds;

    private static Key key;

    private static Key activationKey;


    @PostConstruct
    protected void init() {
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("The secret key must be at least 32 characters long");
        }
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
        activationKey = Keys.hmacShaKeyFor(ACTIVATION_KEY.getBytes());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .claim("sub", username)
                .claim("iat", new Date())  // Issued at
                .claim("exp", new Date(System.currentTimeMillis() + validityInMilliseconds))  // Expiration
                .signWith(key)  // Sign with the SecretKey
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateActivateToken(String email) {
        return Jwts.builder()
                .claim("sub", email)
                .claim("iat", new Date())  // Issued at
                .claim("exp", new Date(System.currentTimeMillis() + EXPIRATION_ACTIVATION_TOKEN))  // Expiration
                .signWith(activationKey)  // Sign with the SecretKey
                .compact();
    }

    public boolean validateActivationToken(String token) {

        try {
            Jwts.parserBuilder()
                    .setSigningKey(activationKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromActivationToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(activationKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}