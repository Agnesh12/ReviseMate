package com.example.revisemate.Service;

import com.example.revisemate.Exception.TokenRefreshException;
import com.example.revisemate.Model.RefreshToken;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.RefreshTokenRepository;
import com.example.revisemate.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${refresh.token.duration.ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    /* ────────────────────────────────────────── *
     *  look‑up helpers
     * ────────────────────────────────────────── */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /* ────────────────────────────────────────── *
     *  create OR update a refresh‑token for user
     *  (one‑token‑per‑user constraint)
     * ────────────────────────────────────────── */
    public RefreshToken createRefreshToken(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        /* upsert logic */
        RefreshToken refreshToken = refreshTokenRepository
                .findByUserId(userId)
                .orElseGet(RefreshToken::new);   // empty instance if none exists

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        return refreshTokenRepository.save(refreshToken);
    }

    /* ────────────────────────────────────────── *
     *  verify token still valid or throw
     * ────────────────────────────────────────── */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token expired. Please login again.");
        }
        return token;
    }

    /* ────────────────────────────────────────── *
     *  delete all tokens for a user (e.g., logout‑all)
     * ────────────────────────────────────────── */
    @Transactional
    public int deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return refreshTokenRepository.deleteByUser(user);
    }
}
