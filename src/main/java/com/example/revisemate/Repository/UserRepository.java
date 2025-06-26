package com.example.revisemate.Repository;

import com.example.revisemate.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// UserRepository.java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}

