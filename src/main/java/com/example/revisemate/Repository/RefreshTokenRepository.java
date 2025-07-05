package com.example.revisemate.Repository;

import com.example.revisemate.Model.RefreshToken;
import com.example.revisemate.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long userId); // ✅ Add this line
    int deleteByUser(User user);
}
