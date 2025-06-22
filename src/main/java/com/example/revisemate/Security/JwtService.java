package com.example.revisemate.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret:default_super_long_test_key_12345678901234567890}") String secretKeyString) {
        System.out.println("🔐 JWT Secret received (first 10 chars): " + secretKeyString.substring(0, Math.min(secretKeyString.length(), 10)) + "...");
        System.out.println("🔑 JWT Secret length: " + secretKeyString.length());

        if (secretKeyString.length() < 32) {
            throw new IllegalArgumentException("❌ JWT secret key must be at least 32 characters long");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
        System.out.println("✅ JwtService initialized successfully");
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username.trim().toLowerCase())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(secretKey, SignatureAlgorithm.HS256)
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

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}
