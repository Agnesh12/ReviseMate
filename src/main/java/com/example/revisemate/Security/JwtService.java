package com.example.revisemate.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKeyBase64;         // Base64-encoded secret from properties

    private Key signInKey;                  // initialized once at startup
    private static final long EXP_MS = 1000 * 60 * 60 * 24; // 24 h

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        this.signInKey = Keys.hmacShaKeyFor(keyBytes);      // ≥256-bit key
    }

    /* ---------- token generation ---------- */

    public String generateToken(Long userId) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(String.valueOf(userId))         // <-- store userId here
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + EXP_MS))
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /* ---------- extraction helpers ---------- */

    public Long extractUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims,T> resolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signInKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return resolver.apply(claims);
    }

    /* ---------- validation ---------- */

    public boolean isTokenValid(String token, Long expectedUserId) {
        return expectedUserId.equals(extractUserId(token)) && !isExpired(token);
    }

    private boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /* ---------- convenience ---------- */

    public String resolveToken(HttpServletRequest request) {
        String hdr = request.getHeader("Authorization");
        return (hdr != null && hdr.startsWith("Bearer ")) ? hdr.substring(7) : null;
    }
}
