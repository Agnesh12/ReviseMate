package com.example.revisemate.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

import static javax.crypto.Cipher.SECRET_KEY;

@Service
public class JwtService {
    public String generateToken(String  username) {
        @Value("${jwt.secret}")
        private String SECRET_KEY;

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    public String extractToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public boolean isTokenValid(String token, String username) {
        final String extractedName = extractToken(token);
        return extractedName.equals(username) && !isTokenExpired(token);

    }
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims:: getExpiration);
    }
}
