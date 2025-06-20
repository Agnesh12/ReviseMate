package com.example.revisemate.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    private final byte[] secretKeyBytes;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        // Initialize secretKeyBytes here inside constructor (no @PostConstruct)
        this.secretKeyBytes = secretKey.getBytes();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .setSigningKey(secretKeyBytes)
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username.trim().toLowerCase())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SignatureAlgorithm.HS256, secretKeyBytes)
                .compact();
    }

    public boolean isTokenValid(String token, String username) {
        if (token == null || username == null) return false;
        final String tokenUsername = extractUsername(token);
        return tokenUsername.trim().equalsIgnoreCase(username.trim()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
