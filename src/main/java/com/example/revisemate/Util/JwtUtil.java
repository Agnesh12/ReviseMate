package com.example.revisemate.Util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${refresh.token.duration.ms}")
    private long refreshTokenDurationMs;

    private static final long ACCESS_TOKEN_DURATION_MS = 15 * 60 * 1000; // 15 min

    /* ------------ INTERNAL ------------ */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    private String buildToken(Long userId, long duration) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + duration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ------------ PUBLIC API ------------ */
    public String generateAccessToken(Long userId)  { return buildToken(userId, ACCESS_TOKEN_DURATION_MS); }
    public String generateRefreshToken(Long userId) { return buildToken(userId, refreshTokenDurationMs);  }

    public Long validateTokenAndGetUserId(String token) throws JwtException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }
}
