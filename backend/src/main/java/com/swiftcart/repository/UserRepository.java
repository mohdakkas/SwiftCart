package com.swiftcart.repository;

import com.swiftcart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPasswordResetToken(String passwordResetToken);
    Optional<User> findByEmailVerificationToken(String emailVerificationToken);
    boolean existsByEmail(String email);
}
