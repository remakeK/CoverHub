package com.group.cover_hub.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Date;
import java.util.HexFormat;

@Service
public class JwtService {
    private final SecretKey key;
    private final long ACCESS_TOKEN_LIFETIME = Duration.ofMinutes(15).toMillis();
    private final long REFRESH_TOKEN_LIFETIME = Duration.ofDays(7).toMillis();

    public JwtService(@Value("${jwt.secret}") String secret){
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(String username, String type, long lifetime){
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + lifetime))
                .signWith(key)
                .compact();
    }
    private Claims parseToken(String token){
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public String hashToken(String token){
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 is not available", e
            );
        }
    }

    public String generateAccessToken(String username){
        return generateToken(username, "access", ACCESS_TOKEN_LIFETIME);
    }
    public String generateRefreshToken(String username){
        return generateToken(username, "refresh", REFRESH_TOKEN_LIFETIME);
    }

    public boolean isValidRefreshToken(String token) {
        try {
            return "refresh".equals(parseToken(token).get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isValidAccessToken(String token) {
        try {
            return "access".equals(parseToken(token).get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsernameFromAccessToken(String token){
        Claims claims = parseToken(token);
        if (!"access".equals(claims.get("type", String.class))){
            throw new JwtException("Invalid token type");
        }
        return claims.getSubject();
    }
    public String extractUsernameFromRefreshToken(String token){
        Claims claims = parseToken(token);
        if (!"refresh".equals(claims.get("type", String.class))){
            throw new JwtException("Invalid token type");
        }
        return claims.getSubject();
    }

}
