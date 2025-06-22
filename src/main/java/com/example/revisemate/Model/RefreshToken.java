package com.example.revisemate.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder; // <-- Make sure this import is present
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @OneToOne // One-to-one relationship with User
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // You don't need to manually write constructor, getters/setters, equals/hashCode, toString if using Lombok @Data, @AllArgsConstructor, @NoArgsConstructor, @Builder
    // However, if you add custom logic or specific constructors, you might omit some Lombok annotations.
    // For a simple data model, the annotations above are sufficient.
}