package com.example.revisemate.Service;

import com.example.revisemate.Model.RefreshToken;
import com.example.revisemate.Model.User;
import com.example.revisemate.Repository.RefreshTokenRepository;
import com.example.revisemate.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${refresh.token.duration.ms}")
    private Long refreshTokenDurationMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username.toLowerCase().trim())
                .orElseThrow(() -> new RuntimeException("User not found for refresh token creation: " + username));

        Optional<RefreshToken> existingTokenOptional = refreshTokenRepository.findByUser(user);

        RefreshToken token;
        if (existingTokenOptional.isPresent()) {
            token = existingTokenOptional.get();
            token.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            token.setToken(UUID.randomUUID().toString()); // Generate a new token string
        } else {
            token = RefreshToken.builder()
                    .user(user)
                    .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                    .token(UUID.randomUUID().toString())
                    .build();
        }
        return refreshTokenRepository.save(token);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);
    }

    @Transactional
    public void deleteToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}